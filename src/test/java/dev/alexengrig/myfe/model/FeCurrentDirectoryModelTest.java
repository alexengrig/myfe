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
import dev.alexengrig.myfe.model.event.FeCurrentDirectoryModelEvent;
import dev.alexengrig.myfe.model.event.FeCurrentDirectoryModelListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeCurrentDirectoryModelTest {

    static final String ROOT_NAME = "test-root";
    static final String GO_TO_ROOT = "goToRoot";
    static final String GO_TO_DIRECTORY = "goToDirectory";
    static final String REFRESH = "refresh";

    final FeCurrentDirectoryModel model = new FeCurrentDirectoryModel(ROOT_NAME);

    final Map<String, List<FeCurrentDirectoryModelEvent>> eventsByName = new HashMap<>();

    FeCurrentDirectoryModelListener listener;

    @BeforeEach
    void beforeEach() {
        listener = new FeCurrentDirectoryModelListener() {

            @Override
            public void goToRoot(FeCurrentDirectoryModelEvent event) {
                eventsByName.computeIfAbsent(GO_TO_ROOT, k -> new LinkedList<>()).add(event);
            }

            @Override
            public void goToDirectory(FeCurrentDirectoryModelEvent event) {
                eventsByName.computeIfAbsent(GO_TO_DIRECTORY, k -> new LinkedList<>()).add(event);
            }

            @Override
            public void refresh(FeCurrentDirectoryModelEvent event) {
                eventsByName.computeIfAbsent(REFRESH, k -> new LinkedList<>()).add(event);
            }

        };
        model.addFeCurrentDirectoryModelListener(listener);
    }

    @Test
    void should_return_rootName() {
        assertEquals(ROOT_NAME, model.getRootName(), "Root name");
    }

    @Test
    void should_return_rootDirectory() {
        Optional<FeDirectory> optionalDirectory = model.getCurrentDirectory();
        assertTrue(optionalDirectory.isEmpty(), "It isn't root");
    }

    @Test
    void should_go_to_directory() {
        Optional<FeDirectory> optionalDirectory = model.getCurrentDirectory();
        assertTrue(optionalDirectory.isEmpty(), "It isn't root");
        goToDirectory(new FeDirectory("/", "/"));
    }

    @Test
    void should_go_to_root() {
        Optional<FeDirectory> optionalDirectory = model.getCurrentDirectory();
        assertTrue(optionalDirectory.isEmpty(), "It isn't root");
        goToDirectory(new FeDirectory("/", "/"));
        goToRoot();
    }

    @Test
    void should_go_back() {
        Optional<FeDirectory> optionalDirectory = model.getCurrentDirectory();
        assertTrue(optionalDirectory.isEmpty(), "It isn't root");
        FeDirectory directory = new FeDirectory("/", "/");
        goToDirectory(directory);
        assertTrue(model.canGoBack(), "Can't go back");
        model.goBack();
        optionalDirectory = model.getCurrentDirectory();
        assertTrue(optionalDirectory.isEmpty(), "It isn't root");
    }

    @Test
    void should_go_forward() {
        Optional<FeDirectory> optionalDirectory = model.getCurrentDirectory();
        assertTrue(optionalDirectory.isEmpty(), "It isn't root");
        FeDirectory directory = new FeDirectory("/", "/");
        goToDirectory(directory);
        assertTrue(model.canGoBack(), "Can't go back");
        model.goBack();
        optionalDirectory = model.getCurrentDirectory();
        assertTrue(optionalDirectory.isEmpty(), "It isn't root");
        List<FeCurrentDirectoryModelEvent> eventsOfGoToRoot = getEventsOfGoToRoot();
        assertEquals(1, eventsOfGoToRoot.size(), "Number of events of goToRoot");
        FeCurrentDirectoryModelEvent eventOfGoToRoot = eventsOfGoToRoot.get(0);
        assertNull(eventOfGoToRoot.getDirectory(), "Directory from event");
        assertTrue(model.canGoForward(), "Can't go forward");
        model.goForward();
        optionalDirectory = model.getCurrentDirectory();
        assertTrue(optionalDirectory.isPresent(), "It isn't directory");
        assertSame(directory, optionalDirectory.get(), "Current directory");
        List<FeCurrentDirectoryModelEvent> eventsOfGoToDirectory = getEventsOfGoToDirectory();
        assertEquals(1, eventsOfGoToDirectory.size(), "Number of events of goToDirectory");
        FeCurrentDirectoryModelEvent eventOfGoToDirectory = eventsOfGoToDirectory.get(0);
        assertSame(directory, eventOfGoToDirectory.getDirectory(), "Directory from event");
    }

    @Test
    void should_go_up() {
        Optional<FeDirectory> optionalDirectory = model.getCurrentDirectory();
        assertTrue(optionalDirectory.isEmpty(), "It isn't root");
        goToDirectory(new FeDirectory("/directory", "directory"));
        assertTrue(model.canGoUp(), "Can't go up");
        model.goUp();
        optionalDirectory = model.getCurrentDirectory();
        assertTrue(optionalDirectory.isPresent(), "It isn't directory");
        FeDirectory directory = new FeDirectory("/", "/");
        assertEquals(directory, optionalDirectory.get(), "Current directory");
        List<FeCurrentDirectoryModelEvent> eventsOfGoToDirectory = getEventsOfGoToDirectory();
        assertEquals(1, eventsOfGoToDirectory.size(), "Number of events of goToDirectory");
        FeCurrentDirectoryModelEvent eventOfGoToDirectory = eventsOfGoToDirectory.get(0);
        assertEquals(directory, eventOfGoToDirectory.getDirectory(), "Directory from event");
    }

    @Test
    void should_update() {
        model.update();
        List<FeCurrentDirectoryModelEvent> events = getEventsOfRefresh();
        assertEquals(1, events.size(), "Number of events");
    }

    private List<FeCurrentDirectoryModelEvent> getEventsOfGoToRoot() {
        List<FeCurrentDirectoryModelEvent> events = eventsByName.get(GO_TO_ROOT);
        assertNotNull(events, "Events of goToRoot");
        return events;
    }

    private List<FeCurrentDirectoryModelEvent> getEventsOfGoToDirectory() {
        List<FeCurrentDirectoryModelEvent> events = eventsByName.get(GO_TO_DIRECTORY);
        assertNotNull(events, "Events of goToDirectory");
        return events;
    }

    private List<FeCurrentDirectoryModelEvent> getEventsOfRefresh() {
        List<FeCurrentDirectoryModelEvent> events = eventsByName.get(REFRESH);
        assertNotNull(events, "Events of refresh");
        return events;
    }

    private void goToRoot() {
        Optional<FeDirectory> optionalDirectory;
        model.goToRoot();
        optionalDirectory = model.getCurrentDirectory();
        assertTrue(optionalDirectory.isEmpty(), "It isn't root");
        List<FeCurrentDirectoryModelEvent> eventsOfGoToRoot = getEventsOfGoToRoot();
        assertEquals(1, eventsOfGoToRoot.size(), "Number of events of goToRoot");
        FeCurrentDirectoryModelEvent eventOfGoToRoot = eventsOfGoToRoot.get(0);
        assertNull(eventOfGoToRoot.getDirectory(), "Directory from event");
        eventsOfGoToRoot.clear();
    }

    private void goToDirectory(FeDirectory directory) {
        Optional<FeDirectory> optionalDirectory;
        model.goToDirectory(directory);
        optionalDirectory = model.getCurrentDirectory();
        assertTrue(optionalDirectory.isPresent(), "It isn't directory");
        assertSame(directory, optionalDirectory.get(), "Current directory");
        List<FeCurrentDirectoryModelEvent> eventsOfGoToDirectory = getEventsOfGoToDirectory();
        assertEquals(1, eventsOfGoToDirectory.size(), "Number of events of goToDirectory");
        FeCurrentDirectoryModelEvent eventOfGoToDirectory = eventsOfGoToDirectory.get(0);
        assertSame(directory, eventOfGoToDirectory.getDirectory(), "Directory from event");
        eventsOfGoToDirectory.clear();
    }

}