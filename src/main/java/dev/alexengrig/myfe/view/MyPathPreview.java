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

import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.model.MyPathModel;

import javax.swing.*;
import java.awt.*;

public class MyPathPreview extends JPanel {

    private final MyPathModel model;
    private final JPanel contentPane;

    public MyPathPreview(MyPathModel model) {
        this.model = model;
        this.contentPane = new JPanel(new BorderLayout());
        init();
    }

    private void init() {
        addPreviewComponent();
        model.addMyPathModelListener(event -> updatePreviewComponent());
        add(contentPane);
    }

    private void addPreviewComponent() {
        JComponent component;
        if (model.isEmpty()) {
            component = createEmptyComponent();
        } else {
            component = createComponent();
        }
        contentPane.add(component);
    }

    private void updatePreviewComponent() {
        contentPane.removeAll();
        addPreviewComponent();
        contentPane.revalidate();
    }

    private JComponent createEmptyComponent() {
        return new JLabel("Select an element to preview");
    }

    private JComponent createComponent() {
        MyPath path = model.getPath();
        if (path.isDirectory()) {
            return new JLabel("No preview available");
        }
        //TODO: Add content
        return new JLabel("Preview");
    }

}
