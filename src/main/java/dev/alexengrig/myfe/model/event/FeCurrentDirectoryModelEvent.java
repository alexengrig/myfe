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

package dev.alexengrig.myfe.model.event;

import dev.alexengrig.myfe.domain.FeDirectory;
import dev.alexengrig.myfe.util.event.Event;

/**
 * Event of {@link FeCurrentDirectoryModelListener}.
 */
public class FeCurrentDirectoryModelEvent implements Event {

    private final Type type;
    private final FeDirectory directory;

    private FeCurrentDirectoryModelEvent(Type type, FeDirectory directory) {
        this.type = type;
        this.directory = directory;
    }

    public static FeCurrentDirectoryModelEvent root() {
        return new FeCurrentDirectoryModelEvent(Type.GO_TO_ROOT, null);
    }

    public static FeCurrentDirectoryModelEvent directory(FeDirectory directory) {
        return new FeCurrentDirectoryModelEvent(Type.GO_TO_DIRECTORY, directory);
    }

    public static FeCurrentDirectoryModelEvent refreshing() {
        return new FeCurrentDirectoryModelEvent(Type.REFRESH, null);
    }

    protected Type getType() {
        return type;
    }

    public FeDirectory getDirectory() {
        return directory;
    }

    @Override
    public String toString() {
        return "FeCurrentDirectoryModelEvent{" +
                "type=" + type +
                ", directory=" + directory +
                '}';
    }

    protected enum Type {
        GO_TO_ROOT,
        GO_TO_DIRECTORY,
        REFRESH
    }

}
