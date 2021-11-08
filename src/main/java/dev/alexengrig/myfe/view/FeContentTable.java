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

import dev.alexengrig.myfe.model.FeContentTableModel;
import dev.alexengrig.myfe.model.FePath;
import dev.alexengrig.myfe.util.swing.DelayedSingleTaskExecutor;
import dev.alexengrig.myfe.view.event.DoNothingKeyListener;
import dev.alexengrig.myfe.view.event.DoNothingMouseListener;
import dev.alexengrig.myfe.view.event.FeContentTableEvent;
import dev.alexengrig.myfe.view.event.FeContentTableListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableRowSorter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class FeContentTable extends JTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<FeContentTableListener> listeners = new LinkedList<>();

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
        addKeyListener(new GoBackListener());
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

    public void addFeContentTableListener(FeContentTableListener listener) {
        listeners.add(listener);
    }

    public void removeFeContentTableListener(FeContentTableListener listener) {
        listeners.remove(listener);
    }

    private void fireSelectPath(FeContentTableEvent event) {
        LOGGER.debug("Fire select path: {}", event);
        for (FeContentTableListener listener : listeners) {
            listener.selectPath(event);
        }
    }

    private void fireGoToPath(FeContentTableEvent event) {
        LOGGER.debug("Fire go to path: {}", event);
        for (FeContentTableListener listener : listeners) {
            listener.goToPath(event);
        }
    }

    private void fireGoBack(FeContentTableEvent event) {
        LOGGER.debug("Fire go back: {}", event);
        for (FeContentTableListener listener : listeners) {
            listener.goBack(event);
        }
    }

    private void fireChangeRowCount(FeContentTableEvent event) {
        LOGGER.debug("Fire change row count: {}", event);
        for (FeContentTableListener listener : listeners) {
            listener.changeRowCount(event);
        }
    }

    /**
     * On select a single row.
     *
     * @see FeContentTable#fireSelectPath(FeContentTableEvent)
     */
    private class SelectPathListener implements ListSelectionListener {

        private final DelayedSingleTaskExecutor timer = new DelayedSingleTaskExecutor(400);

        private transient FePath previousPath;

        @Override
        public void valueChanged(ListSelectionEvent ignore) {
            timer.execute(this::handleSelect);
        }

        private void handleSelect() {
            if (getSelectedRowCount() == 1) {
                FePath path = getSelectedPath();
                if (!Objects.equals(previousPath, path)) {
                    previousPath = path;
                    fireSelectPath(new FeContentTableEvent(path));
                }
            }
        }

    }

    /**
     * On double-click the left mouse button and press the Enter key on a row.
     *
     * @see FeContentTable#fireGoToPath(FeContentTableEvent)
     */
    private class GoToPathListener implements DoNothingMouseListener, DoNothingKeyListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1 && getSelectedRowCount() == 1) {
                FePath path = getSelectedPath();
                fireGoToPath(new FeContentTableEvent(path));
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER && getSelectedRowCount() == 1) {
                FePath path = getSelectedPath();
                fireGoToPath(new FeContentTableEvent(path));
            }
        }

    }

    /**
     * On press the Backspace key.
     *
     * @see FeContentTable#fireGoBack(FeContentTableEvent)
     */
    private class GoBackListener implements DoNothingKeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            //TODO: As global for tab
            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                fireGoBack(new FeContentTableEvent());
            }
        }

    }

    /**
     * On change number of rows.
     *
     * @see FeContentTable#fireChangeRowCount(FeContentTableEvent)
     */
    private class RowCountListener implements RowSorterListener {

        @Override
        public void sorterChanged(RowSorterEvent e) {
            int currentCount = getRowSorter().getViewRowCount();
            if (currentCount == 0 || currentCount != e.getPreviousRowCount()) {
                fireChangeRowCount(new FeContentTableEvent(currentCount));
            }
        }

    }

}
