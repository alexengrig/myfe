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

package dev.alexengrig.myfe;

import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

public class WithUnixFtpServer {

    protected final String host = "localhost";
    protected final int port = 21;
    protected final String username = "user";
    protected final String password = "pass";

    protected FakeFtpServer ftpServer;

    public static void main(String[] args) throws Exception {
        WithUnixFtpServer instance = new WithUnixFtpServer();
        instance.setup();
        FileSystem fs = instance.ftpServer.getFileSystem();
        fs.add(new FileEntry("/pub/text.txt", "This is text.txt"));
        fs.add(new FileEntry("/pub/other.txt", "This is other.txt"));
        System.out.println("Press enter for stop.");
        int ignore = System.in.read();
        instance.tearDown();
    }

    FakeFtpServer createUnixFakeFtpServer() {
        FakeFtpServer fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(port);
        fakeFtpServer.addUserAccount(new UserAccount(username, password, "/"));
        UnixFakeFileSystem fs = new UnixFakeFileSystem();
        fs.add(new DirectoryEntry("/"));
        fakeFtpServer.setFileSystem(fs);
        return fakeFtpServer;
    }

    protected void setup() {
        ftpServer = createUnixFakeFtpServer();
        ftpServer.start();
    }

    protected void tearDown() {
        ftpServer.stop();
    }

    protected void addDirectory(String path) {
        FileSystem fs = ftpServer.getFileSystem();
        fs.add(new DirectoryEntry(path));
    }

    protected void addFile(String path) {
        FileSystem fs = ftpServer.getFileSystem();
        fs.add(new FileEntry(path));
    }

    protected void addFile(String path, String content) {
        FileSystem fs = ftpServer.getFileSystem();
        fs.add(new FileEntry(path, content));
    }

}
