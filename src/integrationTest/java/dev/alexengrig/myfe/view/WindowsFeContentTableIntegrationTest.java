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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class WindowsFeContentTableIntegrationTest extends BaseMyfeApplicationIntegrationTest {

    private JTableFixture table;

    @BeforeClass
    public void skipOnNotWindows() {
        TestUtil.skipOnNotWindows();
    }

    @BeforeMethod
    public void beforeMethod() {
        this.table = getTable();
    }

    @AfterMethod
    public void afterMethod() {
        this.table = null;
    }

    @Test
    public void should_doubleClick_cellOfDiskC_and_find_cellOfWindows() {
        JTableCellFixture diskCCell = table.cell("C:\\");
        diskCCell.doubleClick();
        JTableCellFixture windowsCell = table.cell("Windows");
        assertNotNull(windowsCell, "Cell of Windows");
    }

}
