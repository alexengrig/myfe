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

package dev.alexengrig.myfe.model.tree;

import dev.alexengrig.myfe.model.Model;
import dev.alexengrig.myfe.model.ModelUpdater;
import dev.alexengrig.myfe.model.tree.event.BaseGenericTreeModelEvent;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.util.LinkedList;
import java.util.List;

/**
 * Base implementation {@link GenericTreeModel}.
 *
 * @param <E>    the type of {@link BaseGenericTreeModelEvent}
 * @param <SELF> the type of {@link GenericTreeModel}
 */
@Deprecated
public abstract class BaseGenericTreeModel<
        M extends Model,
        N extends GenericTreeNode<M, N> & ModelUpdater<M>,
        P extends BaseGenericTreePath<M, N, P>,
        E extends BaseGenericTreeModelEvent<M, N, P, SELF>,
        SELF extends GenericTreeModel<M, N, P>>
        implements GenericTreeModel<M, N, P> {

    private final List<TreeModelListener> listeners;

    protected BaseGenericTreeModel() {
        this(new LinkedList<>());
    }

    protected BaseGenericTreeModel(List<TreeModelListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public N child(N parent, int index) {
        return parent.childAt(index);
    }

    @Override
    public int childCount(N parent) {
        return parent.getChildCount();
    }

    @Override
    public boolean leaf(N node) {
        return node.isLeaf();
    }

    @Override
    public int indexOfChild(N parent, N child) {
        return parent.index(child);
    }

    @Override
    public void change(P path, M newModel) {
        N node = path.lastNode();
        boolean isUpdated = node.update(newModel);
        if (isUpdated) {
            E event = createEventOnChangeNode(path, path.getPathCount() - 1, node);
            fireTreeNodesChanged(event);
        }
    }

    protected abstract E createEventOnChangeNode(P path, int childIndex, N child);

    @Override

    public void addTreeModelListener(TreeModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener listener) {
        listeners.remove(listener);
    }

    protected void fireTreeStructureChanged(TreeModelEvent event) {
        for (TreeModelListener listener : listeners) {
            listener.treeStructureChanged(event);
        }
    }

    protected void fireTreeNodesInserted(TreeModelEvent event) {
        for (TreeModelListener listener : listeners) {
            listener.treeNodesInserted(event);
        }
    }

    protected void fireTreeNodesChanged(TreeModelEvent event) {
        for (TreeModelListener listener : listeners) {
            listener.treeNodesChanged(event);
        }
    }

    protected void fireTreeNodesRemoved(TreeModelEvent event) {
        for (TreeModelListener listener : listeners) {
            listener.treeNodesRemoved(event);
        }
    }

}
