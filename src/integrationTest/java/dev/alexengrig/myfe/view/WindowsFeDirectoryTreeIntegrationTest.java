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

import dev.alexengrig.myfe.BaseMyfeApplicationIntegrationTest;
import dev.alexengrig.myfe.TestUtil;
import org.assertj.swing.fixture.JTableCellFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.assertj.swing.fixture.JTreeFixture;
import org.assertj.swing.fixture.JTreePathFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class WindowsFeDirectoryTreeIntegrationTest extends BaseMyfeApplicationIntegrationTest {

    private JTreeFixture tree;

    @BeforeClass
    public void skipOnNotWindows() {
        TestUtil.skipOnNotWindows();
    }

    @BeforeMethod
    public void beforeMethod() {
        this.tree = getTree();
    }

    @AfterMethod
    public void afterMethod() {
        this.tree = null;
    }

    @Test
    public void should_expand_nodeOfdiskC_and_find_nodeOfWindows() {
        JTreePathFixture diskCNode = tree.node("This computer/C:\\");
        diskCNode.expand();
        JTreePathFixture windowsNode = tree.node("This computer/C:\\/Windows");
        assertNotNull(windowsNode, "Node of Windows");
    }

    @Test
    public void should_click_nodeOfdiskC_and_find_cellOfWindows() {
        JTreePathFixture diskCNode = tree.node("This computer/C:\\");
        diskCNode.click();
        JTableFixture table = getTable();
        JTableCellFixture windowsCell = table.cell("Windows");
        assertNotNull(windowsCell, "Cell of Windows");
    }

    @Test
    public void should_click_rootNode_and_find_cellOfDiskC() {
        JTreePathFixture diskCNode = tree.node("This computer/C:\\");
        diskCNode.click();
        JTreePathFixture rootNode = tree.node("This computer");
        rootNode.click();
        JTableFixture table = getTable();
        JTableCellFixture diskCCell = table.cell("C:\\");
        assertNotNull(diskCCell, "Cell of C:\\");
    }

}
