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

import dev.alexengrig.myfe.client.CommonsFtpClientManager;
import dev.alexengrig.myfe.client.FtpClient;
import dev.alexengrig.myfe.client.FtpClientManager;
import dev.alexengrig.myfe.converter.Converter;
import dev.alexengrig.myfe.domain.FeDirectory;
import dev.alexengrig.myfe.domain.FePath;
import dev.alexengrig.myfe.domain.FtpConnectionConfig;
import dev.alexengrig.myfe.domain.FtpDirectory;
import dev.alexengrig.myfe.domain.FtpPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FtpClientPathRepository implements FePathRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FtpClientManager<FtpClient> clientManager;
    private final Converter<FtpPath, FePath> path2pathConverter;
    private final Converter<FtpDirectory, FeDirectory> directory2directoryConverter;

    public FtpClientPathRepository(
            FtpConnectionConfig config,
            Converter<FtpDirectory, FeDirectory> directory2directoryConverter,
            Converter<FtpPath, FePath> path2pathConverter
    ) {
        this(//TODO: Get from context
                new CommonsFtpClientManager(config),
                directory2directoryConverter,
                path2pathConverter);
    }

    @SuppressWarnings("unchecked")
    public FtpClientPathRepository(
            FtpClientManager<? extends FtpClient> ftpClientManager,
            Converter<FtpDirectory, FeDirectory> directory2directoryConverter,
            Converter<FtpPath, FePath> path2pathConverter) {
        this.clientManager = (FtpClientManager<FtpClient>) ftpClientManager;
        this.path2pathConverter = path2pathConverter;
        this.directory2directoryConverter = directory2directoryConverter;
    }

    @Override
    public List<FeDirectory> getRootDirectories() {
        LOGGER.debug("Getting root directories");
        try (FtpClient client = clientManager.getClient()) {
            List<FtpDirectory> directories = client.listRootDirectories();
            return directories.stream()
                    .map(directory2directoryConverter::convert)
                    .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    public List<FePath> getChildren(String directoryPath) {
        LOGGER.debug("Getting children: {}", directoryPath);
        try (FtpClient client = clientManager.getClient()) {
            List<FtpPath> children = client.listChildren(directoryPath);
            return children.stream()
                    .map(path2pathConverter::convert)
                    .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    public List<FeDirectory> getSubdirectories(String directoryPath) {
        LOGGER.debug("Getting subdirectories: {}", directoryPath);
        try (FtpClient client = clientManager.getClient()) {
            List<FtpDirectory> subdirectories = client.listSubdirectories(directoryPath);
            return subdirectories.stream()
                    .map(directory2directoryConverter::convert)
                    .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    public String readBatch(String filePath, int batchSize) {
        LOGGER.debug("Reading a batch: {} - {} bytes", filePath, batchSize);
        try (FtpClient client = clientManager.getClient();
             InputStream in = client.retrieveFileStream(filePath)) {
            byte[] buffer = new byte[batchSize];
            int count = in.read(buffer);
            if (count > 0) {
                return new String(buffer, StandardCharsets.UTF_8);
            } else {
                return "";
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    public Stream<String> readInBatches(String filePath, int batchSize, int numberOfBatches) {
        throw new UnsupportedOperationException(); //TODO: Implement?
    }

    @Override
    public byte[] readAllBytes(String filePath) {
        LOGGER.debug("Reading all bytes: {}", filePath);
        try (FtpClient client = clientManager.getClient();
             InputStream in = client.retrieveFileStream(filePath)) {
            return in.readAllBytes();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    public void close() throws Exception {
        clientManager.close();
    }

}
