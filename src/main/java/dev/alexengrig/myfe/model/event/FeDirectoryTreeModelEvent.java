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

import dev.alexengrig.myfe.util.event.Event;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import java.util.Arrays;

/**
 * Event of {@link FeDirectoryTreeModelListener}.
 */
public class FeDirectoryTreeModelEvent extends TreeModelEvent implements Event {

    private final Type type;

    private FeDirectoryTreeModelEvent(Type type, Object source, TreePath path) {
        super(source, path, null, null);
        this.type = type;
    }

    public static FeDirectoryTreeModelEvent structureChanged(Object source, TreePath path) {
        return new FeDirectoryTreeModelEvent(Type.STRUCTURE_CHANGED, source, path);
    }

    protected Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "FeDirectoryTreeModelEvent{" +
                "type=" + type +
                ", path=" + path +
                ", childIndices=" + Arrays.toString(childIndices) +
                ", children=" + Arrays.toString(children) +
                ", source=" + source +
                '}';
    }

    protected enum Type {
        NODES_CHANGED,
        NODES_INSERTED,
        NODES_REMOVED,
        STRUCTURE_CHANGED
    }

}
