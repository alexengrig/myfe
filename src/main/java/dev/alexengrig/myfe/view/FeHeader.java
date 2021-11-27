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

import dev.alexengrig.myfe.domain.FeDirectory;
import dev.alexengrig.myfe.model.FeCurrentDirectoryModel;
import dev.alexengrig.myfe.model.event.FeCurrentDirectoryModelEvent;
import dev.alexengrig.myfe.model.event.FeCurrentDirectoryModelListener;
import dev.alexengrig.myfe.view.event.FeHeaderEvent;
import dev.alexengrig.myfe.view.event.FeHeaderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

public class FeHeader extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<FeHeaderListener> listeners = new LinkedList<>();

    private final FeCurrentDirectoryModel directoryModel;

    private final JTextField pathField;
    //TODO: Compose buttons to NavigationComponent
    private final JButton backButton;
    private final JButton forwardButton;
    private final JButton upButton;
    private final JButton updateButton;

    public FeHeader(FeCurrentDirectoryModel directoryModel) {
        super(new BorderLayout());
        this.directoryModel = directoryModel;
        this.pathField = new JTextField(directoryModel.getRootName());
        this.backButton = new JButton();
        this.forwardButton = new JButton();
        this.upButton = new JButton();
        this.updateButton = new JButton();
        init();
    }

    private void init() {
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        initListeners();
        addComponents();
    }

    private void addComponents() {
        ButtonActions actions = new ButtonActions();
        addNavigations(actions);
        pathField.setEnabled(false);
        pathField.setDisabledTextColor(Color.BLACK);
        pathField.setBackground(Color.WHITE);
        add(pathField, BorderLayout.CENTER);
        updateButton.setText("Update");
        updateButton.addActionListener(actions::update);
        add(updateButton, BorderLayout.EAST);
    }

    private void addNavigations(ButtonActions actions) {
        toggleNavigations();
        JPanel actionPanel = new JPanel(new GridLayout(1, 3));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
        backButton.setText("<");
        backButton.addActionListener(actions::back);
        actionPanel.add(backButton);
        forwardButton.setText(">");
        forwardButton.addActionListener(actions::forward);
        actionPanel.add(forwardButton);
        upButton.setText("^");
        upButton.addActionListener(actions::up);
        actionPanel.add(upButton);
        add(actionPanel, BorderLayout.WEST);
    }

    private void toggleNavigations() {
        backButton.setEnabled(directoryModel.canGoBack());
        forwardButton.setEnabled(directoryModel.canGoForward());
        upButton.setEnabled(directoryModel.canGoUp());
    }

    private void initListeners() {
        directoryModel.addFeCurrentDirectoryModelListener(new DirectoryListener());
    }

    private void setDirectoryPath(String directoryPath) {
        pathField.setText(directoryPath);
    }

    private void handleSetRoot() {
        LOGGER.debug("Handle set root");
        setDirectoryPath(directoryModel.getRootName());
        toggleNavigations();
        fireMoveToRoot(FeHeaderEvent.root());
    }

    private void handleSetDirectory(FeDirectory directory) {
        LOGGER.debug("Handle set directory: {}", directory);
        setDirectoryPath(directory.getPath());
        toggleNavigations();
        fireMoveToDirectory(FeHeaderEvent.directory(directory));
    }

    private void handleRefresh() {
        LOGGER.debug("Handle refresh");
        fireRefreshContent(FeHeaderEvent.refreshing());
    }

    public void addFeHeaderListener(FeHeaderListener listener) {
        listeners.add(listener);
    }

    public void removeFeHeaderListener(FeHeaderListener listener) {
        listeners.remove(listener);
    }

    private void fireMoveToRoot(FeHeaderEvent event) {
        LOGGER.debug("Fire move to root: {}", event);
        for (FeHeaderListener listener : listeners) {
            listener.moveToRoot(event);
        }
    }

    private void fireMoveToDirectory(FeHeaderEvent event) {
        LOGGER.debug("Fire move to directory: {}", event);
        for (FeHeaderListener listener : listeners) {
            listener.moveToDirectory(event);
        }
    }

    private void fireRefreshContent(FeHeaderEvent event) {
        LOGGER.debug("Fire refresh content");
        for (FeHeaderListener listener : listeners) {
            listener.refreshContent(event);
        }
    }

    private class ButtonActions {

        public void back(ActionEvent ignore) {
            directoryModel.goBack();
        }

        public void forward(ActionEvent ignore) {
            directoryModel.goForward();
        }

        public void up(ActionEvent ignore) {
            directoryModel.goUp();
        }

        public void update(ActionEvent ignore) {
            directoryModel.update();
        }

    }

    private class DirectoryListener implements FeCurrentDirectoryModelListener {

        @Override
        public void goToRoot(FeCurrentDirectoryModelEvent event) {
            handleSetRoot();
        }

        @Override
        public void goToDirectory(FeCurrentDirectoryModelEvent event) {
            FeDirectory directory = event.getDirectory();
            handleSetDirectory(directory);
        }

        @Override
        public void refresh(FeCurrentDirectoryModelEvent event) {
            handleRefresh();
        }
    }

}
