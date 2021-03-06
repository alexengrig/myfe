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

import dev.alexengrig.myfe.view.FeContentFilter;
import dev.alexengrig.myfe.view.FeContentTable;
import dev.alexengrig.myfe.view.FeDirectoryTree;
import dev.alexengrig.myfe.view.FeFooter;
import dev.alexengrig.myfe.view.FeHeader;
import dev.alexengrig.myfe.view.FePathDetails;
import dev.alexengrig.myfe.view.FePathPreview;
import dev.alexengrig.myfe.view.FeTabbedPane;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.assertj.swing.fixture.JTabbedPaneFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.assertj.swing.fixture.JTreeFixture;
import org.assertj.swing.testng.listener.ScreenshotOnFailureListener;
import org.assertj.swing.testng.testcase.AssertJSwingTestngTestCase;
import org.testng.annotations.Listeners;

@GUITest
@Listeners(ScreenshotOnFailureListener.class)
public abstract class BaseMyfeApplicationIntegrationTest extends AssertJSwingTestngTestCase {

    private FrameFixture window;

    protected FrameFixture window() {
        return window;
    }

    @Override
    protected final void onSetUp() {
        MyfeApplication mainWindow = createMyfeApplication();
        this.window = new FrameFixture(this.robot(), mainWindow);
        this.window.show();
    }

    private MyfeApplication createMyfeApplication() {
        return GuiActionRunner.execute(new GuiQuery<>() {

            @Override
            protected MyfeApplication executeInEDT() {
                return new MyfeApplication();
            }

        });
    }

    @Override
    protected final void onTearDown() {
        super.onTearDown();
        this.window.cleanUp();
        this.window = null;
    }

    /**
     * @return {@link FeDirectoryTree}
     */
    protected JTreeFixture getTree() {
        return window.tree(new InstanceTypeMatcher<>(FeDirectoryTree.class));
    }

    /**
     * @return {@link FeContentTable}
     */
    protected JTableFixture getTable() {
        return window.table(new InstanceTypeMatcher<>(FeContentTable.class));
    }

    /**
     * @param name component name
     * @return {@link FeContentTable} by {@code name}
     */
    protected JTableFixture getTable(String name) {
        return window.table(new NamedInstanceTypeMatcher<>(FeContentTable.class, name));
    }

    /**
     * @return {@link FeContentFilter}
     */
    protected JPanelFixture getFilterPanel() {
        return window.panel(new InstanceTypeMatcher<>(FeContentFilter.class));
    }

    /**
     * @return {@link FeFooter}
     */
    protected JPanelFixture getFooter() {
        return window.panel(new InstanceTypeMatcher<>(FeFooter.class));
    }

    /**
     * @return {@link FeHeader}
     */
    protected JPanelFixture getHeader() {
        return window.panel(new InstanceTypeMatcher<>(FeHeader.class));
    }

    /**
     * @return {@link FePathDetails}
     */
    protected JPanelFixture getDetailsPanel() {
        return window.panel(new InstanceTypeMatcher<>(FePathDetails.class));
    }

    /**
     * @return {@link FePathPreview}
     */
    protected JPanelFixture getPreviewPanel() {
        return window.panel(new InstanceTypeMatcher<>(FePathPreview.class));
    }

    /**
     * @param name component name
     * @return {@link FePathPreview} by {@code name}
     */
    protected JPanelFixture getPreviewPanel(String name) {
        return window.panel(new NamedInstanceTypeMatcher<>(FePathPreview.class, name));
    }

    /**
     * @return {@link FePathPreview}
     */
    protected JTabbedPaneFixture getTabbedPane() {
        return window.tabbedPane(new InstanceTypeMatcher<>(FeTabbedPane.class));
    }

}
