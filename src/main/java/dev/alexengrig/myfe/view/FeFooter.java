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

import dev.alexengrig.myfe.model.FeFooterModel;
import dev.alexengrig.myfe.model.event.FeFooterModelEvent;
import dev.alexengrig.myfe.model.event.FeFooterModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.List;

public class FeFooter extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FeFooterModel model;

    private final JLabel counterView = new JLabel();
    private final JLabel tasksView = new JLabel();
    private final JProgressBar progressView = new JProgressBar();

    public FeFooter(FeFooterModel model) {
        super(new BorderLayout());
        this.model = model;
        init();
    }

    private void init() {
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        initListeners();
        initComponents();
        handleChangeNumberOfElements(model.getNumberOfElements());
    }

    private void initListeners() {
        model.addFeFooterModelListener(new ModelListener());
    }

    private void initComponents() {
        add(counterView, BorderLayout.WEST);
        JPanel taskPanel = createTasksPanel();
        add(taskPanel, BorderLayout.EAST);
    }

    private JPanel createTasksPanel() {
        JPanel tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.X_AXIS));
        progressView.setIndeterminate(true);
        tasksPanel.add(tasksView);
        tasksPanel.add(Box.createHorizontalStrut(4));
        Dimension barSize = progressView.getPreferredSize();
        barSize.width = 50;
        progressView.setPreferredSize(barSize);
        progressView.setVisible(false);
        tasksPanel.add(progressView);
        return tasksPanel;
    }

    private String createCounterText(int count) {
        if (count == 1) {
            return "1 element";
        } else {
            return count + " elements";
        }
    }

    private void handleChangeNumberOfElements(Integer numberOfElements) {
        LOGGER.debug("Handle change number of elements: {}", numberOfElements);
        String text = numberOfElements != null ? createCounterText(numberOfElements) : "";
        counterView.setText(text);
    }

    private void handleChangeTasks(List<String> tasks) {
        LOGGER.debug("Handle change tasks: {}", tasks);
        String text;
        if (tasks.isEmpty()) {
            text = "";
            progressView.setVisible(false);
        } else {
            text = tasks.get(0);
            progressView.setVisible(true);
        }
        tasksView.setText(text);
    }

    private class ModelListener implements FeFooterModelListener {

        @Override
        public void changeTasks(FeFooterModelEvent event) {
            List<String> tasks = event.getTasks();
            handleChangeTasks(tasks);
        }

        @Override
        public void changeNumberOfElements(FeFooterModelEvent event) {
            Integer numberOfElements = event.getNumberOfElements();
            handleChangeNumberOfElements(numberOfElements);
        }

    }

}
