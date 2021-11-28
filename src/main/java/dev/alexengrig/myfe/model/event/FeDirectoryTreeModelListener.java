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

import dev.alexengrig.myfe.util.event.EventListener;
import dev.alexengrig.myfe.util.event.UnknownEventException;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

/**
 * Event listener of {@link FeDirectoryTreeModelEvent}.
 */
public interface FeDirectoryTreeModelListener extends EventListener<FeDirectoryTreeModelEvent>, TreeModelListener {

    static FeDirectoryTreeModelListener wrap(TreeModelListener listener) {
        return new FeDirectoryTreeModelListener() {

            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                listener.treeNodesChanged(e);
            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {
                listener.treeNodesChanged(e);
            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {
                listener.treeNodesChanged(e);
            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) {
                listener.treeNodesChanged(e);
            }

        };
    }

    @Override
    default void notify(FeDirectoryTreeModelEvent event) {
        switch (event.getType()) {
            case NODES_CHANGED:
                treeNodesChanged(event);
                break;
            case NODES_INSERTED:
                treeNodesInserted(event);
                break;
            case NODES_REMOVED:
                treeNodesRemoved(event);
                break;
            case STRUCTURE_CHANGED:
                treeStructureChanged(event);
                break;
            default:
                throw new UnknownEventException(event);
        }
    }

}
