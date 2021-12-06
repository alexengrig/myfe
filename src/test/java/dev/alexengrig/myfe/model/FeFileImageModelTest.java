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

import dev.alexengrig.myfe.domain.FeFile;
import dev.alexengrig.myfe.model.event.FeFileImageModelEvent;
import dev.alexengrig.myfe.model.event.FeFileImageModelListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeFileImageModelTest {

    final FeFileImageModel model = new FeFileImageModel();

    final LinkedList<FeFileImageModelEvent> events = new LinkedList<>();
    final FeFileImageModelListener listener = events::add;

    @BeforeEach
    void beforeEach() {
        model.addFeFileImageModelListener(listener);
    }

    @Test
    void should_do_event() {
        model.removeFeFileImageModelListener(listener);
        model.setFileData(new FeFile("/file.this", "file.this"), new byte[0]);
        assertTrue(events.isEmpty(), "There are events");
    }

    @Test
    void should_set_fileData() {
        FeFile file = new FeFile("/file.this", "file.this");
        byte[] data = {1, 2, 3};
        model.setFileData(file, data);
        assertEquals(1, events.size(), "Number of events");
        FeFileImageModelEvent event = events.get(0);
        assertSame(file, event.getFile(), "File");
        assertArrayEquals(data, event.getData(), "Data");
    }

}