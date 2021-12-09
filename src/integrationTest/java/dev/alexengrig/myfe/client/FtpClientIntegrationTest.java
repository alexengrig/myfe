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

import dev.alexengrig.myfe.WithUnixFtpServer;
import dev.alexengrig.myfe.domain.FtpDirectory;
import dev.alexengrig.myfe.domain.FtpPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class FtpClientIntegrationTest extends WithUnixFtpServer {

    CommonsFtpClient client;

    @BeforeEach
    void beforeEach() throws IOException {
        setup();
        client = new CommonsFtpClient();
        client.connect(host, port);
        client.login(username, password);
    }

    @AfterEach
    void afterEach() throws IOException {
        client.disconnect();
        tearDown();
    }

    @Test
    void should_return_rootDirectories() {
        // run
        List<FtpDirectory> rootDirectories = client.listRootDirectories();
        // check
        Set<String> expectedPaths = Collections.singleton("/");
        Set<String> actualPaths = rootDirectories.stream()
                .map(FtpDirectory::getPath)
                .collect(Collectors.toSet());
        assertIterableEquals(expectedPaths, actualPaths, "Paths of root directories");
    }

    @Test
    void should_return_subdirectories() throws IOException {
        // setup
        String directoryPath = "/pub/directory";
        addDirectory(directoryPath);
        String filePath = "/pub/file.this";
        addFile(filePath);
        // run
        List<FtpDirectory> subdirectories = client.listSubdirectories("/pub");
        // check
        Set<String> expectedPaths = Collections.singleton(directoryPath);
        Set<String> actualPaths = subdirectories.stream()
                .map(FtpDirectory::getPath)
                .collect(Collectors.toSet());
        assertIterableEquals(expectedPaths, actualPaths, "Paths of subdirectories");
    }

    @Test
    void should_return_children() throws IOException {
        // setup
        String directoryPath = "/pub/directory";
        addDirectory(directoryPath);
        String filePath = "/pub/file.this";
        addFile(filePath);
        // run
        List<FtpPath> children = client.listChildren("/pub");
        // check
        Set<String> expectedPaths = new HashSet<>(Arrays.asList(directoryPath, filePath));
        Set<String> actualPaths = children.stream()
                .map(FtpPath::getPath)
                .collect(Collectors.toSet());
        assertIterableEquals(expectedPaths, actualPaths, "Paths of children");
    }

    @Test
    void should_return_fileStream() throws IOException {
        // setup
        String filePath = "/pub/file.this";
        String fileSource = "Test source";
        addFile(filePath, fileSource);
        // run
        try (InputStream inputStream = client.retrieveFileStream(filePath)) {
            byte[] bytes = inputStream.readAllBytes();
            // check
            assertEquals(fileSource, new String(bytes), "File source");
        }
    }

}