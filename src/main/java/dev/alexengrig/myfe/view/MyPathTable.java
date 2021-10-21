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
import dev.alexengrig.myfe.model.MyPathTableModel;
import dev.alexengrig.myfe.view.event.DoNothingKeyListener;
import dev.alexengrig.myfe.view.event.DoNothingMouseListener;
import dev.alexengrig.myfe.view.event.MyPathTableEvent;
import dev.alexengrig.myfe.view.event.MyPathTableListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

public class MyPathTable extends JTable {

    private final List<MyPathTableListener> listeners;

    public MyPathTable(MyPathTableModel model) {
        super(model);
        this.listeners = new LinkedList<>();
        init();
    }

    private void init() {
        initListeners();
    }

    private void initListeners() {
        getSelectionModel().addListSelectionListener(new SelectPathListener());
        GoToPathListener goToPathListener = new GoToPathListener();
        addMouseListener(goToPathListener);
        addKeyListener(goToPathListener);
        addKeyListener(new GoBackListener());
    }

    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new MyPathTableHeader(columnModel);
    }

    @Override
    public MyPathTableModel getModel() {
        return (MyPathTableModel) super.getModel();
    }

    private void handleSelectPath(MyPath path) {
        fireSelectPath(new MyPathTableEvent(path));
    }

    private void handleGoToPath(MyPath path) {
        fireDoubleClickOnPath(new MyPathTableEvent(path));
    }

    private void handleGoBack() {
        fireGoBack(new MyPathTableEvent());
    }

    public void addMyPathTableListener(MyPathTableListener listener) {
        listeners.add(listener);
    }

    public void removeMyPathTableListener(MyPathTableListener listener) {
        listeners.remove(listener);
    }

    private void fireSelectPath(MyPathTableEvent event) {
        for (MyPathTableListener listener : listeners) {
            listener.selectPath(event);
        }
    }

    private void fireDoubleClickOnPath(MyPathTableEvent event) {
        for (MyPathTableListener listener : listeners) {
            listener.doubleClickOnPath(event);
        }
    }

    private void fireGoBack(MyPathTableEvent event) {
        for (MyPathTableListener listener : listeners) {
            listener.goBack(event);
        }
    }

    /**
     * On select a single row.
     *
     * @see MyPathTable#handleSelectPath(MyPath)
     */
    private class SelectPathListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (getSelectedRowCount() != 1) return;
            int rowIndex = getSelectedRow();
            MyPath path = getModel().getPathAt(rowIndex);
            handleSelectPath(path);
        }

    }

    /**
     * On double-click the left mouse button and press the Enter key on a row.
     *
     * @see MyPathTable#handleGoToPath(MyPath)
     */
    private class GoToPathListener implements DoNothingMouseListener, DoNothingKeyListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1 && getSelectedRowCount() == 1) {
                int rowIndex = MyPathTable.this.rowAtPoint(e.getPoint());
                MyPathTableModel model = getModel();
                MyPath path = model.getPathAt(rowIndex);
                handleGoToPath(path);
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER && getSelectedRowCount() == 1) {
                int rowIndex = getSelectedRow();
                MyPathTableModel model = getModel();
                MyPath path = model.getPathAt(rowIndex);
                handleGoToPath(path);
            }
        }

    }

    /**
     * On press the Backspace key.
     *
     * @see MyPathTable#handleGoBack
     */
    private class GoBackListener implements DoNothingKeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                handleGoBack();
            }
        }

    }

}
