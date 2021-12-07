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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MyComboBoxModelTest {

    final List<String> elements = new LinkedList<>();
    final List<ListDataEvent> events = new LinkedList<>();

    final MyComboBoxModel<String> model = new MyComboBoxModel<>("default", () -> elements);

    ListDataListener listener;

    @BeforeEach
    void beforeEach() {
        listener = new ListDataListener() {

            @Override
            public void intervalAdded(ListDataEvent e) {
                fail();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                fail();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                events.add(e);
            }

        };
        model.addListDataListener(listener);
    }

    @Test
    void should_return_valueAndSelectedItem() {
        assertTrue(model.getValue().isEmpty(), "Has value");
        assertEquals("default", model.getSelectedItem(), "Default value");
    }

    @Test
    void should_select_item() {
        elements.add("first");
        model.setSelectedItem("first");
        assertEquals(1, events.size(), "Number of events");
        assertEquals("first", model.getSelectedItem(), "Selected item");
        assertEquals("first", model.getValue().orElseThrow(), "Value");
    }

}