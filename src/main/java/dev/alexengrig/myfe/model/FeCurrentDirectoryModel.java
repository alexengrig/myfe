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

import dev.alexengrig.myfe.model.event.FeCurrentDirectoryModelEvent;
import dev.alexengrig.myfe.model.event.FeCurrentDirectoryModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class FeCurrentDirectoryModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<FeCurrentDirectoryModelListener> listeners = new LinkedList<>();

    /**
     * Current directory on the top; {@code null} - the root.
     */
    private final Deque<FeDirectory> backStack = new LinkedList<>();
    private final Deque<FeDirectory> forwardStack = new LinkedList<>();

    private final String rootName;

    public FeCurrentDirectoryModel(String rootName) {
        this.rootName = rootName;
    }

    public String getRootName() {
        return rootName;
    }

    public FeDirectory getDirectory() {
        return backStack.peek();
    }

    public boolean canGoBack() {
        return !backStack.isEmpty();
    }

    public boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    public boolean canGoUp() {
        return false;
    }

    public void goToDirectory(FeDirectory directory) {
        if (!Objects.equals(getDirectory(), directory)) {
            backStack.push(directory);
            if (!forwardStack.isEmpty()) {
                forwardStack.clear();
            }
            fireGoToDirectory(FeCurrentDirectoryModelEvent.directory(directory));
        }
    }

    public void goToRoot() {
        if (getDirectory() != null) {
            backStack.push(null);
            if (!forwardStack.isEmpty()) {
                forwardStack.clear();
            }
            fireGoToRoot(FeCurrentDirectoryModelEvent.root());
        }
    }

    public void goBack() {
        if (!canGoBack()) {
            return;
        }
        FeDirectory currentDirectory = backStack.poll();
        forwardStack.push(currentDirectory);
        FeDirectory previousDirectory = getDirectory();
        if (previousDirectory == null) {
            fireGoToRoot(FeCurrentDirectoryModelEvent.root());
        } else {
            fireGoToDirectory(FeCurrentDirectoryModelEvent.directory(previousDirectory));
        }
    }

    public void goForward() {
        if (!canGoForward()) {
            return;
        }
        FeDirectory nextDirectory = forwardStack.poll();
        backStack.push(nextDirectory);
        if (nextDirectory == null) {
            fireGoToRoot(FeCurrentDirectoryModelEvent.root());
        } else {
            fireGoToDirectory(FeCurrentDirectoryModelEvent.directory(nextDirectory));
        }
    }

    public void goUp() {
        if (!canGoUp()) {
            return;
        }
        //FIXME: Implement
    }

    public void addFeCurrentDirectoryModelListener(FeCurrentDirectoryModelListener listener) {
        listeners.add(listener);
    }

    public void removeFeCurrentDirectoryModelListener(FeCurrentDirectoryModelListener listener) {
        listeners.remove(listener);
    }

    private void fireGoToRoot(FeCurrentDirectoryModelEvent event) {
        LOGGER.debug("Fire go to root: {}", event);
        for (FeCurrentDirectoryModelListener listener : listeners) {
            listener.goToRoot(event);
        }
    }

    private void fireGoToDirectory(FeCurrentDirectoryModelEvent event) {
        LOGGER.debug("Fire go to directory: {}", event);
        for (FeCurrentDirectoryModelListener listener : listeners) {
            listener.goToDirectory(event);
        }
    }

}
