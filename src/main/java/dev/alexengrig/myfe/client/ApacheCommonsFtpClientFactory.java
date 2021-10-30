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
import dev.alexengrig.myfe.exception.UncheckedInterruptedException;
import dev.alexengrig.myfe.model.ContextFTPFile;
import dev.alexengrig.myfe.model.MyFtpDirectory;
import dev.alexengrig.myfe.model.MyFtpPath;
import dev.alexengrig.myfe.util.LazyLogger;
import dev.alexengrig.myfe.util.LazyLoggerFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

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

    private static final LazyLogger LOGGER = LazyLoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int DEFAULT_MAX_NUMBER_OF_CONNECTIONS = 10; //FIXME: Select the best number
    private static final int CONNECTION_TIMEOUT_IN_MILLIS = 7_000;

    //TODO: Windows, OK?
    private static final String ROOT_PATH = "/";
    private static final String SEPARATOR = "/";

    private final Converter<ContextFTPFile, MyFtpPath> pathConverter;
    private final Converter<ContextFTPFile, MyFtpDirectory> directoryConverter;
    private final FTPConnectionConfig connectionConfig;
    private final Semaphore connectionPermits;
    private final ClientAdapter clientAdapter = new ClientAdapter();

    public ApacheCommonsFtpClientFactory(FTPConnectionConfig connectionConfig) {
        this(//TODO: Get from context
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
        return connectionConfig.getInfo();
    }

    @Override
    public MyFtpClient createClient() {
        return clientAdapter;
    }

    private void acquireConnection() {
        try {
            connectionPermits.acquire();
            LOGGER.trace(m -> m.log("Acquired connection, available: {}",
                    connectionPermits.availablePermits()));
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted", e);
            Thread.currentThread().interrupt();
            throw new UncheckedInterruptedException(e);
        }
    }

    private void releaseConnection() {
        connectionPermits.release();
        LOGGER.trace(m -> m.log("Released connection, available: {}",
                connectionPermits.availablePermits()));
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

        private void prepareIfNeed() {
            if (!acquiredConnection) {
//                LOGGER.trace("Start preparing");
//                acquireConnection();
                acquiredConnection = true;
                try {
                    prepare();
                    LOGGER.trace("Finished preparing");
                } catch (Exception e) {
                    acquiredConnection = false;
//                    releaseConnection();
                    throw e;
                }
            }
        }

        private void prepare() {
            //FIXME: Remove below
            LOGGER.trace(m -> m.log("Before connect: available={}, connected={}", client.isAvailable(), client.isConnected()));
            connect();
            //FIXME: Remove below
            LOGGER.trace(m -> m.log("Before login: available={}, connected={}", client.isAvailable(), client.isConnected()));
            login();
        }

        private void connect() {
            runInUncheckedIOException(() -> client.connect(connectionConfig.getHost(), connectionConfig.getPort()));
            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                LOGGER.warn(m -> m.log("Could not connect to \"{}:{}\": {}",
                        connectionConfig.getHost(), connectionConfig.getPort(), client.getReplyString()));
                throw new IllegalArgumentException("Could not connect to \"" +
                        connectionConfig.getHost() + ":" +
                        connectionConfig.getPort() + "\": " +
                        client.getReplyString());
            }
        }

        private void login() {
            String username = connectionConfig.getUsername();
            boolean successfulLogin = callInUncheckedIOException(() ->
                    client.login(username, new String(connectionConfig.getPassword())));
            if (!successfulLogin || !FTPReply.isPositiveCompletion(client.getReplyCode())) {
                LOGGER.warn("Could not login to \"{}:{}\" as user \"{}\": {}",
                        connectionConfig.getHost(), connectionConfig.getPort(), username, client.getReplyString());
                throw new IllegalArgumentException("Could not login to \"" +
                        connectionConfig.getHost() + ":" +
                        connectionConfig.getPort() + "\" as user \"" +
                        username + "\": " +
                        client.getReplyString());
            }
        }

        @Override
        public void close() {
//            LOGGER.trace("Start completing");
            try {
//                complete();
//                LOGGER.trace("Finished completing");
            } finally {
//                releaseConnection();
//                acquiredConnection = false;
            }
        }

        private void complete() {
            if (client.isConnected()) {
                logout();
                disconnect();
            }
        }

        private void logout() {
            runInUncheckedIOException(() -> {
                boolean logout = client.logout();
                if (!logout) {
                    logout = true;
                }
            });
        }

        private void disconnect() {
            runInUncheckedIOException(client::disconnect);
        }

        @Override
        public Stream<MyFtpPath> list() {
            return list(ROOT_PATH);
        }

        @Override
        public Stream<MyFtpPath> list(String directoryPath) {
            LOGGER.trace(m -> m.log("Start getting children for: {}",
                    directoryPath));
            prepareIfNeed();
            FTPFile[] ftpFiles = callInUncheckedIOException(() -> client.listFiles(directoryPath));
            Stream<MyFtpPath> result = convertToPaths(directoryPath, ftpFiles);
            LOGGER.trace(m -> m.log("Finished getting children for: {}",
                    directoryPath));
            return result;
        }

        @Override
        public Stream<MyFtpDirectory> subdirectories() {
            return subdirectories(ROOT_PATH);
        }

        @Override
        public Stream<MyFtpDirectory> subdirectories(String directoryPath) {
            LOGGER.trace(m -> m.log("Start getting subdirectories for: {}",
                    directoryPath));
            prepareIfNeed();
            FTPFile[] ftpFiles = callInUncheckedIOException(() ->
                    client.listFiles(directoryPath, FTPFile::isDirectory));
            Stream<MyFtpDirectory> result = convertToDirectories(directoryPath, ftpFiles);
            LOGGER.trace(m -> m.log("Finished getting subdirectories for: {}",
                    directoryPath));
            return result;
        }

        @Override
        public InputStream inputStream(String path) {
            LOGGER.trace(m -> m.log("Start getting InputStream for: {}",
                    path));
            prepareIfNeed();
            InputStream result = callInUncheckedIOException(() -> client.retrieveFileStream(path));
            LOGGER.trace(m -> m.log("Finished getting InputStream for: {}",
                    path));
            return result;
        }

        private void runInUncheckedIOException(IORunnable runnable) {
            try {
                runnable.run();
            } catch (IOException e) {
                LOGGER.error("Exception of running: " + client.getReplyString(), e);
                throw new UncheckedIOException(e);
            }
        }

        private <T> T callInUncheckedIOException(IOCallable<T> callable) {
            try {
                return callable.call();
            } catch (IOException e) {
                LOGGER.error("Exception of getting: " + client.getReplyString(), e);
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
