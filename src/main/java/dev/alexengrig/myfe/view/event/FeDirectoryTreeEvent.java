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

package dev.alexengrig.myfe.view.event;

import dev.alexengrig.myfe.domain.FeDirectory;
import dev.alexengrig.myfe.util.event.Event;

/**
 * Event of {@link FeDirectoryTreeListener}.
 */
public class FeDirectoryTreeEvent implements Event {

    private final Type type;
    private final FeDirectory directory;
    private final String rootName;

    private FeDirectoryTreeEvent(Type type, FeDirectory directory, String rootName) {
        this.type = type;
        this.directory = directory;
        this.rootName = rootName;
    }

    public static FeDirectoryTreeEvent selectRoot(String rootName) {
        return new FeDirectoryTreeEvent(Type.SELECT_ROOT, null, rootName);
    }

    public static FeDirectoryTreeEvent selectDirectory(FeDirectory directory) {
        return new FeDirectoryTreeEvent(Type.SELECT_DIRECTORY, directory, null);
    }

    protected Type getType() {
        return type;
    }

    public FeDirectory getDirectory() {
        return directory;
    }

    public String getRootName() {
        return rootName;
    }

    @Override
    public String toString() {
        return "FeDirectoryTreeEvent{" +
                "type=" + type +
                ", directory=" + directory +
                ", rootName='" + rootName + '\'' +
                '}';
    }

    protected enum Type {
        SELECT_ROOT,
        SELECT_DIRECTORY
    }

}
