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
import java.awt.*;

public class MyTabComponent extends JPanel {

    public MyTabComponent() {
        super(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        initHeader();
        initBody();
        initFooter();
    }

    private void initHeader() {
        //TODO: Create header component
        JPanel top = new JPanel();
        top.add(new JLabel("<- -> ^     C:\\Users\\admin     refresh"));
        add(top, BorderLayout.NORTH);
    }

    private void initBody() {
        MyTree tree = new MyTree();
        MyTable table = new MyTable();
        MyDetails details = new MyDetails();
        MyPreview preview = new MyPreview();
        MySplitPane info = new MySplitPane.Vertical(new MyScrollPane(details), new MyScrollPane(preview));
        MySplitPane content = new MySplitPane.Horizontal(new MyScrollPane(table), info);
        MySplitPane center = new MySplitPane.Horizontal(new MyScrollPane(tree), content);
        add(center, BorderLayout.CENTER);
    }

    private void initFooter() {
        //TODO: Create footer component
        JPanel bottom = new JPanel();
        bottom.add(new JLabel("Number of elements: 123"));
        add(bottom, BorderLayout.SOUTH);
    }

}
