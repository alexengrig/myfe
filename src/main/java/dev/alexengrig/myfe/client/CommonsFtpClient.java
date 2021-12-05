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
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommonsFtpClient implements FtpClient {

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
    public void connect(String host, int port) throws IOException {
        client.connect(host, port);
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
        boolean isLogged = client.login(username, password);
        if (!isLogged) {
            int code = client.getReplyCode();
            String message = client.getReplyString();
            throw new IOException(code + " " + message);
        }
    }

    @Override
    public void disconnect() throws IOException {
        client.disconnect();
    }

    @Override
    public List<FtpDirectory> listRootDirectories() {
        return ROOT_DIRECTORIES;
    }

    @Override
    public List<FtpDirectory> listSubdirectories(String path) throws IOException {
        FTPFile[] files = client.listFiles(path, FTPFile::isDirectory);
        return Arrays.stream(files)
                .map(ContextFTPFile.factory(path, SEPARATOR))
                .map(directoryConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<FtpPath> listChildren(String path) throws IOException {
        FTPFile[] files = client.listFiles(path);
        return Arrays.stream(files)
                .map(ContextFTPFile.factory(path, SEPARATOR))
                .map(pathConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public InputStream retrieveFileStream(String path) throws IOException {
        InputStream inputStream = client.retrieveFileStream(path);
        if (inputStream == null) {
            int code = client.getReplyCode();
            String message = client.getReplyString();
            throw new IOException(code + " " + message);
        }
        return inputStream;
    }

    @Override
    public void close() throws IOException {
        disconnect();
    }

}
