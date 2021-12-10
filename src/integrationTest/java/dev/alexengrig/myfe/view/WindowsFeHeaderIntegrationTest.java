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
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class WindowsFeHeaderIntegrationTest extends BaseWindowsMyfeApplicationIntegrationTest {

    private JPanelFixture panel;

    @BeforeMethod
    public void beforeMethod() {
        this.panel = getHeader();
    }

    @AfterMethod
    public void afterMethod() {
        this.panel = null;
    }

    @Test
    public void should_goTo_diskC_and_back() {
        JButtonFixture backButton = panel.button("back");
        backButton.requireDisabled();
        JTableFixture table = getTable();
        table.cell("C:\\").doubleClick();
        backButton.requireEnabled();
        backButton.click();
        table.cell("C:\\");
        backButton.requireDisabled();
    }

    @Test
    public void should_goTo_diskC_and_back_and_forward() {
        JButtonFixture backButton = panel.button("back");
        JButtonFixture forwardButton = panel.button("forward");
        backButton.requireDisabled();
        forwardButton.requireDisabled();
        JTableFixture table = getTable();
        table.cell("C:\\").doubleClick();
        backButton.requireEnabled();
        forwardButton.requireDisabled();
        backButton.click();
        table.cell("C:\\");
        backButton.requireDisabled();
        forwardButton.requireEnabled();
        forwardButton.click();
        backButton.requireEnabled();
        forwardButton.requireDisabled();
    }

    @Test
    public void should_goTo_diskC_and_toParent() {
        JButtonFixture upButton = panel.button("to-parent");
        upButton.requireDisabled();
        JTableFixture table = getTable();
        table.cell("C:\\").doubleClick();
        upButton.requireEnabled();
        upButton.click();
        table.cell("C:\\");
        upButton.requireDisabled();
    }

    @Test
    public void should_goTo_diskC_and_change_path() {
        JTextComponentFixture pathTextField = panel.textBox("path");
        assertEquals(pathTextField.text(), "This computer", "Path of root");
        JTableFixture table = getTable();
        table.cell("C:\\").doubleClick();
        assertEquals(pathTextField.text(), "C:\\", "Path of C:\\");
    }

}
