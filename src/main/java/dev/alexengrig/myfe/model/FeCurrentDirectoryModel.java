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
import dev.alexengrig.myfe.util.FePathUtil;
import dev.alexengrig.myfe.util.event.EventListenerGroup;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

public class FeCurrentDirectoryModel {

    private final EventListenerGroup<FeCurrentDirectoryModelListener, FeCurrentDirectoryModelEvent> listenerGroup = new EventListenerGroup<>();

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

    public Optional<FeDirectory> getCurrentDirectory() {
        return Optional.ofNullable(backStack.peek());
    }

    public boolean canGoBack() {
        return !backStack.isEmpty();
    }

    public boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    public boolean canGoUp() {
        return getCurrentDirectory().isPresent();
    }

    public void goToDirectory(FeDirectory directory) {
        Optional<FeDirectory> optionalDirectory = getCurrentDirectory();
        if (optionalDirectory.isEmpty() || !Objects.equals(optionalDirectory.get(), directory)) {
            backStack.push(directory);
            if (!forwardStack.isEmpty()) {
                forwardStack.clear();
            }
            listenerGroup.fire(FeCurrentDirectoryModelEvent.directory(directory));
        }
    }

    public void goToRoot() {
        if (getCurrentDirectory().isPresent()) {
            backStack.push(null);
            if (!forwardStack.isEmpty()) {
                forwardStack.clear();
            }
            listenerGroup.fire(FeCurrentDirectoryModelEvent.root());
        }
    }

    public void goBack() {
        if (!canGoBack()) {
            return;
        }
        FeDirectory previousDirectory = backStack.poll();
        forwardStack.push(previousDirectory);
        Optional<FeDirectory> optionalDirectory = getCurrentDirectory();
        if (optionalDirectory.isEmpty()) {
            listenerGroup.fire(FeCurrentDirectoryModelEvent.root());
        } else {
            listenerGroup.fire(FeCurrentDirectoryModelEvent.directory(optionalDirectory.get()));
        }
    }

    public void goForward() {
        if (!canGoForward()) {
            return;
        }
        FeDirectory nextDirectory = forwardStack.poll();
        backStack.push(nextDirectory);
        if (nextDirectory == null) {
            listenerGroup.fire(FeCurrentDirectoryModelEvent.root());
        } else {
            listenerGroup.fire(FeCurrentDirectoryModelEvent.directory(nextDirectory));
        }
    }

    public void goUp() {
        Optional<FeDirectory> optionalDirectory = getCurrentDirectory();
        if (optionalDirectory.isEmpty()) {
            return;
        }
        FeDirectory currentDirectory = optionalDirectory.get();
        FeDirectory parentDirectory = FePathUtil.getParent(currentDirectory).orElse(null);
        backStack.push(parentDirectory);
        if (!forwardStack.isEmpty()) {
            forwardStack.clear();
        }
        if (parentDirectory == null) {
            listenerGroup.fire(FeCurrentDirectoryModelEvent.root());
        } else {
            listenerGroup.fire(FeCurrentDirectoryModelEvent.directory(parentDirectory));
        }
    }

    public void update() {
        listenerGroup.fire(FeCurrentDirectoryModelEvent.refreshing());
    }

    public void addFeCurrentDirectoryModelListener(FeCurrentDirectoryModelListener listener) {
        listenerGroup.add(listener);
    }

    public void removeFeCurrentDirectoryModelListener(FeCurrentDirectoryModelListener listener) {
        listenerGroup.remove(listener);
    }

}
