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
import dev.alexengrig.myfe.model.event.FeContentFilterModelEvent;
import dev.alexengrig.myfe.model.event.FeContentFilterModelListener;
import dev.alexengrig.myfe.view.event.FeContentFilterEvent;
import dev.alexengrig.myfe.view.event.FeContentFilterListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class FeContentFilter extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<FeContentFilterListener> listeners = new LinkedList<>();

    private final FeContentFilterModel model;
    private final TypeModel typeModel;

    public FeContentFilter(FeContentFilterModel model) {
        super(new BorderLayout());
        this.model = model;
        this.typeModel = new TypeModel();
        init();
    }

    private void init() {
        setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        add(new JLabel("Filter type: "), BorderLayout.WEST);
        model.addFeContentFilterModelListener(new ModelListener());
        add(new JComboBox<>(typeModel));
    }

    public void addFeContentFilterListener(FeContentFilterListener listener) {
        listeners.add(listener);
    }

    public void removeFeContentFilterListener(FeContentFilterListener listener) {
        listeners.remove(listener);
    }

    private void fireChangeType(FeContentFilterEvent event) {
        LOGGER.debug("Fire change type: {}", event);
        for (FeContentFilterListener listener : listeners) {
            listener.changeType(event);
        }
    }

    private class ModelListener implements FeContentFilterModelListener {

        @Override
        public void changeTypes(FeContentFilterModelEvent ignore) {
            typeModel.reset();
        }

    }

    private class TypeModel implements ComboBoxModel<String> {

        private static final String DEFAULT_ITEM = "All";

        private final List<ListDataListener> listeners = new LinkedList<>();

        private String selected = DEFAULT_ITEM;

        public void reset() {
            this.selected = DEFAULT_ITEM;
            fireContentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
        }

        @Override
        public Object getSelectedItem() {
            return selected;
        }

        @Override
        public void setSelectedItem(Object anItem) {
            String selected = anItem.toString();
            if (!Objects.equals(this.selected, selected)) {
                this.selected = selected;
                String payload = DEFAULT_ITEM.equals(selected) ? null : selected;
                fireChangeType(FeContentFilterEvent.type(payload));
            }
        }

        @Override
        public int getSize() {
            int defaultSize = 1;
            return defaultSize + model.getTypes().size();
        }

        @Override
        public String getElementAt(int index) {
            return index == 0 ? DEFAULT_ITEM : model.getTypes().get(index - 1);
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

    }

}
