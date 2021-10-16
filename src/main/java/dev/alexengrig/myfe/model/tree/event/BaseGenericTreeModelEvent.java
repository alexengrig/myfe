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

package dev.alexengrig.myfe.model.tree.event;

import dev.alexengrig.myfe.model.Model;
import dev.alexengrig.myfe.model.tree.BaseGenericTreePath;
import dev.alexengrig.myfe.model.tree.GenericTreeModel;
import dev.alexengrig.myfe.model.tree.GenericTreeNode;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link TreeModelEvent} with generics.
 *
 * @param <M>  the type of {@link Model}
 * @param <N>  the type of {@link GenericTreeNode}
 * @param <P>  the type of {@link BaseGenericTreePath}
 * @param <TM> the type of {@link GenericTreeModel}
 */
public class BaseGenericTreeModelEvent<
        M extends Model,
        N extends GenericTreeNode<M, N>,
        P extends BaseGenericTreePath<M, N, P>,
        TM extends GenericTreeModel<M, N, P>>
        extends TreeModelEvent {

    protected BaseGenericTreeModelEvent(TM source, P path) {
        super(source, path);
    }

    protected BaseGenericTreeModelEvent(TM source, P path, int[] childIndices, N[] children) {
        super(source, path, childIndices, children);
    }

    /**
     * {@link TreeModelEvent#getTreePath()}.
     */
    @SuppressWarnings("unchecked")
    protected P path() {
        return (P) getTreePath();
    }

    @Override
    public final TreePath getTreePath() {
        return super.getTreePath();
    }

    /**
     * {@link TreeModelEvent#getPath()}.
     */
    @SuppressWarnings("unchecked")
    protected List<N> allNodes() {
        Object[] values = getPath();
        ArrayList<N> nodes = new ArrayList<>(values.length);
        for (Object value : values) {
            nodes.add((N) value);
        }
        return nodes;
    }

    @Override
    public final Object[] getPath() {
        return super.getPath();
    }

    /**
     * {@link TreeModelEvent#getChildren()}.
     */
    @SuppressWarnings("unchecked")
    protected List<N> children() {
        Object[] values = getChildren();
        ArrayList<N> nodes = new ArrayList<>(values.length);
        for (Object value : values) {
            nodes.add((N) value);
        }
        return nodes;
    }

    @Override
    public final Object[] getChildren() {
        return super.getChildren();
    }

}
