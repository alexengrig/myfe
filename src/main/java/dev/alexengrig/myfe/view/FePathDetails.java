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

import dev.alexengrig.myfe.model.FePath;
import dev.alexengrig.myfe.model.FeSelectedPathModel;
import dev.alexengrig.myfe.util.FePathUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FePathDetails extends JPanel {

    private final FeSelectedPathModel model;
    private final JPanel contentPane;

    public FePathDetails(FeSelectedPathModel model) {
        this.model = model;
        this.contentPane = new JPanel(new GridLayout(0, 1));
        init();
    }

    private void init() {
        addDetailsComponents();
        model.addSelectedFePathModelListener(event -> updateDetailsComponents());
        add(contentPane);
    }

    private void addDetailsComponents() {
        List<JComponent> components;
        if (model.isEmpty()) {
            components = createEmptyComponent();
        } else {
            components = createComponent();
        }
        for (JComponent component : components) {
            contentPane.add(component);
        }
    }

    private void updateDetailsComponents() {
        contentPane.removeAll();
        addDetailsComponents();
        contentPane.revalidate();
    }

    private List<JComponent> createEmptyComponent() {
        return List.of(new JLabel("Select an element to details"));
    }

    private List<JComponent> createComponent() {
        FePath path = model.getPath();
        return List.of(
                new JLabel("Name: " + path.getName()),
                new JLabel("Type: " + FePathUtil.getType(path))
        );
    }

}
