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

import dev.alexengrig.myfe.domain.FeFile;
import dev.alexengrig.myfe.model.FeContentTableModel;
import dev.alexengrig.myfe.view.event.FeContentTableEvent;
import dev.alexengrig.myfe.view.event.FeContentTableListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class FeContentTableTest {

    FeContentTableModel model;
    List<FeContentTableEvent> events;
    FeContentTableListener listener;

    FeContentTable view;

    @BeforeEach
    void beforeEach() {
        model = new FeContentTableModel();
        view = new FeContentTable(model);
        events = new LinkedList<>();
        listener = new FeContentTableListener() {

            @Override
            public void selectPath(FeContentTableEvent event) {
                events.add(event);
            }

            @Override
            public void goToPath(FeContentTableEvent event) {
                events.add(event);
            }

            @Override
            public void changeRowCount(FeContentTableEvent event) {
                events.add(event);
            }

        };
        view.addFeContentTableListener(listener);
    }

    @Test
    void should_select_path() throws InterruptedException {
        FeFile file = new FeFile("/file.this", "file.this");
        model.setPaths(Collections.singletonList(file));
        assertEquals(ListSelectionModel.SINGLE_SELECTION, view.getSelectionModel().getSelectionMode(), "Selection mode");
        events.clear();
        view.getSelectionModel().setSelectionInterval(0, 0);
        Thread.sleep(500); // wait select timeout
        assertEquals(1, events.size(), "Number of events");
        FeContentTableEvent event = events.get(0);
        assertSame(file, event.getPath(), "Selected file");
    }

    @Disabled("Mouse click / Enter press")
    @Test
    void should_go_to_path() throws AWTException, InterruptedException {
        FeFile file = new FeFile("/file.this", "file.this");
        model.setPaths(Collections.singletonList(file));
        events.clear();
        //TODO: Click / press
        Thread.sleep(500); // wait select timeout
        assertEquals(1, events.size(), "Number of events");
        FeContentTableEvent event = events.get(0);
        assertSame(file, event.getPath(), "Go to file");
    }

    @Test
    void should_change_rowCount() {
        FeFile file = new FeFile("/file.this", "file.this");
        model.setPaths(Collections.singletonList(file));
        assertEquals(1, events.size(), "Number of events");
        FeContentTableEvent event = events.get(0);
        assertEquals(1, event.getRowCount(), "Row count");
    }

}