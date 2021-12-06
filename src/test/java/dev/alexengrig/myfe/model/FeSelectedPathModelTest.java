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

import dev.alexengrig.myfe.domain.FeDirectory;
import dev.alexengrig.myfe.domain.FeFile;
import dev.alexengrig.myfe.model.event.FeSelectedPathModelEvent;
import dev.alexengrig.myfe.model.event.FeSelectedPathModelListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeSelectedPathModelTest {

    final FeSelectedPathModel model = new FeSelectedPathModel();

    final LinkedList<FeSelectedPathModelEvent> events = new LinkedList<>();
    final FeSelectedPathModelListener listener = events::add;

    @BeforeEach
    void beforeEach() {
        model.addSelectedFePathModelListener(listener);
    }

    @Test
    void should_do_event() {
        assertTrue(events.isEmpty(), "There are events");
        model.setPath(new FeDirectory("/", "/"));
        assertEquals(1, events.size(), "Number of events");
        events.clear();
        model.removeSelectedFePathModelListener(listener);
        model.setPath(new FeDirectory("/pub", "pub"));
        assertTrue(events.isEmpty(), "There are events");
    }

    @Test
    void should_set_path() {
        FeFile file = new FeFile("/file.this", "file.this");
        assertTrue(model.isEmpty(), "There is selected path");
        model.setPath(file);
        assertSame(file, model.getPath(), "Path from model");
        assertEquals(1, events.size(), "Number of events");
        FeSelectedPathModelEvent event = events.get(0);
        assertSame(file, event.getPath(), "Path from event");
        events.clear();
        model.setPath(file);
        assertTrue(events.isEmpty(), "There are events");
    }

}