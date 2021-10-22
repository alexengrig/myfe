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

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MyTabbedPane extends JTabbedPane {

    public MyTabbedPane(MyTab defaultTab) {
        super(TOP, SCROLL_TAB_LAYOUT);
        insertTab(defaultTab.title(), defaultTab.icon(), defaultTab.component(), defaultTab.tip(), 0);
    }

    public void addMyTab(MyTab tab) {
        String title = tab.title();
        super.addTab(title, tab.icon(), tab.component(), tab.tip());
        int index = super.getTabCount() - 1;
        super.setTabComponentAt(index, new MyTabComponent(tab));
    }

    private class MyTabComponent extends JPanel {

        private MyTabComponent(MyTab tab) {
            String title = tab.title();
            add(new JLabel(title));
            JButton button = new JButton(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = MyTabbedPane.super.indexOfTab(title);
                    MyTabbedPane.super.removeTabAt(index);
                    tab.component().destroy();
                }
            });
            button.setText("x"); //TODO: Make it beautiful
            add(button);
        }

    }

}
