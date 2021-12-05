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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeContentTableModelTest {

    final List<TableModelEvent> events = new LinkedList<>();
    final TableModelListener listener = events::add;

    final FeContentTableModel model = new FeContentTableModel();

    @BeforeEach
    void beforeEach() {
        model.addTableModelListener(listener);
    }

    @Test
    void should_do_events_of_changePaths() {
        assertTrue(events.isEmpty(), "There are events");
        model.setPaths(Collections.emptyList());
        assertEquals(1, events.size(), "Number of events");
        events.clear();
        model.removeTableModelListener(listener);
        model.setPaths(Collections.emptyList());
        assertTrue(events.isEmpty(), "There are events");
    }

    @Test
    void should_set_filteredType() {
        // setup
        String type = "type";
        // run
        model.setFilteredType(type);
        // check
        assertEquals(type, model.getFilteredType(), "Filtered type");
        assertEquals(1, events.size(), "Number of events");
    }

    @Test
    void should_set_paths() {
        // setup
        List<FePath> paths = List.of(
                new FeDirectory("/path/to/directory", "directory"),
                new FeFile("/path/to/file", "file"),
                new FeFile("/path/to/file.this", "file.this"));
        // run
        model.setPaths(paths);
        // check
        assertNull(model.getFilteredType(), "Filtered type");
        assertSame(paths.get(0), model.getPathAt(0), "Path at 0");
        assertSame(paths.get(1), model.getPathAt(1), "Path at 1");
        assertSame(paths.get(2), model.getPathAt(2), "Path at 2");
    }

}