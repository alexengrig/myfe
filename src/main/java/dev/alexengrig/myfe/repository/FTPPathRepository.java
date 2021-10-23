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

package dev.alexengrig.myfe.repository;

import dev.alexengrig.myfe.config.FTPConnectionConfig;
import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.util.CloseOnTerminalOperationStreams;
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
import java.util.List;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * {@link FTPClient}-based implementation.
 */
public class FTPPathRepository implements MyPathRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FTPConnectionConfig config;

    //TODO: Create new abstraction for FTP client
    private final ThreadLocal<FTPClient> clientHolder;

    public FTPPathRepository(FTPConnectionConfig config) {
        this.config = config;
        this.clientHolder = ThreadLocal.withInitial(this::initClient);
        testClientConnection();
    }

    private void testClientConnection() {
        FTPClient testClient = initClient();
        prepareClient(testClient);
        completeClient(testClient);
    }

    private FTPClient initClient() {
        FTPClient client = new FTPClient();
        client.setConnectTimeout(5_000);
        return client;
    }

    private void prepareClient(FTPClient client) {
        try {
            client.connect(config.getHost(), config.getPort());
            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                LOGGER.warn("Could not connect to: {}:{}", config.getHost(), config.getPort());
                throw new IllegalArgumentException("Could not connect to: " + config.getHost() + ":" + config.getPort());
            }
            client.enterLocalPassiveMode();
            if (!client.login(config.getUsername(), new String(config.getPassword()))) {
                LOGGER.warn("Could not login to \"{}:{}\" as user: {}",
                        config.getHost(), config.getPort(), config.getUsername());
                throw new IllegalArgumentException("Could not login to \"" +
                        config.getHost() + ":" + config.getPort() +
                        "\" as user: " + config.getUsername());
            }
        } catch (IOException e) {
            LOGGER.error("Exception of preparing FTP client for: {}:{}", config.getHost(), config.getPort());
            throw new UncheckedIOException("Exception of preparing FTP client for: " +
                    config.getHost() + ":" + config.getPort(), e);
        }
    }

    private void completeClient(FTPClient client) {
        try {
            client.logout();
            client.disconnect();

        } catch (IOException e) {
            LOGGER.error("Exception of completing FTP client for: {}:{}", config.getHost(), config.getPort(), e);
            throw new UncheckedIOException("Exception of completing FTP client for: " +
                    config.getHost() + ":" + config.getPort(), e);
        }
    }

    private FTPClient getPreparedClient() {
        FTPClient client = clientHolder.get();
        prepareClient(client);
        return client;
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public List<MyDirectory> getRootDirectories() {
        FTPClient client = getPreparedClient();
        try {
            LOGGER.debug("Start getting root directories \"{}:{}\"", config.getHost(), config.getPort());
            FTPFile[] ftpFiles = client.listFiles();
            LOGGER.debug("Got root directories {}:{}", config.getHost(), config.getPort());
            List<MyDirectory> result = Arrays.stream(ftpFiles)
                    .map(f -> new MyDirectory("/" + f.getName(), f.getName()))
                    .collect(Collectors.toList());
            LOGGER.debug("Finished getting root directories \"{}:{}\": {} directory(-ies)",
                    config.getHost(), config.getPort(), result.size());
            return result;
        } catch (IOException e) {
            LOGGER.debug("Exception of getting root directories \"{}:{}\"", config.getHost(), config.getPort(), e);
            throw new UncheckedIOException("Exception of getting root directories \"" +
                    config.getHost() + ":" + config.getPort() + "\"", e);
        } finally {
            completeClient(client);
        }
    }

    @Override
    public List<MyPath> getChildren(String directoryPath) {
        FTPClient client = getPreparedClient();
        try {
            LOGGER.debug("Start getting children for: {}:{}{}", config.getHost(), config.getPort(), directoryPath);
            FTPFile[] ftpFiles = client.listFiles(directoryPath);
            LOGGER.debug("Got children for: {}:{}{}", config.getHost(), config.getPort(), directoryPath);
            List<MyPath> result = Arrays.stream(ftpFiles)
                    .map(f -> MyPath.of(String.join("/", directoryPath, f.getName()), f.getName(), f.isDirectory()))
                    .collect(Collectors.toList());
            LOGGER.debug("Finished getting children for \"{}:{}{}\": {} child(-ren)",
                    config.getHost(), config.getPort(), directoryPath, result.size());
            return result;
        } catch (IOException e) {
            LOGGER.error("Exception of getting children for: {}:{}{}", config.getHost(), config.getPort(), directoryPath);
            throw new UncheckedIOException("Exception of getting children for: " +
                    config.getHost() + ":" + config.getPort() + directoryPath, e);
        } finally {
            completeClient(client);
        }
    }

    @Override
    public List<MyDirectory> getSubdirectories(String directoryPath) {
        FTPClient client = getPreparedClient();
        try {
            LOGGER.debug("Start getting subdirectories for: {}:{}{}", config.getHost(), config.getPort(), directoryPath);
            FTPFile[] ftpFiles = client.listFiles(directoryPath);
            LOGGER.debug("Got subdirectories for: {}:{}{}", config.getHost(), config.getPort(), directoryPath);
            List<MyDirectory> result = Arrays.stream(ftpFiles)
                    .filter(FTPFile::isDirectory)
                    .map(f -> new MyDirectory(String.join("/", directoryPath, f.getName()), f.getName()))
                    .collect(Collectors.toList());
            LOGGER.debug("Finished getting subdirectories for \"{}:{}{}\": {} subdirectory(-ies)",
                    config.getHost(), config.getPort(), directoryPath, result.size());
            return result;
        } catch (IOException e) {
            LOGGER.debug("Exception of getting subdirectories for: {}:{}{}", config.getHost(), config.getPort(), directoryPath, e);
            throw new UncheckedIOException("Exception of getting subdirectories for: " +
                    config.getHost() + ":" + config.getPort() + directoryPath, e);
        } finally {
            completeClient(client);
        }
    }

    @Override
    public Stream<String> readByLine(String filePath) {
        FTPClient client = getPreparedClient();
        try {
            LOGGER.debug("Start reading by line: {}:{}{}", config.getHost(), config.getPort(), filePath);
            InputStream inputStream = client.retrieveFileStream(filePath);
            LOGGER.debug("Got input stream for reading by line: {}:{}{}", config.getHost(), config.getPort(), filePath);
            Scanner scanner = new Scanner(inputStream);
            Spliterator<String> spliterator = Spliterators.spliteratorUnknownSize(
                    scanner, Spliterator.NONNULL | Spliterator.IMMUTABLE | Spliterator.ORDERED);
            Stream<String> result = StreamSupport.stream(spliterator, false)
                    .onClose(() -> LOGGER.debug("Finished reading by line: {}:{}{}", config.getHost(), config.getPort(), filePath));
            LOGGER.debug("Return stream of reading by line: {}:{}{}", config.getHost(), config.getPort(), filePath);
            return CloseOnTerminalOperationStreams.wrap(result);
        } catch (Exception e) {
            LOGGER.error("Exception of reading by line: {}:{}{}", config.getHost(), config.getPort(), filePath, e);
            throw new RuntimeException("Exception of reading by line: " + config.getHost() + ":" + config.getPort() + filePath, e);
        } finally {
            completeClient(client);
        }
    }

}
