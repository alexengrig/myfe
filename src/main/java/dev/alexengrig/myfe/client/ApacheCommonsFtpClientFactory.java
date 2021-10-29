/*
 * Copyright 2021 Alexengrig Dev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.alexengrig.myfe.client;

import dev.alexengrig.myfe.config.FTPConnectionConfig;
import dev.alexengrig.myfe.converter.ContextFTPFile2MyFtpDirectoryConverter;
import dev.alexengrig.myfe.converter.ContextFTPFile2MyPathConverter;
import dev.alexengrig.myfe.converter.Converter;
import dev.alexengrig.myfe.model.ContextFTPFile;
import dev.alexengrig.myfe.model.MyFtpDirectory;
import dev.alexengrig.myfe.model.MyFtpPath;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * {@link FTPClient}-based implementation.
 *
 * @implSpec Methods of FTP client are blocking.
 */
public class ApacheCommonsFtpClientFactory implements MyFtpClientFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int DEFAULT_MAX_NUMBER_OF_CONNECTIONS = 10; //FIXME: Select the best number
    private static final int CONNECTION_TIMEOUT_IN_MILLIS = 7_000;

    //TODO: Windows, OK?
    private static final String ROOT_PATH = "/";
    private static final String SEPARATOR = "/";

    private final Converter<ContextFTPFile, MyFtpPath> pathConverter;
    private final Converter<ContextFTPFile, MyFtpDirectory> directoryConverter;
    private final FTPConnectionConfig connectionConfig;
    private final Semaphore connectionPermits;

    private transient String connectionInfo;

    public ApacheCommonsFtpClientFactory(FTPConnectionConfig connectionConfig) {
        //TODO: Get from context
        this(
                new ContextFTPFile2MyPathConverter(),
                new ContextFTPFile2MyFtpDirectoryConverter(),
                connectionConfig,
                DEFAULT_MAX_NUMBER_OF_CONNECTIONS);
    }

    public ApacheCommonsFtpClientFactory(
            Converter<ContextFTPFile, MyFtpPath> pathConverter,
            Converter<ContextFTPFile, MyFtpDirectory> directoryConverter,
            FTPConnectionConfig connectionConfig, int maxNumberOfConnections) {
        this.pathConverter = pathConverter;
        this.directoryConverter = directoryConverter;
        this.connectionConfig = connectionConfig;
        this.connectionPermits = new Semaphore(maxNumberOfConnections, true);
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public String getConnectionInfo() {
        if (connectionInfo == null) {
            connectionInfo = connectionConfig.getHost() + ":" + connectionConfig.getPort() +
                    " by " + connectionConfig.getUsername();
        }
        return connectionInfo;
    }

    @Override
    public MyFtpClient createClient() {
        return new ClientAdapter();
    }

    private void acquireConnection() {
        try {
            connectionPermits.acquire();
            LOGGER.trace("Acquired connection, available: {}", connectionPermits.availablePermits());
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted"); //FIXME: Create exception
        }
    }

    private void releaseConnection() {
        connectionPermits.release();
        LOGGER.trace("Released connection, available: {}", connectionPermits.availablePermits());
    }

    @FunctionalInterface
    private interface IOCallable<T> {

        T call() throws IOException;

    }

    @FunctionalInterface
    private interface IORunnable {

        void run() throws IOException;

    }

    private class ClientAdapter implements MyFtpClient {

        private final FTPClient client;

        private transient boolean acquiredConnection;

        private ClientAdapter() {
            this.client = new FTPClient();
            configure();
        }

        private void configure() {
            client.setConnectTimeout(CONNECTION_TIMEOUT_IN_MILLIS);
            client.enterLocalPassiveMode();
        }

        private void prepare() {
            String host = connectionConfig.getHost();
            int port = connectionConfig.getPort();
            runInUncheckedIOException(() -> client.connect(host, port));
            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                LOGGER.warn("Could not connect to: {}:{}", host, port);
                throw new IllegalArgumentException("Could not connect to: ");
            }
            String username = connectionConfig.getUsername();
            boolean successfulLogin = callInUncheckedIOException(() ->
                    client.login(username, new String(connectionConfig.getPassword())));
            if (!successfulLogin) {
                LOGGER.warn("Could not login to \"{}:{}\" as user: {}", host, port, username);
                throw new IllegalArgumentException("Could not login to \"" +
                        host + ":" + port + "\" as user: " + username);
            }
        }

        private void prepareIfNeed() {
            if (!acquiredConnection) {
                acquireConnection();
                acquiredConnection = true;
                try {
                    prepare();
                } catch (Exception e) {
                    acquiredConnection = true;
                    releaseConnection();
                    throw e;
                }
            }
        }

        private void complete() {
            runInUncheckedIOException(client::logout);
            runInUncheckedIOException(client::disconnect);
        }

        @Override
        public void close() {
            try {
                complete();
            } finally {
                releaseConnection();
            }
        }

        @Override
        public Stream<MyFtpPath> list() {
            return list(ROOT_PATH);
        }

        @Override
        public Stream<MyFtpPath> list(String directoryPath) {
            prepareIfNeed();
            FTPFile[] ftpFiles = callInUncheckedIOException(() -> client.listFiles(directoryPath));
            return convertToPaths(directoryPath, ftpFiles);
        }

        @Override
        public Stream<MyFtpDirectory> subdirectories() {
            return subdirectories(ROOT_PATH);
        }

        @Override
        public Stream<MyFtpDirectory> subdirectories(String directoryPath) {
            prepareIfNeed();
            FTPFile[] ftpFiles = callInUncheckedIOException(() ->
                    client.listFiles(directoryPath, FTPFile::isDirectory));
            return convertToDirectories(directoryPath, ftpFiles);
        }

        @Override
        public InputStream inputStream(String path) {
            prepareIfNeed();
            return callInUncheckedIOException(() -> client.retrieveFileStream(path));
        }

        private void runInUncheckedIOException(IORunnable runnable) {
            try {
                runnable.run();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        private <T> T callInUncheckedIOException(IOCallable<T> callable) {
            try {
                return callable.call();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        private Stream<MyFtpPath> convertToPaths(String directoryPath, FTPFile[] ftpFiles) {
            return Arrays.stream(ftpFiles)
                    .map(this.withContext(directoryPath))
                    .map(pathConverter::convert);
        }

        private Stream<MyFtpDirectory> convertToDirectories(String directoryPath, FTPFile[] ftpFiles) {
            return Arrays.stream(ftpFiles)
                    .map(this.withContext(directoryPath))
                    .map(directoryConverter::convert);
        }

        private Function<FTPFile, ContextFTPFile> withContext(String parentPath) {
            String parent = ROOT_PATH.equals(parentPath) ? "" : parentPath;
            return ftpFile -> new ContextFTPFile(parent, SEPARATOR, ftpFile);
        }

    }

}
