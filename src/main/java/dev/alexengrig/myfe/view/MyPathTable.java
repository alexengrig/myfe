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
import dev.alexengrig.myfe.model.MyPathTableModel;
import dev.alexengrig.myfe.view.event.DoNothingKeyListener;
import dev.alexengrig.myfe.view.event.DoNothingMouseListener;
import dev.alexengrig.myfe.view.event.MyPathTableEvent;
import dev.alexengrig.myfe.view.event.MyPathTableListener;
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

public class MyPathTable extends JTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<MyPathTableListener> listeners = new LinkedList<>();

    public MyPathTable(MyPathTableModel model) {
        super(model);
        init();
    }

    private void init() {
        getTableHeader().setReorderingAllowed(false);
        initSorter();
        initListeners();
    }

    private void initSorter() {
        TableRowSorter<MyPathTableModel> sorter = new TableRowSorter<>(getModel());
        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends MyPathTableModel, ? extends Integer> entry) {
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
    public MyPathTableModel getModel() {
        return (MyPathTableModel) super.getModel();
    }

    private FePath getSelectedPath() {
        int rowIndex = getSelectedRow();
        int pathIndex = getRowSorter().convertRowIndexToModel(rowIndex);
        return getModel().getPathAt(pathIndex);
    }

    public void addMyPathTableListener(MyPathTableListener listener) {
        listeners.add(listener);
    }

    public void removeMyPathTableListener(MyPathTableListener listener) {
        listeners.remove(listener);
    }

    private void fireSelectPath(MyPathTableEvent event) {
        LOGGER.debug("Fire select path: {}", event);
        for (MyPathTableListener listener : listeners) {
            listener.selectPath(event);
        }
    }

    private void fireDoubleClickOnPath(MyPathTableEvent event) {
        LOGGER.debug("Fire double click on path: {}", event);
        for (MyPathTableListener listener : listeners) {
            listener.doubleClickOnPath(event);
        }
    }

    private void fireGoBack(MyPathTableEvent event) {
        LOGGER.debug("Fire go back: {}", event);
        for (MyPathTableListener listener : listeners) {
            listener.goBack(event);
        }
    }

    private void fireChangeRowCount(MyPathTableEvent event) {
        LOGGER.debug("Fire change row count: {}", event);
        for (MyPathTableListener listener : listeners) {
            listener.changeRowCount(event);
        }
    }

    /**
     * On select a single row.
     *
     * @see MyPathTable#fireSelectPath(MyPathTableEvent)
     */
    private class SelectPathListener implements ListSelectionListener {

        private transient FePath previousPath;

        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (getSelectedRowCount() == 1) {
                FePath path = getSelectedPath();
                //FIXME: Don't handle same path
                if (Objects.equals(previousPath, path)) {
                    return;
                }
                previousPath = path;
                fireSelectPath(new MyPathTableEvent(path));
            }
        }

    }

    /**
     * On double-click the left mouse button and press the Enter key on a row.
     *
     * @see MyPathTable#fireDoubleClickOnPath(MyPathTableEvent)
     */
    private class GoToPathListener implements DoNothingMouseListener, DoNothingKeyListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1 && getSelectedRowCount() == 1) {
                FePath path = getSelectedPath();
                fireDoubleClickOnPath(new MyPathTableEvent(path));
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER && getSelectedRowCount() == 1) {
                FePath path = getSelectedPath();
                fireDoubleClickOnPath(new MyPathTableEvent(path));
            }
        }

    }

    /**
     * On press the Backspace key.
     *
     * @see MyPathTable#fireGoBack(MyPathTableEvent)
     */
    private class GoBackListener implements DoNothingKeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            //TODO: As global for tab
            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                fireGoBack(new MyPathTableEvent());
            }
        }

    }

    /**
     * On change number of rows.
     *
     * @see MyPathTable#fireChangeRowCount(MyPathTableEvent)
     */
    private class RowCountListener implements RowSorterListener {

        @Override
        public void sorterChanged(RowSorterEvent e) {
            int currentCount = getRowSorter().getViewRowCount();
            if (currentCount == 0 || currentCount != e.getPreviousRowCount()) {
                fireChangeRowCount(new MyPathTableEvent(currentCount));
            }
        }

    }

}
