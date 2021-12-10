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

import dev.alexengrig.myfe.domain.FePath;
import dev.alexengrig.myfe.model.FeSelectedPathModel;
import dev.alexengrig.myfe.model.event.FeSelectedPathModelEvent;
import dev.alexengrig.myfe.model.event.FeSelectedPathModelListener;
import dev.alexengrig.myfe.util.FePathUtil;

import javax.swing.*;

public class FePathDetails extends JPanel {

    private final FeSelectedPathModel model;

    private final JLabel noDetailsLabel = new JLabel();

    private final JPanel detailsPanel = new JPanel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel typeLabel = new JLabel();

    public FePathDetails(FeSelectedPathModel model) {
        this.model = model;
        init();
    }

    private void init() {
        model.addSelectedFePathModelListener(new ModelListener());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        noDetailsLabel.setName("no-details");
        noDetailsLabel.setText("Select an element to details");
        add(noDetailsLabel);
        detailsPanel.setName("details");
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setVisible(false);
        nameLabel.setName("name");
        detailsPanel.add(nameLabel);
        typeLabel.setName("type");
        detailsPanel.add(typeLabel);
        add(detailsPanel);
    }

    private void handleChangePath(FePath path) {
        if (path != null) {
            nameLabel.setText("Name: " + path.getName());
            typeLabel.setText("Type: " + FePathUtil.getType(path));
            noDetailsLabel.setVisible(false);
            detailsPanel.setVisible(true);
        } else {
            detailsPanel.setVisible(false);
            noDetailsLabel.setVisible(true);
            nameLabel.setText("");
            typeLabel.setText("");
        }
    }

    private class ModelListener implements FeSelectedPathModelListener {

        @Override
        public void changePath(FeSelectedPathModelEvent event) {
            handleChangePath(event.getPath());
        }

    }

}
