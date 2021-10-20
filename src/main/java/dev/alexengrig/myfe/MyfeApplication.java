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

import dev.alexengrig.myfe.view.MyTab;
import dev.alexengrig.myfe.view.MyTabFactory;
import dev.alexengrig.myfe.view.MyTabbedPane;

import javax.swing.*;
import java.awt.*;

public final class MyfeApplication extends JFrame {

    private static final String TITLE = "myfe";

    private final MyTabFactory tabFactory = new MyTabFactory();

    public MyfeApplication() {
        super(TITLE);
        init();
        pack();
        setLocationRelativeTo(null); // center of screen
        setVisible(true);
    }

    private void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(createFramePreferredSize());
        componentsInit();
    }

    private Dimension createFramePreferredSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Dimension(screenSize.width / 2, screenSize.height / 2);
    }

    private void componentsInit() {
        //TODO: Add menu
        tabbedPaneInit();
    }

    private void tabbedPaneInit() {
        MyTab defaultTab = tabFactory.createDefaultTab();
        MyTabbedPane pane = new MyTabbedPane(defaultTab);
        add(pane);
    }

}
