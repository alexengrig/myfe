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

import dev.alexengrig.myfe.WithFtpServer;
import dev.alexengrig.myfe.config.FtpConnectionConfig;
import dev.alexengrig.myfe.model.FeDirectory;
import dev.alexengrig.myfe.model.FePath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApacheCommonsFtpFileSystemPathRepositoryTest extends WithFtpServer {

    ApacheCommonsFtpFileSystemPathRepository ftpPathRepository;

    @BeforeEach
    @Override
    protected void setup() {
        super.setup();
        FtpConnectionConfig config = FtpConnectionConfig.user(host, username, password.toCharArray());
        ftpPathRepository = new ApacheCommonsFtpFileSystemPathRepository(config);
    }

    @AfterEach
    @Override
    protected void tearDown() throws Exception {
        ftpPathRepository.close();
        super.tearDown();
    }

    @Test
    void should_return_rootDirectories() {
        FileSystem fs = ftpServer.getFileSystem();
        fs.add(new DirectoryEntry("/pub"));
        List<FeDirectory> rootDirectories = ftpPathRepository.getRootDirectories();
        assertEquals(1, rootDirectories.size(), "Number of root directories");
    }

    @Test
    void should_return_children() {
        FileSystem fs = ftpServer.getFileSystem();
        fs.add(new DirectoryEntry("/pub/empty"));
        fs.add(new FileEntry("/pub/text.txt"));
        List<FePath> children = ftpPathRepository.getChildren("/pub");
        assertEquals(2, children.size(), "Number of children");
        FePath dir, file;
        if (children.get(0).isDirectory()) {
            dir = children.get(0);
            file = children.get(1);
            assertTrue(file.isFile(), "It isn't file: " + file);
        } else {
            file = children.get(0);
            dir = children.get(1);
            assertTrue(dir.isDirectory(), "It isn't directory: " + dir);
        }
        assertEquals("empty", dir.getName(), "Directory name");
        assertEquals("text.txt", file.getName(), "File name");
        assertEquals("/pub/empty", dir.getPath(), "Directory path");
        assertEquals("/pub/text.txt", file.getPath(), "File path");
    }

    @Test
    void should_return_subdirectories() {
        FileSystem fs = ftpServer.getFileSystem();
        fs.add(new DirectoryEntry("/pub/empty"));
        fs.add(new FileEntry("/pub/text.txt"));
        List<FeDirectory> subdirectories = ftpPathRepository.getSubdirectories("/pub");
        assertEquals(1, subdirectories.size(), "Number of subdirectories");
    }

    @Test
    void should_read_batch() {
        FileSystem fs = ftpServer.getFileSystem();
        fs.add(new FileEntry("/text.txt", new String(new byte[]{'t', 'e', 'x', 't'})));
        String batch = ftpPathRepository.readBatch("/text.txt", 2);
        assertEquals("te", batch, "Batch");
    }

    @Test
    void should_read_inBatches() {
        FileSystem fs = ftpServer.getFileSystem();
        fs.add(new FileEntry("/text.txt", new String(new byte[]{'t', 'e', 'x', 't'})));
        Stream<String> batchStream = ftpPathRepository.readInBatches("/text.txt", 2, 4);
        List<String> batches = batchStream.collect(Collectors.toList());
        assertEquals(2, batches.size(), () -> "Number of batches, batches: " + batches);
        assertEquals("te", batches.get(0), "First batch");
        assertEquals("xt", batches.get(1), "Second batch");
    }

}