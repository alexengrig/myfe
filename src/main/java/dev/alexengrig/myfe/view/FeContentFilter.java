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
import dev.alexengrig.myfe.model.MyComboBoxModel;
import dev.alexengrig.myfe.model.event.FeContentFilterModelEvent;
import dev.alexengrig.myfe.model.event.FeContentFilterModelListener;
import dev.alexengrig.myfe.util.event.EventListenerGroup;
import dev.alexengrig.myfe.view.event.FeContentFilterEvent;
import dev.alexengrig.myfe.view.event.FeContentFilterListener;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;

/**
 * View of file explorer content filter.
 */
public class FeContentFilter extends JPanel {

    private final EventListenerGroup<FeContentFilterListener, FeContentFilterEvent> listenerGroup = new EventListenerGroup<>();

    private final FeContentFilterModel model;
    private final MyComboBoxModel<String> typeModel;

    public FeContentFilter(FeContentFilterModel model) {
        this(model, new MyComboBoxModel<>("All", model::getTypes));
    }

    protected FeContentFilter(FeContentFilterModel model, MyComboBoxModel<String> typeModel) {
        super(new BorderLayout());
        this.model = model;
        this.typeModel = typeModel;
        init();
    }

    private void init() {
        setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        add(new JLabel("Filter type: "), BorderLayout.WEST);
        model.addFeContentFilterModelListener(new ModelListener());
        typeModel.addListDataListener(new TypeModelListener());
        add(new JComboBox<>(typeModel));
    }

    private void handleSelectType(String type) {
        listenerGroup.fire(FeContentFilterEvent.type(type));
    }

    public void addFeContentFilterListener(FeContentFilterListener listener) {
        listenerGroup.add(listener);
    }

    public void removeFeContentFilterListener(FeContentFilterListener listener) {
        listenerGroup.remove(listener);
    }

    private class ModelListener implements FeContentFilterModelListener {

        @Override
        public void changeTypes(FeContentFilterModelEvent ignore) {
            typeModel.setDefault();
        }

    }

    private class TypeModelListener implements ListDataListener {

        @Override
        public void contentsChanged(ListDataEvent e) {
            handleSelectType(typeModel.getValue().orElse(null));
        }

        @Override
        public void intervalAdded(ListDataEvent e) {
            // do nothing
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            // do nothing
        }

    }

}
