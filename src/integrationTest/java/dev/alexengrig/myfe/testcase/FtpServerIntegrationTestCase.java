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

package dev.alexengrig.myfe.testcase;

import dev.alexengrig.myfe.BaseMyfeApplicationIntegrationTest;
import dev.alexengrig.myfe.WithUnixFtpServer;
import org.assertj.swing.data.Index;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.fixture.JTabbedPaneFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.assertj.swing.timing.Pause;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class FtpServerIntegrationTestCase extends BaseMyfeApplicationIntegrationTest {

    private final WithUnixFtpServer ftpServer = new WithUnixFtpServer();
    private final String rootName = ftpServer.host + ":" + ftpServer.port;

    @BeforeMethod
    public void beforeMethod() {
        ftpServer.setup();
    }

    @AfterMethod
    public void afterMethod() {
        ftpServer.tearDown();
    }

    @Test
    public void should_connect_to_localFtpServer() {
        connectToLocalFtpServer();
    }

    @Test
    public void should_load_filePreview() {
        // setup
        String expectedPreview = "This is expected content.";
        ftpServer.addFile("/pub/file.txt", expectedPreview);
        // run
        connectToLocalFtpServer();
        JTableFixture table = getTable(rootName);
        table.cell("/").doubleClick();
        Pause.pause(400);
        table.cell("pub").doubleClick();
        Pause.pause(400);
        table.cell("file.txt").click();
        Pause.pause(400);
        JPanelFixture panel = getPreviewPanel(rootName);
        panel.button("load").click();
        String actualPreview = panel.textBox("preview-text").text().trim();
        assertEquals(actualPreview, expectedPreview, "Text of preview");
    }

    private void connectToLocalFtpServer() {
        FrameFixture window = window();
        window.menuItem("file").click();
        window.menuItem("connect-to-ftp-server").click();
        DialogFixture dialog = window.dialog("ftp-connect");
        dialog.textBox("host").setText(ftpServer.host);
        dialog.textBox("port").setText(String.valueOf(ftpServer.port));
        dialog.textBox("username").setText(ftpServer.username);
        dialog.textBox("password").setText(ftpServer.password);
        dialog.button("connect").click();
        JTabbedPaneFixture tabbedPane = getTabbedPane();
        tabbedPane.requireTabTitles("This computer", "FTP: " + rootName);
        tabbedPane.requireSelectedTab(Index.atIndex(1));
        JTableFixture table = getTable(rootName);
        table.cell("/");
    }

}
