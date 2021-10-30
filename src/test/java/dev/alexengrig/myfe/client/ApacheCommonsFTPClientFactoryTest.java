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

import dev.alexengrig.myfe.WithFtpServer;
import dev.alexengrig.myfe.config.FTPConnectionConfig;
import dev.alexengrig.myfe.model.MyFtpDirectory;
import dev.alexengrig.myfe.model.MyFtpPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ApacheCommonsFTPClientFactoryTest extends WithFtpServer {

    ApacheCommonsFtpClientFactory ftpClientFactory;

    ApacheCommonsFtpClientFactory createFtpClientFactory() {
        return new ApacheCommonsFtpClientFactory(FTPConnectionConfig.user(host, username, password.toCharArray()));
    }

    @BeforeEach
    void beforeEach() {
        ftpClientFactory = createFtpClientFactory();
        super.setup();
    }

    @AfterEach
    void afterEach() throws Exception {
        ftpClientFactory.close();
        super.tearDown();
    }

    @Test
    void should_return_rootPaths() {
        // setup
        FileSystem fs = ftpServer.getFileSystem();
        String fileName = "info.txt";
        FileEntry fileEntry = new FileEntry("/" + fileName);
        fs.add(fileEntry);
        String dirName = "pub";
        DirectoryEntry dirEntry = new DirectoryEntry("/" + dirName);
        fs.add(dirEntry);
        // run
        try (MyFtpClient client = ftpClientFactory.createClient()) {
            List<MyFtpPath> rootPaths = client.list().collect(Collectors.toList());
            // check
            assertEquals(2, rootPaths.size());
            MyFtpPath dir, file;
            if (rootPaths.get(0).isDirectory()) {
                dir = rootPaths.get(0);
                file = rootPaths.get(1);
                assertTrue(file.isFile(), () -> "It isn't file: " + file);
            } else {
                file = rootPaths.get(0);
                dir = rootPaths.get(1);
                assertTrue(dir.isDirectory(), () -> "It isn't directory: " + dir);
            }
            assertEquals(dirName, dir.getName(), "Directory name");
            assertEquals(fileName, file.getName(), "File name");
            assertEquals(dirEntry.getPath(), dir.getPath(), "Directory path");
            assertEquals(fileEntry.getPath(), file.getPath(), "File path");
        } catch (Exception exception) {
            fail(exception);
        }
    }

    @Test
    void should_return_directoryPaths() {
        // setup
        FileSystem fs = ftpServer.getFileSystem();
        String path = "/pub";
        String dirName = "dir";
        DirectoryEntry dirEntry = new DirectoryEntry(path + "/" + dirName);
        fs.add(dirEntry);
        String fileName = "file.tmp";
        FileEntry fileEntry = new FileEntry(path + "/" + fileName);
        fs.add(fileEntry);
        // run
        try (MyFtpClient client = ftpClientFactory.createClient()) {
            List<MyFtpPath> dirPaths = client.list(path).collect(Collectors.toList());
            // check
            assertEquals(2, dirPaths.size());
            MyFtpPath dir, file;
            if (dirPaths.get(0).isDirectory()) {
                dir = dirPaths.get(0);
                file = dirPaths.get(1);
                assertTrue(file.isFile(), () -> "It isn't file: " + file);
            } else {
                file = dirPaths.get(0);
                dir = dirPaths.get(1);
                assertTrue(dir.isDirectory(), () -> "It isn't directory: " + dir);
            }
            assertEquals(dirName, dir.getName(), "Directory name");
            assertEquals(fileName, file.getName(), "File name");
            assertEquals(dirEntry.getPath(), dir.getPath(), "Directory path");
            assertEquals(fileEntry.getPath(), file.getPath(), "File path");
        } catch (Exception exception) {
            fail(exception);
        }
    }

    @Test
    void should_return_rootSubdirectories() {
        // setup
        FileSystem fs = ftpServer.getFileSystem();
        String dirName = "pub";
        DirectoryEntry dirEntry = new DirectoryEntry("/" + dirName);
        fs.add(dirEntry);
        fs.add(new FileEntry("/info.txt")); // ignored
        // run
        try (MyFtpClient client = ftpClientFactory.createClient()) {
            List<MyFtpDirectory> rootSubdirectories = client.subdirectories().collect(Collectors.toList());
            // check
            assertEquals(1, rootSubdirectories.size());
            MyFtpDirectory dir = rootSubdirectories.get(0);
            assertEquals(dirName, dir.getName(), "Directory name");
            assertEquals(dirEntry.getPath(), dir.getPath(), "Directory path");
        } catch (Exception exception) {
            fail(exception);
        }
    }

    @Test
    void should_return_directorySubdirectories() {
        // setup
        FileSystem fs = ftpServer.getFileSystem();
        String path = "/pub";
        String dirName = "dir";
        DirectoryEntry dirEntry = new DirectoryEntry(path + "/" + dirName);
        fs.add(dirEntry);
        fs.add(new FileEntry(path + "/" + "file.tmp"));
        // run
        try (MyFtpClient client = ftpClientFactory.createClient()) {
            List<MyFtpDirectory> dirSubdirectories = client.subdirectories(path).collect(Collectors.toList());
            // check
            assertEquals(1, dirSubdirectories.size());
            MyFtpDirectory dir = dirSubdirectories.get(0);
            assertEquals(dirName, dir.getName(), "Directory name");
            assertEquals(dirEntry.getPath(), dir.getPath(), "Directory path");
        } catch (Exception exception) {
            fail(exception);
        }
    }

    @Test
    void should_return_fileInputStream() {
        // setup
        FileSystem fs = ftpServer.getFileSystem();
        String fileContent = "Content of info.txt";
        FileEntry fileEntry = new FileEntry("/info.txt", fileContent);
        fs.add(fileEntry);
        // run
        try (MyFtpClient client = ftpClientFactory.createClient();
             InputStream inputStream = client.inputStream(fileEntry.getPath())) {
            // check
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(fileContent, content);
        } catch (Exception exception) {
            fail(exception);
        }
    }

    @Test
    void testPermits() throws InterruptedException {
        int numberOfThreads = 30;
        AtomicInteger counter = new AtomicInteger(numberOfThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try (MyFtpClient client = ftpClientFactory.createClient()) {
                    long result = client.list().count();
                    Thread.sleep(300L);
                    counter.decrementAndGet();
                    return result;
                } catch (Exception e) {
                    counter.incrementAndGet();
                    return -1;
                }
            });
        }
        executorService.shutdown();
        if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
            fail("Timeout expired");
        }
        assertEquals(0, counter.get(), "Counter");
    }

}