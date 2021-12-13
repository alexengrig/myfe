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

public class FeTabbedPane extends JTabbedPane {

    public FeTabbedPane() {
        super(TOP, SCROLL_TAB_LAYOUT);
    }

    public void setDefaultTab(FeTab defaultTab) {
        insertTab(defaultTab.title(), null, defaultTab, defaultTab.tip(), 0);
    }

    public void openNewTab(FeTab tab) {
        super.addTab(tab.title(), null, tab, tab.tip());
        int index = super.getTabCount() - 1;
        super.setTabComponentAt(index, new MyTabComponent(tab));
        setSelectedIndex(index);
    }

    public boolean hasTab(String title) {
        return indexOfTab(title) >= 0;
    }

    public void openTab(String title) {
        int index = indexOfTab(title);
        setSelectedIndex(index);
    }

    private class MyTabComponent extends JPanel {

        private MyTabComponent(FeTab tab) {
            String title = tab.title();
            add(new JLabel(title));
            JButton button = new JButton(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = FeTabbedPane.super.indexOfTab(title);
                    FeTabbedPane.super.removeTabAt(index);
                    tab.destroy();
                }
            });
            button.setText("x"); //TODO: Make it beautiful
            button.setBorder(null);
            add(button);
        }

    }

}
