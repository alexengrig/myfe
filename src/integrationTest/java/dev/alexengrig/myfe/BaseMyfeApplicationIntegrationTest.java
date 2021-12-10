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

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.fixture.FrameFixture;
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

}
