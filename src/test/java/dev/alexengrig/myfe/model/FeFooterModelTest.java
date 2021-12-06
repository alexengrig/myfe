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

import dev.alexengrig.myfe.model.event.FeFooterModelEvent;
import dev.alexengrig.myfe.model.event.FeFooterModelListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeFooterModelTest {

    final FeFooterModel model = new FeFooterModel(0);

    FeFooterModelListener listener;
    List<FeFooterModelEvent> events = new LinkedList<>();

    @BeforeEach
    void beforeEach() {
        listener = new FeFooterModelListener() {

            @Override
            public void changeTasks(FeFooterModelEvent event) {
                events.add(event);
            }

            @Override
            public void changeNumberOfElements(FeFooterModelEvent event) {
                events.add(event);
            }

        };
        model.addFeFooterModelListener(listener);
    }

    @Test
    void should_do_event() {
        assertTrue(events.isEmpty(), "There are events");
        model.setNumberOfElements(1);
        assertEquals(1, events.size(), "Number of events");
        model.removeFeFooterModelListener(listener);
        events.clear();
        model.setNumberOfElements(2);
        assertTrue(events.isEmpty(), "There are events");
    }

    @Test
    void should_add_task() {
        String task = "Test task";
        model.addTask(() -> task);
        assertEquals(1, events.size(), "Number of events");
        FeFooterModelEvent event = events.get(0);
        assertIterableEquals(Collections.singletonList(task), event.getTasks(), "Tasks");
    }

    @Test
    void should_remove_firstTask() {
        String task = "Test task";
        model.addTask(() -> task);
        model.addTask(() -> task);
        assertEquals(2, events.size(), "Number of events");
        FeFooterModelEvent event = events.get(1);
        assertIterableEquals(Arrays.asList(task, task), event.getTasks(), "Tasks");
        events.clear();
        model.removeTask(() -> task);
        assertEquals(1, events.size(), "Number of events");
        event = events.get(0);
        assertIterableEquals(Collections.singletonList(task), event.getTasks(), "Tasks");
    }

    @Test
    void should_set_numberOfElements() {
        int count = 10;
        model.setNumberOfElements(count);
        assertEquals(count, model.getNumberOfElements(), "Number of elements from model");
        assertEquals(1, events.size(), "Number of events");
        FeFooterModelEvent event = events.get(0);
        assertEquals(count, event.getNumberOfElements(), "Number of elements from event");
    }

}