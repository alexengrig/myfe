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

import dev.alexengrig.myfe.client.MyFtpClient;
import dev.alexengrig.myfe.client.MyFtpClientFactory;
import dev.alexengrig.myfe.converter.Converter;
import dev.alexengrig.myfe.converter.MyFtpDirectory2MyDirectoryConverter;
import dev.alexengrig.myfe.converter.MyFtpPath2MyPathConverter;
import dev.alexengrig.myfe.exception.MyPathRepositoryException;
import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyFtpDirectory;
import dev.alexengrig.myfe.model.MyFtpPath;
import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.util.CloseOnTerminalOperationStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterator.NONNULL;
import static java.util.Spliterator.ORDERED;

/**
 * {@link MyFtpClient}-based implementation.
 */
//TODO: Create FileSystem of FTP server for FileSystemPathRepository
public class FtpPathRepository implements MyPathRepository {

    //TODO: Use lazy-arguments logger
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Converter<MyFtpPath, MyPath> pathConverter;
    private final Converter<MyFtpDirectory, MyDirectory> directoryConverter;
    private final MyFtpClientFactory clientFactory;

    public FtpPathRepository(MyFtpClientFactory clientFactory) {
        this(//TODO: Get from context
                new MyFtpPath2MyPathConverter(),
                new MyFtpDirectory2MyDirectoryConverter(),
                clientFactory);
    }

    public FtpPathRepository(
            Converter<MyFtpPath, MyPath> pathConverter,
            Converter<MyFtpDirectory, MyDirectory> directoryConverter,
            MyFtpClientFactory clientFactory) {
        this.pathConverter = pathConverter;
        this.directoryConverter = directoryConverter;
        this.clientFactory = clientFactory;
    }

    @Override
    public void close() throws Exception {
        clientFactory.close();
    }

    @Override
    public List<MyDirectory> getRootDirectories() {
        LOGGER.debug("Start getting root directories \"{}\"",
                clientFactory.getConnectionInfo());
        try (MyFtpClient client = clientFactory.createClient()) {
            Stream<MyFtpDirectory> ftpDirectories = client.subdirectories("/");
            List<MyDirectory> result = ftpDirectories
                    .map(directoryConverter::convert)
                    .collect(Collectors.toList());
            LOGGER.debug("Finished getting root directories \"{}\", number of directories: {}",
                    clientFactory.getConnectionInfo(), result.size());
            return result;
        } catch (Exception e) {
            LOGGER.error("Exception of getting root directories \"{}\"",
                    clientFactory.getConnectionInfo(), e);
            throw new MyPathRepositoryException("Exception of getting root directories \"" +
                    clientFactory.getConnectionInfo() + "\"",
                    e);
        }
    }

    @Override
    public List<MyPath> getChildren(String directoryPath) {
        LOGGER.debug("Start getting children \"{}\" for: {}",
                clientFactory.getConnectionInfo(), directoryPath);
        try (MyFtpClient client = clientFactory.createClient()) {
            Stream<MyFtpPath> ftpPaths = client.list(directoryPath);
            List<MyPath> result = ftpPaths
                    .map(pathConverter::convert)
                    .collect(Collectors.toList());
            LOGGER.debug("Finished getting children \"{}\" in \"{}\", number of elements: {}",
                    clientFactory.getConnectionInfo(), directoryPath, result.size());
            return result;
        } catch (Exception e) {
            LOGGER.error("Exception of getting children \"{}\" for: {}",
                    clientFactory.getConnectionInfo(), directoryPath, e);
            throw new MyPathRepositoryException("Exception of getting children \"" +
                    clientFactory.getConnectionInfo() + "\" for: " +
                    directoryPath,
                    e);
        }
    }

