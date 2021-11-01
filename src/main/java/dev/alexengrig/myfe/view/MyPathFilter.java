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

import dev.alexengrig.myfe.model.FeContentFilterModel;
import dev.alexengrig.myfe.view.event.MyPathFilterEvent;
import dev.alexengrig.myfe.view.event.MyPathFilterListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

public class MyPathFilter extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<MyPathFilterListener> listeners = new LinkedList<>();

    private final FeContentFilterModel filterModel;

    public MyPathFilter(FeContentFilterModel filterModel) {
        super(new BorderLayout());
        this.filterModel = filterModel;
        init();
    }

    private void init() {
        add(new JLabel("Filter type: "), BorderLayout.WEST);
        TypeComboBoxModel typeComboBoxModel = new TypeComboBoxModel();
        filterModel.addFeContentFilterModelListener(event -> typeComboBoxModel.reset());
        add(new JComboBox<>(typeComboBoxModel));
    }

    public void addMyPathFilterListener(MyPathFilterListener listener) {
        listeners.add(listener);
    }

    public void removeMyPathFilterListener(MyPathFilterListener listener) {
        listeners.remove(listener);
    }

    private void fireFilterType(MyPathFilterEvent event) {
        LOGGER.debug("Fire filter type: {}", event);
        for (MyPathFilterListener listener : listeners) {
            listener.filterType(event);
        }
    }

    private class TypeComboBoxModel implements ComboBoxModel<String> {

        private static final String DEFAULT_ITEM = "All";

        private final List<ListDataListener> listeners = new LinkedList<>();

        private String selected = DEFAULT_ITEM;

        @Override
        public Object getSelectedItem() {
            return selected;
        }

        @Override
        public void setSelectedItem(Object anItem) {
            selected = anItem.toString();
            @SuppressWarnings("StringEquality")
            String payload = selected == DEFAULT_ITEM ? null : selected;
            fireFilterType(MyPathFilterEvent.type(payload));
        }

        @Override
        public int getSize() {
            return 1 + filterModel.getTypes().size();
        }

        @Override
        public String getElementAt(int index) {
            return index == 0 ? DEFAULT_ITEM : filterModel.getTypes().get(index - 1);
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

        public void reset() {
            this.selected = DEFAULT_ITEM;
            fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
        }

    }

}
