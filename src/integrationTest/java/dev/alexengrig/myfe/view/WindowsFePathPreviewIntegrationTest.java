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

package dev.alexengrig.myfe.view;

import dev.alexengrig.myfe.BaseWindowsMyfeApplicationIntegrationTest;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.timing.Pause;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class WindowsFePathPreviewIntegrationTest extends BaseWindowsMyfeApplicationIntegrationTest {

    private JPanelFixture panel;

    @BeforeMethod
    public void beforeMethod() {
        this.panel = getPreviewPanel();
    }

    @AfterMethod
    public void afterMethod() {
        this.panel = null;
    }

    @Test
    public void should_show_unselectedPreviewText() {
        JTextComponentFixture previewText = panel.textBox("preview-text");
        assertEquals(previewText.text(), "Select an element to preview", "Text of preview");
    }

    @Test
    public void should_show_noPreviewText() {
        JTextComponentFixture previewText = panel.textBox("preview-text");
        getTable().cell("C:\\").click();
        Pause.pause(1000);
        assertEquals(previewText.text(), "No preview available", "Text of preview");
    }

}
