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

import dev.alexengrig.myfe.converter.Path2FeDirectoryConverter;
import dev.alexengrig.myfe.converter.Path2FePathConverter;
import dev.alexengrig.myfe.model.FeDirectory;
import dev.alexengrig.myfe.model.FePath;
import dev.alexengrig.myfe.util.PathUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LocalFileSystemPathRepositoryIntegrationTest {

    static final FileSystem FS = FileSystems.getDefault();

    Path2FeDirectoryConverter directoryConverter;
    Path2FePathConverter pathConverter;

    LocalFileSystemPathRepository repository;

    @BeforeEach
    void beforeEach() {
        directoryConverter = new Path2FeDirectoryConverter();
        pathConverter = new Path2FePathConverter();
        repository = new LocalFileSystemPathRepository(directoryConverter, pathConverter);
    }

    @Test
    void should_return_rootDirectories() {
        // run
        List<FeDirectory> rootDirectories = repository.getRootDirectories();
        // check
        Set<String> expectedPaths = StreamSupport.stream(FS.getRootDirectories().spliterator(), false)
                .map(Path::toString)
                .collect(Collectors.toSet());
        Set<String> actualPaths = rootDirectories.stream()
                .map(FeDirectory::getPath)
                .collect(Collectors.toSet());
        assertIterableEquals(expectedPaths, actualPaths, "Paths of root directories");
    }

    @Test
    void should_return_children() throws URISyntaxException {
        // setup
        URL thisTestClassUrl = getClass().getResource(getClass().getSimpleName() + ".class");
        assertNotNull(thisTestClassUrl, "URL of this test class");
        Path packagePath = FS.provider().getPath(thisTestClassUrl.toURI()).getParent();
        // run
        List<FePath> children = repository.getChildren(packagePath.toString());
        // check
        Set<String> expectedPaths = PathUtil.getChildren(packagePath).stream()
                .map(Path::toString)
                .collect(Collectors.toSet());
        Set<String> actualPaths = children.stream()
                .map(FePath::getPath)
                .collect(Collectors.toSet());
        assertIterableEquals(expectedPaths, actualPaths, "Paths of children");
    }

    @Test
    void should_return_subdirectories() throws URISyntaxException {
        // setup
        URL thisTestClassUrl = getClass().getResource(getClass().getSimpleName() + ".class");
        assertNotNull(thisTestClassUrl, "URL of this test class");
        Path basePackagePath = FS.provider().getPath(thisTestClassUrl.toURI()).getParent().getParent();
        // run
        List<FeDirectory> children = repository.getSubdirectories(basePackagePath.toString());
        // check
        Set<String> expectedPaths = PathUtil.getSubdirectories(basePackagePath).stream()
                .map(Path::toString)
                .collect(Collectors.toSet());
        Set<String> actualPaths = children.stream()
                .map(FePath::getPath)
                .collect(Collectors.toSet());
        assertIterableEquals(expectedPaths, actualPaths, "Paths of subdirectories");
    }

    @Test
    void should_read_batch() throws IOException {
        // setup
        Path tempPath = Files.createTempFile(getClass().getSimpleName() + "-batch-file", "");
        try {
            String sourceLine = "Test source line";
            Files.writeString(tempPath, sourceLine);
            int batchSize = 4;
            // run
            String batch = repository.readBatch(tempPath.toString(), batchSize);
            // check
            assertEquals(sourceLine.substring(0, batchSize), batch, "Batch");
        } finally {
            Files.delete(tempPath);
        }
    }

    @Test
    void should_read_batches() throws IOException {
        // setup
        Path tempPath = Files.createTempFile(getClass().getSimpleName() + "-batches-file", "");
        try {
            String sourceLines = "One\nTwo\nThree";
            Files.writeString(tempPath, sourceLines);
            int batchSize = 4;
            // run
            Stream<String> batchStream = repository.readInBatches(tempPath.toString(), batchSize, 4);
            String batches = batchStream.collect(Collectors.joining());
            // check
            assertEquals(sourceLines, batches, "Batches");
        } finally {
            Files.delete(tempPath);
        }
    }

    @Test
    void should_read_allBytes() throws IOException {
        // setup
        Path tempPath = Files.createTempFile(getClass().getSimpleName() + "-bytes-file", "");
        try {
            byte[] bytes = new byte[]{1, 2, 3};
            Files.write(tempPath, bytes);
            // run
            byte[] readBytes = repository.readAllBytes(tempPath.toString());
            // check
            assertArrayEquals(bytes, readBytes, "Bytes");
        } finally {
            Files.delete(tempPath);
        }
    }

}