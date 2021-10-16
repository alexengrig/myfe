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

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * {@link TreeModel} with generics.
 *
 * @param <M> the type of {@link Model}
 * @param <N> the type of {@link GenericTreeNode}
 * @param <P> the type of {@link BaseGenericTreePath}
 */
public interface GenericTreeModel<
        M extends Model,
        N extends GenericTreeNode<M, N>,
        P extends BaseGenericTreePath<M, N, P>>
        extends TreeModel {

    /**
     * {@link TreeModel#getRoot()}.
     */
    N root();

    @Override
    default Object getRoot() {
        return root();
    }

    /**
     * {@link TreeModel#getChild(Object, int)}.
     */
    N child(N parent, int index);

    @Override
    @SuppressWarnings("unchecked")
    default Object getChild(Object parent, int index) {
        return child((N) parent, index);
    }

    /**
     * {@link TreeModel#getChildCount(Object)}.
     */
    int childCount(N parent);

    @Override
    @SuppressWarnings("unchecked")
    default int getChildCount(Object parent) {
        return childCount((N) parent);
    }

    /**
     * {@link TreeModel#isLeaf(Object)}.
     */
    boolean leaf(N node);

    @Override
    @SuppressWarnings("unchecked")
    default boolean isLeaf(Object node) {
        return leaf((N) node);
    }

    /**
     * {@link TreeModel#valueForPathChanged(TreePath, Object)}.
     */
    void change(P path, M newModel);

    @Override
    @SuppressWarnings("unchecked")
    default void valueForPathChanged(TreePath path, Object newValue) {
        change((P) path, (M) newValue);
    }

    /**
     * {@link TreeModel#getIndexOfChild(Object, Object)}.
     */
    int indexOfChild(N parent, N child);

    @Override
    @SuppressWarnings("unchecked")
    default int getIndexOfChild(Object parent, Object child) {
        return indexOfChild((N) parent, (N) child);
    }

}
