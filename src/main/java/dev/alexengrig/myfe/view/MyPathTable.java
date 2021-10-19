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

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.LinkedList;
import java.util.List;

public class MyPathTable extends JTable {

    private final List<MyPathTableListener> listeners;

    public MyPathTable(MyPathTableModel model) {
        super(model);
        this.listeners = new LinkedList<>();
        init();
    }

    @Override
    public MyPathTableModel getModel() {
        return (MyPathTableModel) super.getModel();
    }

    private void init() {
        getSelectionModel().addListSelectionListener(new SelectSingleRowListener());
    }

    private void handleSelectPath(MyPath path) {
        fireSelectPath(new MyPathTableEvent(path));
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

    /**
     * On select a single row.
     *
     * @see MyPathTable#handleSelectPath(MyPath)
     */
    private class SelectSingleRowListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (getSelectedRowCount() != 1) return;
            int rowIndex = getSelectedRow();
            MyPath path = getModel().getPathAt(rowIndex);
            handleSelectPath(path);
        }

    }

}
