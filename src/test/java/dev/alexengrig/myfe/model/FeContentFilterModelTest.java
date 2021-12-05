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
import dev.alexengrig.myfe.domain.FePath;
import dev.alexengrig.myfe.model.event.FeContentFilterModelEvent;
import dev.alexengrig.myfe.model.event.FeContentFilterModelListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeContentFilterModelTest {

    final List<FeContentFilterModelEvent> events = new LinkedList<>();
    final FeContentFilterModelListener listener = events::add;

    final FeContentFilterModel model = new FeContentFilterModel();

    @BeforeEach
    void beforeEach() {
        model.addFeContentFilterModelListener(listener);
    }

    @Test
    void should_do_events_of_changeTypes() {
        assertTrue(events.isEmpty(), "There are events");
        List<String> expectedTypes = Collections.singletonList("type");
        model.setTypes(expectedTypes);
        assertEquals(1, events.size(), "Number of events");
        FeContentFilterModelEvent event = events.get(0);
        assertEquals(expectedTypes, event.getTypes(), "Types from event");
        events.clear();
        model.removeFeContentFilterModelListener(listener);
        model.setTypes(expectedTypes);
        assertTrue(events.isEmpty(), "There are events");
    }

    @Test
    void should_set_types() {
        // setup
        List<String> expectedTypes = List.of("first", "second");
        // run
        model.setTypes(expectedTypes);
        // check
        assertIterableEquals(expectedTypes, model.getTypes(), "Types from model");
        assertEquals(1, events.size(), "Number of events");
        FeContentFilterModelEvent event = events.get(0);
        assertIterableEquals(expectedTypes, event.getTypes(), "Types from event");
    }

    @Test
    void should_set_paths() {
        // setup
        List<? extends FePath> paths = List.of(
                new FeDirectory("/path/to/directory", "directory"),
                new FeFile("/path/to/file", "file"),
                new FeFile("/path/to/file.this", "file.this"));
        List<String> expectedTypes = List.of("File", "File folder", "THIS file");
        // run
        model.setPaths(paths);
        // check
        assertIterableEquals(expectedTypes, model.getTypes(), "Types from model");
        assertEquals(1, events.size(), "Number of events");
        FeContentFilterModelEvent event = events.get(0);
        assertIterableEquals(expectedTypes, event.getTypes(), "Types from event");
    }

}