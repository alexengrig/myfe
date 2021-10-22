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

import dev.alexengrig.myfe.model.MyPathFilterModel;
import dev.alexengrig.myfe.view.event.MyPathFilterEvent;
import dev.alexengrig.myfe.view.event.MyPathFilterListener;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class MyPathFilter extends JPanel {

    private final List<MyPathFilterListener> listeners = new LinkedList<>();

    private final MyPathFilterModel filterModel;

    public MyPathFilter(MyPathFilterModel filterModel) {
        super(new BorderLayout());
        this.filterModel = filterModel;
        init();
    }

    private void init() {
        add(new JLabel("Filter type: "), BorderLayout.WEST);
        TypeComboBoxModel typeComboBoxModel = new TypeComboBoxModel(filterModel.getTypes());
        filterModel.addMyPathFilterModelListener(event -> typeComboBoxModel.setTypes(event.getTypes()));
        add(new JComboBox<>(typeComboBoxModel)); //TODO: Add clear button
    }

    public void addMyPathFilterListener(MyPathFilterListener listener) {
        listeners.add(listener);
    }

    public void removeMyPathFilterListener(MyPathFilterListener listener) {
        listeners.remove(listener);
    }

    private void fireFilterType(MyPathFilterEvent event) {
        for (MyPathFilterListener listener : listeners) {
            listener.filterType(event);
        }
    }

    private class TypeComboBoxModel implements ComboBoxModel<String> {

        private final List<ListDataListener> listeners = new LinkedList<>();

        private List<String> types;
        private String selected;

        public TypeComboBoxModel(List<String> types) {
            this.types = types;
        }

        @Override
        public Object getSelectedItem() {
            return selected;
        }

        @Override
        public void setSelectedItem(Object anItem) {
            selected = anItem == null ? null : anItem.toString();
            fireFilterType(MyPathFilterEvent.type(selected));
        }

        @Override
        public int getSize() {
            return types.size();
        }

        @Override
        public String getElementAt(int index) {
            return types.get(index);
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            listeners.remove(l);
        }

        private void fireContentsChanged(ListDataEvent event) {
            for (ListDataListener listener : listeners) {
                listener.contentsChanged(event);
            }
        }

        public void setTypes(List<String> types) {
            this.selected = null;
            this.types = types;
            fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
        }

    }

}
