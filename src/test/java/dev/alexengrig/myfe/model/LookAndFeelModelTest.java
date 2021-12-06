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

import dev.alexengrig.myfe.model.event.LookAndFeelModelEvent;
import dev.alexengrig.myfe.model.event.LookAndFeelModelListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LookAndFeelModelTest {

    static UIManager.LookAndFeelInfo[] LAFs = UIManager.getInstalledLookAndFeels();

    final LookAndFeelModel model = new LookAndFeelModel(LAFs[0].getClassName());

    final LinkedList<LookAndFeelModelEvent> events = new LinkedList<>();
    final LookAndFeelModelListener listener = events::add;

    @BeforeEach
    void beforeEach() {
        model.addLookAndFeelModelListener(listener);
    }

    @Test
    void should_do_event() {
        assertTrue(events.isEmpty(), "There are events");
        UIManager.LookAndFeelInfo firstLaf = LAFs[1];
        model.setByName(firstLaf.getName());
        assertEquals(1, events.size(), "Number of events");
        events.clear();
        model.removeLookAndFeelModelListener(listener);
        UIManager.LookAndFeelInfo secondLaf = LAFs[2];
        model.setByName(secondLaf.getName());
        assertTrue(events.isEmpty(), "There are events");
    }

    @Test
    void should_return_lafInfo() {
        assertEquals(LAFs[0].getClassName(), model.getCurrentClassName(), "Class name");
        assertEquals(LAFs[0].getName(), model.getCurrentName(), "Name");
    }

    @Test
    void should_set_laf_by_name() {
        UIManager.LookAndFeelInfo laf = LAFs[1];
        model.setByName(laf.getName());
        assertEquals(laf.getClassName(), model.getCurrentClassName(), "Class name from model");
        assertEquals(laf.getName(), model.getCurrentName(), "Name from model");
        assertEquals(1, events.size(), "Number of events");
        LookAndFeelModelEvent event = events.get(0);
        assertEquals(laf.getClassName(), event.getClassName(), "Class name from event");
        assertEquals(laf.getName(), event.getName(), "Name from event");
    }

}