    @Override
    public List<MyDirectory> getSubdirectories(String directoryPath) {
        LOGGER.debug("Start getting subdirectories \"{}\" for: {}",
                clientFactory.getConnectionInfo(), directoryPath);
        try (MyFtpClient client = clientFactory.createClient()) {
            Stream<MyFtpPath> ftpPaths = client.list(directoryPath);
            List<MyDirectory> result = ftpPaths
                    .filter(MyFtpPath::isDirectory)
                    .map(MyFtpPath::asDirectory)
                    .map(directoryConverter::convert)
                    .collect(Collectors.toList());
            LOGGER.debug("Finished getting subdirectories \"{}\" in \"{}\", number of directories: {}",
                    clientFactory.getConnectionInfo(), directoryPath, result.size());
            return result;
        } catch (Exception e) {
            LOGGER.error("Exception of getting subdirectories \"{}\" for: {}",
                    clientFactory.getConnectionInfo(), directoryPath, e);
            throw new MyPathRepositoryException("Exception of getting subdirectories \"" +
                    clientFactory.getConnectionInfo() + "\" for: " +
                    directoryPath,
                    e);
        }
    }

    @Override
    public String readBatch(String filePath, int batchSize) {
        LOGGER.debug("Start reading a batch of {} byte(s) \"{}\" for: {}",
                batchSize, clientFactory.getConnectionInfo(), filePath);
        try (MyFtpClient client = clientFactory.createClient()) {
            InputStream inputStream = client.inputStream(filePath);
            byte[] buffer = new byte[batchSize];
            int count = inputStream.read(buffer);
            String result;
            if (count != -1) {
                result = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(buffer, 0, count)).toString();
            } else {
                result = "";
            }
            LOGGER.debug("Finished reading a batch of {} byte(s) \"{}\" in \"{}\", number of characters: {}",
                    batchSize, clientFactory.getConnectionInfo(), filePath, result.length());
            return result;
        } catch (Exception e) {
            LOGGER.error("Exception of reading a batch of {} byte(s) \"{}\" for: {}",
                    batchSize, clientFactory.getConnectionInfo(), filePath, e);
            throw new MyPathRepositoryException("Exception of reading a batch of " +
                    batchSize + " byte(s) \"" +
                    clientFactory.getConnectionInfo() + "\" for: " +
                    filePath,
                    e);
        }
    }

    @Override
    public Stream<String> readInBatches(String filePath, int batchSize, int numberOfBatches) {
        LOGGER.debug("Start reading {} batch(es) of {} byte(s) \"{}\" for: {}",
                numberOfBatches, batchSize, clientFactory.getConnectionInfo(), filePath);
        try (MyFtpClient client = clientFactory.createClient()) {
            InputStream inputStream = client.inputStream(filePath);
            Iterator<String> iterator = new Scanner(inputStream); //FIXME: Replace with custom iterator
            Spliterator<String> spliterator = Spliterators.spliteratorUnknownSize(iterator, NONNULL | ORDERED);
            Stream<String> result = StreamSupport.stream(spliterator, false)
                    .onClose(() -> {
                        try {
                            inputStream.close();
                            LOGGER.debug("Closed InputStream of reading {} batch(es) of {} byte(s) \"{}\" for: {}",
                                    numberOfBatches, batchSize, clientFactory.getConnectionInfo(), filePath);
                        } catch (IOException e) {
                            LOGGER.error("Exception of closing InputStream of reading {} batch(es) of {} byte(s) \"{}\" for: {}",
                                    numberOfBatches, batchSize, clientFactory.getConnectionInfo(), filePath, e);
                            throw new UncheckedIOException("Exception of closing InputStream of reading " +
                                    numberOfBatches + " batch(es) of " +
                                    batchSize + " byte(s) \"" +
                                    clientFactory.getConnectionInfo() + "\" for: " +
                                    filePath,
                                    e);
                        }
                    });
            LOGGER.debug("Return Stream of reading {} batch(es) of {} byte(s) \"{}\" for: {}",
                    numberOfBatches, batchSize, clientFactory.getConnectionInfo(), filePath);
            return CloseOnTerminalOperationStreams.wrap(result);
        } catch (Exception e) {
            LOGGER.error("Exception of reading {} batch(es) of {} byte(s) \"{}\" for: {}",
                    numberOfBatches, batchSize, clientFactory.getConnectionInfo(), filePath, e);
            throw new MyPathRepositoryException("Exception of reading " +
                    numberOfBatches + " batch(es) of " +
                    batchSize + " byte(s) \"" +
                    clientFactory.getConnectionInfo() + "\" for: " +
                    filePath,
                    e);
        }
    }

}
