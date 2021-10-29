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
import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyFtpDirectory;
import dev.alexengrig.myfe.model.MyFtpPath;
import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.util.CloseOnTerminalOperationStreams;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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

    private final MyFtpClientFactory clientFactory;

    public FTPPathRepository(MyFtpClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public void close() throws Exception {
        clientFactory.close();
    }

    @Override
    public List<MyDirectory> getRootDirectories() {
        try (MyFtpClient client = clientFactory.createClient()) {
            Stream<MyFtpDirectory> ftpDirectories = client.subdirectories("/");
            return ftpDirectories
                    .map(f -> new MyDirectory("/" + f.getName(), f.getName())) //FIXME: Converter
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Exception of getting root directories \"" + "\"", e);
        }
    }

    @Override
    public List<MyPath> getChildren(String directoryPath) {
        try (MyFtpClient client = clientFactory.createClient()) {
            Stream<MyFtpPath> ftpFiles = client.list(directoryPath);
            return ftpFiles
                    .map(f -> MyPath.of(String.join("/", directoryPath, f.getName()), f.getName(), f.isDirectory())) //FIXME: Converter
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Exception of getting children for: " + directoryPath, e);
        }
    }

    @Override
    public List<MyDirectory> getSubdirectories(String directoryPath) {
        try (MyFtpClient client = clientFactory.createClient()) {
            Stream<MyFtpPath> ftpPaths = client.list(directoryPath);
            return ftpPaths
                    .filter(MyFtpPath::isDirectory)
                    .map(f -> new MyDirectory(String.join("/", directoryPath, f.getName()), f.getName())) //FIXME: Converter
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Exception of getting subdirectories for: " + directoryPath, e);
        }
    }

    @Override
    public String readBatch(String filePath, int batchSize) {
        try (MyFtpClient client = clientFactory.createClient()) {
            InputStream inputStream = client.inputStream(filePath);
            byte[] buffer = new byte[batchSize];
            int count = inputStream.read(buffer);
            if (count != -1) {
                return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(buffer, 0, count)).toString();
            }
            return "";
        } catch (Exception e) {
            throw new RuntimeException("Exception of reading a batch: "
                                       + filePath +
                                       " - " + batchSize + " bytes", e);
        }
    }

    @Override
    public Stream<String> readInBatches(String filePath, int batchSize, int numberOfBatches) {
        try (MyFtpClient client = clientFactory.createClient()) {
            InputStream inputStream = client.inputStream(filePath);
            Scanner scanner = new Scanner(inputStream);
            Spliterator<String> spliterator = Spliterators.spliteratorUnknownSize(
                    scanner, Spliterator.NONNULL | Spliterator.IMMUTABLE | Spliterator.ORDERED);
            Stream<String> result = StreamSupport.stream(spliterator, false)
                    .onClose(() -> LOGGER.debug("Finished reading by line: {}", filePath));
            return CloseOnTerminalOperationStreams.wrap(result);
        } catch (Exception e) {
            throw new RuntimeException("Exception of reading by line: " + filePath, e);
        }
    }

}
