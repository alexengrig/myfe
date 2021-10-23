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

import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyFile;
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

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    //TODO: Create new abstraction for FTP client
    private final ThreadLocal<FTPClient> clientHolder;

    public FTPPathRepository(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.clientHolder = ThreadLocal.withInitial(this::initClient);
        FTPClient testClient = initClient();
        testClient(testClient);
    }

    private FTPClient initClient() {
        FTPClient client = new FTPClient();
        client.setConnectTimeout(5_000);
        return client;
    }

    private void testClient(FTPClient client) {
        prepareClient(client);
        completeClient(client);
    }

    private void prepareClient(FTPClient client) {
        try {
            client.connect(host, port);
            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                LOGGER.warn("Could not connect to: {}:{}", host, port);
                throw new IllegalArgumentException("Could not connect to: " + host + ":" + port);
            }
            client.enterLocalPassiveMode();
            if (!client.login(username, password)) {
                LOGGER.warn("Could not login to \"{}:{}\" as user: {}", host, port, username);
                throw new IllegalArgumentException("Could not login to \"" + host + ":" + port + "\" as user: " + username);
            }
        } catch (IOException e) {
            LOGGER.error("Exception of preparing FTP client for: {}:{}", host, port);
            throw new UncheckedIOException("Exception of preparing FTP client for: " + host + ":" + port, e);
        }
    }

    private void completeClient(FTPClient client) {
        try {
            client.logout();
            client.disconnect();
        } catch (IOException e) {
            LOGGER.error("Exception of completing FTP client for: {}:{}", host, port, e);
            throw new UncheckedIOException("Exception of completing FTP client for: " + host + ":" + port, e);
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
            LOGGER.debug("Start getting root directories \"{}:{}\"", host, port);
            FTPFile[] ftpFiles = client.listFiles();
            LOGGER.debug("Got root directories {}:{}", host, port);
            List<MyDirectory> result = Arrays.stream(ftpFiles)
                    .map(f -> new MyDirectory("/" + f.getName(), f.getName()))
                    .collect(Collectors.toList());
            LOGGER.debug("Finished getting root directories \"{}:{}\": {} directory(-ies)", host, port, result.size());
            return result;
        } catch (IOException e) {
            LOGGER.debug("Exception of getting root directories \"{}:{}\"", host, port, e);
            throw new UncheckedIOException("Exception of getting root directories \"" + host + ":" + port + "\"", e);
        } finally {
            completeClient(client);
        }
    }

    @Override
    public List<MyPath> getChildren(String directoryPath) {
        FTPClient client = getPreparedClient();
        try {
            LOGGER.debug("Start getting children for: {}:{}{}", host, port, directoryPath);
            FTPFile[] ftpFiles = client.listFiles(directoryPath);
            LOGGER.debug("Got children for: {}:{}{}", host, port, directoryPath);
            List<MyPath> result = Arrays.stream(ftpFiles)
                    .map(f -> f.isDirectory()
                            ? new MyDirectory(String.join("/", directoryPath, f.getName()), f.getName())
                            : new MyFile(String.join("/", directoryPath, f.getName()), f.getName()))
                    .collect(Collectors.toList());
            LOGGER.debug("Finished getting children for \"{}:{}{}\": {} child(-ren)", host, port, directoryPath, result.size());
            return result;
        } catch (IOException e) {
            LOGGER.error("Exception of getting children for: {}:{}{}", host, port, directoryPath);
            throw new UncheckedIOException("Exception of getting children for: " + host + ":" + port + directoryPath, e);
        } finally {
            completeClient(client);
        }
    }

    @Override
    public List<MyDirectory> getSubdirectories(String directoryPath) {
        FTPClient client = getPreparedClient();
        try {
            LOGGER.debug("Start getting subdirectories for: {}:{}{}", host, port, directoryPath);
            FTPFile[] ftpFiles = client.listFiles(directoryPath);
            LOGGER.debug("Got subdirectories for: {}:{}{}", host, port, directoryPath);
            List<MyDirectory> result = Arrays.stream(ftpFiles)
                    .filter(FTPFile::isDirectory)
                    .map(f -> new MyDirectory(String.join("/", directoryPath, f.getName()), f.getName()))
                    .collect(Collectors.toList());
            LOGGER.debug("Finished getting subdirectories for \"{}:{}{}\": {} subdirectory(-ies)", host, port, directoryPath, result.size());
            return result;
        } catch (IOException e) {
            LOGGER.debug("Exception of getting subdirectories for: {}:{}{}", host, port, directoryPath, e);
            throw new UncheckedIOException("Exception of getting subdirectories for: " + host + ":" + port + directoryPath, e);
        } finally {
            completeClient(client);
        }
    }

    @Override
    public Stream<String> readByLine(String filePath) {
        FTPClient client = getPreparedClient();
        try {
            LOGGER.debug("Start reading by line: {}:{}{}", host, port, filePath);
            InputStream inputStream = client.retrieveFileStream(filePath);
            LOGGER.debug("Got input stream for reading by line: {}:{}{}", host, port, filePath);
            Scanner scanner = new Scanner(inputStream);
            Spliterator<String> spliterator = Spliterators.spliteratorUnknownSize(
                    scanner, Spliterator.NONNULL | Spliterator.IMMUTABLE | Spliterator.ORDERED);
            Stream<String> result = StreamSupport.stream(spliterator, false)
                    .onClose(() -> LOGGER.debug("Finished reading by line: {}:{}{}", host, port, filePath));
            LOGGER.debug("Return stream of reading by line: {}:{}{}", host, port, filePath);
            return CloseOnTerminalOperationStreams.wrap(result);
        } catch (Exception e) {
            LOGGER.error("Exception of reading by line: {}:{}{}", host, port, filePath, e);
            throw new RuntimeException("Exception of reading by line: " + host + ":" + port + filePath, e);
        } finally {
            completeClient(client);
        }
    }

}
