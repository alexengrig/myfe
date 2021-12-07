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

package dev.alexengrig.myfe.model;

import dev.alexengrig.myfe.util.swing.SwingEventListenerGroup;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Implementation of {@link ComboBoxModel}.
 *
 * @param <T> the type of element
 */
public class MyComboBoxModel<T> implements ComboBoxModel<T> {

    private final SwingEventListenerGroup<ListDataListener, ListDataEvent> listenerGroup = new SwingEventListenerGroup<>();

    private final T defaultValue;
    private final Supplier<List<T>> elementsSupplier;

    private T value;

    public MyComboBoxModel(T defaultValue, Supplier<List<T>> elementsSupplier) {
        this.defaultValue = defaultValue;
        this.elementsSupplier = elementsSupplier;
    }

    private List<T> elements() {
        return elementsSupplier.get();
    }

    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    public void setDefault() {
        this.value = null;
    }

    @Override
    public Object getSelectedItem() {
        return value != null ? value : defaultValue;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        @SuppressWarnings("unchecked")
        T newValue = (T) anItem;
        if (!Objects.equals(newValue, value)) {
            this.value = defaultValue.equals(newValue) ? null : newValue;
            listenerGroup.fire(l -> l::contentsChanged, new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, -1, -1));
        }
    }

    @Override
    public int getSize() {
        return elements().size();
    }

    @Override
    public T getElementAt(int index) {
        return elements().get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listenerGroup.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listenerGroup.add(l);
    }
}
