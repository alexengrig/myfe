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

import dev.alexengrig.myfe.model.FeCurrentDirectoryModel;
import dev.alexengrig.myfe.model.FeDirectory;
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

    public FeHeader(FeCurrentDirectoryModel directoryModel) {
        super(new BorderLayout());
        this.directoryModel = directoryModel;
        this.pathField = new JTextField(directoryModel.getRootName());
        this.backButton = new JButton();
        this.forwardButton = new JButton();
        this.upButton = new JButton();
        //FIXME: Refresh button
        init();
    }

    private void init() {
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        initListeners();
        addComponents();
    }

    private void addComponents() {
        addNavigations();
        pathField.setEnabled(false);
        pathField.setDisabledTextColor(Color.BLACK);
        pathField.setBackground(Color.WHITE);
        add(pathField, BorderLayout.CENTER);
    }

    private void addNavigations() {
        toggleNavigations();
        JPanel actionPanel = new JPanel(new GridLayout(1, 3));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
        NavigationActions navigation = new NavigationActions();
        backButton.setText("<");
        backButton.addActionListener(navigation::back);
        actionPanel.add(backButton);
        forwardButton.setText(">");
        forwardButton.addActionListener(navigation::forward);
        actionPanel.add(forwardButton);
        upButton.setText("^");
        upButton.addActionListener(navigation::up);
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

    private class NavigationActions {

        public void back(ActionEvent ignore) {
            directoryModel.goBack();
        }

        public void forward(ActionEvent ignore) {
            directoryModel.goForward();
        }

        public void up(ActionEvent ignore) {
            directoryModel.goUp();
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

    }

}
