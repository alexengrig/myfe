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
import dev.alexengrig.myfe.model.FeContentTableModel;
import dev.alexengrig.myfe.util.event.EventListenerGroup;
import dev.alexengrig.myfe.util.swing.DelayedSingleTaskExecutor;
import dev.alexengrig.myfe.view.event.DoNothingKeyListener;
import dev.alexengrig.myfe.view.event.DoNothingMouseListener;
import dev.alexengrig.myfe.view.event.FeContentTableEvent;
import dev.alexengrig.myfe.view.event.FeContentTableListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableRowSorter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;

/**
 * View of file explorer content table.
 */
public class FeContentTable extends JTable {

    private final EventListenerGroup<FeContentTableListener, FeContentTableEvent> listenerGroup = new EventListenerGroup<>();

    public FeContentTable(FeContentTableModel model) {
        super(model);
        init();
    }

    private void init() {
        getTableHeader().setReorderingAllowed(false);
        // Remove default action on press Enter
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .getParent()
                .remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        initSorter();
        initListeners();
    }

    private void initSorter() {
        TableRowSorter<FeContentTableModel> sorter = new TableRowSorter<>(getModel());
        sorter.setRowFilter(new RowFilter<>() {

            @Override
            public boolean include(Entry<? extends FeContentTableModel, ? extends Integer> entry) {
                String filteredType = getModel().getFilteredType();
                if (filteredType == null) {
                    return true;
                }
                //TODO: Fix magic number - column of type
                String type = entry.getStringValue(1);
                return filteredType.equals(type);
            }

        });
        setRowSorter(sorter);
    }

    private void initListeners() {
        getSelectionModel().addListSelectionListener(new SelectPathListener());
        GoToPathListener goToPathListener = new GoToPathListener();
        addMouseListener(goToPathListener);
        addKeyListener(goToPathListener);
        getRowSorter().addRowSorterListener(new RowCountListener());
    }

    @Override
    public FeContentTableModel getModel() {
        return (FeContentTableModel) super.getModel();
    }

    private FePath getSelectedPath() {
        int rowIndex = getSelectedRow();
        int pathIndex = getRowSorter().convertRowIndexToModel(rowIndex);
        return getModel().getPathAt(pathIndex);
    }

    private void handleSelectPath(FePath path) {
        listenerGroup.fire(FeContentTableEvent.selectPath(path));
    }

    private void handleGoToPath(FePath path) {
        listenerGroup.fire(FeContentTableEvent.goToPath(path));
    }

    private void handleChangeRowCount(int rowCount) {
        listenerGroup.fire(FeContentTableEvent.changeRowCount(rowCount));
    }

    public void addFeContentTableListener(FeContentTableListener listener) {
        listenerGroup.add(listener);
    }

    public void removeFeContentTableListener(FeContentTableListener listener) {
        listenerGroup.remove(listener);
    }

    /**
     * On select a single row.
     */
    private class SelectPathListener implements ListSelectionListener {

        //TODO: Move delay
        private final DelayedSingleTaskExecutor timer = new DelayedSingleTaskExecutor(400);

        private FePath previousPath;

        @Override
        public void valueChanged(ListSelectionEvent ignore) {
            timer.execute(this::handleSelect);
        }

        private void handleSelect() {
            if (getSelectedRowCount() == 1) {
                FePath path = getSelectedPath();
                if (!Objects.equals(previousPath, path)) {
                    previousPath = path;
                    handleSelectPath(path);
                }
            }
        }

    }

    /**
     * On double-click the left mouse button and press the Enter key on a row.
     */
    private class GoToPathListener implements DoNothingMouseListener, DoNothingKeyListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1 && getSelectedRowCount() == 1) {
                FePath path = getSelectedPath();
                handleGoToPath(path);
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER && getSelectedRowCount() == 1) {
                FePath path = getSelectedPath();
                handleGoToPath(path);
            }
        }

    }

    /**
     * On change number of rows.
     */
    private class RowCountListener implements RowSorterListener {

        @Override
        public void sorterChanged(RowSorterEvent e) {
            int currentCount = getRowSorter().getViewRowCount();
            if (currentCount == 0 || currentCount != e.getPreviousRowCount()) {
                handleChangeRowCount(currentCount);
            }
        }

    }

}
