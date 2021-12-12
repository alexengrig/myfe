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

import dev.alexengrig.myfe.converter.ContextFTPFile2FtpDirectoryConverter;
import dev.alexengrig.myfe.converter.ContextFTPFile2FtpPathConverter;
import dev.alexengrig.myfe.converter.Converter;
import dev.alexengrig.myfe.domain.ContextFTPFile;
import dev.alexengrig.myfe.domain.FtpDirectory;
import dev.alexengrig.myfe.domain.FtpPath;
import dev.alexengrig.myfe.exception.FTPClientIOException;
import dev.alexengrig.myfe.util.logging.LazyLogger;
import dev.alexengrig.myfe.util.logging.LazyLoggerFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper of {@link FTPClient}.
 */
public class CommonsFtpClient implements FtpClient {

    private static final LazyLogger LOGGER = LazyLoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String SEPARATOR = "/";
    private static final List<FtpDirectory> ROOT_DIRECTORIES = Collections.singletonList(new FtpDirectory("/", "/"));

    private final FTPClient client;
    private final Converter<ContextFTPFile, FtpPath> pathConverter;
    private final Converter<ContextFTPFile, FtpDirectory> directoryConverter;

    public CommonsFtpClient() {
        this(//TODO: Get from context
                new FTPClient(),
                new ContextFTPFile2FtpPathConverter(),
                new ContextFTPFile2FtpDirectoryConverter());
    }

    protected CommonsFtpClient(
            FTPClient client,
            Converter<ContextFTPFile, FtpPath> pathConverter,
            Converter<ContextFTPFile, FtpDirectory> directoryConverter) {
        this.client = client;
        this.pathConverter = pathConverter;
        this.directoryConverter = directoryConverter;
    }

    @Override
    public void connect(String host, int port) throws FTPClientIOException {
        LOGGER.trace("Start to connect to {}:{}", host, port);
        try {
            client.connect(host, port);
            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                FTPClientIOException exception = new FTPClientIOException(client);
                client.disconnect();
                throw exception;
            }
        } catch (IOException e) {
            throw new FTPClientIOException(client, e);
        }
    }

    @Override
    public boolean isConnected() {
        return client.isConnected();
    }

    @Override
    public void login(String username, char[] password) throws IOException {
        login(username, new String(password));
    }

    @Override
    public void login(String username, String password) throws IOException {
        LOGGER.trace("Start to login");
        boolean isLogged = client.login(username, password);
        if (!isLogged) {
            throw new FTPClientIOException(client);
        }
    }

    @Override
    public void disconnect() throws IOException {
        LOGGER.trace("Start to disconnect");
        client.disconnect();
    }

    @Override
    public List<FtpDirectory> listRootDirectories() {
        return ROOT_DIRECTORIES;
    }

    @Override
    public List<FtpDirectory> listSubdirectories(String path) throws IOException {
        LOGGER.trace("Start to list subdirectories: {}", path);
        FTPFile[] files = client.listFiles(path, FTPFile::isDirectory);
        return Arrays.stream(files)
                .map(ContextFTPFile.factory(path, SEPARATOR))
                .map(directoryConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<FtpPath> listChildren(String path) throws IOException {
        LOGGER.trace("Start to list children: {}", path);
        FTPFile[] files = client.listFiles(path);
        return Arrays.stream(files)
                .map(ContextFTPFile.factory(path, SEPARATOR))
                .map(pathConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public InputStream retrieveFileStream(String path) throws IOException {
        LOGGER.trace("Start to retrieve file stream: {}", path);
        InputStream inputStream = client.retrieveFileStream(path);
        if (inputStream == null) {
            throw new FTPClientIOException(client);
        }
        return inputStream;
    }

    @Override
    public void close() throws IOException {
        LOGGER.trace("Start to close");
        if (client.isConnected()) {
            disconnect();
        }
    }

}
