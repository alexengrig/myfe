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

import dev.alexengrig.myfe.WithUnixFtpServer;
import dev.alexengrig.myfe.config.FtpConnectionConfig;
import dev.alexengrig.myfe.converter.Path2FeDirectoryConverter;
import dev.alexengrig.myfe.converter.Path2FePathConverter;
import dev.alexengrig.myfe.domain.FeDirectory;
import dev.alexengrig.myfe.domain.FePath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class FtpFileSystemPathRepositoryIntegrationTest extends WithUnixFtpServer {

    FtpConnectionConfig config;
    Path2FeDirectoryConverter directoryConverter;
    Path2FePathConverter pathConverter;

    FtpFileSystemPathRepository repository;

    @BeforeEach
    void beforeEach() {
        setup();
        config = new FtpConnectionConfig(host, port, username, password.toCharArray());
        directoryConverter = new Path2FeDirectoryConverter();
        pathConverter = new Path2FePathConverter();
        repository = new FtpFileSystemPathRepository(config, directoryConverter, pathConverter);
    }

    @AfterEach
    void afterEach() throws Exception {
        repository.close();
        tearDown();
    }

    @Test
    void should_return_rootDirectories() {
        // run
        List<FeDirectory> rootDirectories = repository.getRootDirectories();
        // check
        Set<String> expectedPaths = Collections.singleton("/");
        Set<String> actualPaths = rootDirectories.stream()
                .map(FeDirectory::getPath)
                .collect(Collectors.toSet());
        assertIterableEquals(expectedPaths, actualPaths, "Paths of root directories");
    }

    @Disabled("Don't work - isDirectory")
    @Test
    void should_return_children() {
        addDirectory("/pub");
        String directoryPath = "/pub/subdirectory";
        addDirectory(directoryPath);
        String filePath = "/pub/file.this";
        addFile(filePath);
        // run
        List<FePath> children = repository.getChildren("/pub");
        // check
        Set<String> expectedPaths = Set.of(directoryPath, filePath);
        Set<String> actualPaths = children.stream()
                .map(FePath::getPath)
                .collect(Collectors.toSet());
        assertIterableEquals(expectedPaths, actualPaths, "Paths of children");
    }

    @Disabled("Don't work - isDirectory")
    @Test
    void should_return_subdirectories() {
        addDirectory("/pub");
        String directoryPath = "/pub/subdirectory";
        addDirectory(directoryPath);
        String filePath = "/pub/file.this";
        addFile(filePath);
        // run
        List<FeDirectory> subdirectories = repository.getSubdirectories("/pub");
        // check
        Set<String> expectedPaths = Set.of(directoryPath);
        Set<String> actualPaths = subdirectories.stream()
                .map(FeDirectory::getPath)
                .collect(Collectors.toSet());
        assertIterableEquals(expectedPaths, actualPaths, "Paths of subdirectories");
    }

    @Disabled("Don't read")
    @Test
    void should_read_batch() {
        // setup
        String fileSource = "Test source";
        String filePath = "/pub/file.this";
        addFile(filePath, fileSource);
        int batchSize = 4;
        // run
        String batch = repository.readBatch(filePath, batchSize);
        // check
        assertEquals(fileSource.substring(0, batchSize), batch, "Batch");
    }

    @Disabled("Don't read")
    @Test
    void should_read_batches() {
        // setup
        String fileSource = "Test source";
        String filePath = "/pub/file.this";
        addFile(filePath, fileSource);
        int batchSize = 4;
        int numberOfBatches = 2;
        // run
        Stream<String> batchStream = repository.readInBatches(filePath, batchSize, numberOfBatches);
        String batches = batchStream.collect(Collectors.joining());
        // check
        assertEquals(fileSource.substring(0, batchSize * numberOfBatches), batches, "Batches");
    }

    @Test
    void should_read_allBytes() {
        // setup
        byte[] expectedBytes = {1, 2, 3};
        String fileSource = new String(expectedBytes);
        String filePath = "/pub/file.this";
        addFile(filePath, fileSource);
        // run
        byte[] bytes = repository.readAllBytes(filePath);
        // check
        assertArrayEquals(expectedBytes, bytes, "Bytes");
    }

}