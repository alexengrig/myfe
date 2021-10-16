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

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * {@link TreePath} with generics.
 *
 * @param <M>    the type of {@link Model}
 * @param <N>    the type of {@link GenericTreeNode}
 * @param <SELF> the type of {@link BaseGenericTreePath}
 */
public abstract class BaseGenericTreePath<
        M extends Model,
        N extends GenericTreeNode<M, N>,
        SELF extends BaseGenericTreePath<M, N, SELF>>
        extends TreePath {

    protected BaseGenericTreePath(SELF parent, N lastNode) {
        super(parent, lastNode);
    }

    protected static <
            M extends Model,
            N extends GenericTreeNode<M, N>,
            SELF extends BaseGenericTreePath<M, N, SELF>>
    SELF createParent(N node, BiFunction<SELF, N, SELF> constructor) {
        Deque<N> parentNodes = new LinkedList<>();
        N parentNode = node.parent();
        while (parentNode != null) {
            parentNodes.push(parentNode);
            parentNode = parentNode.parent();
        }
        if (parentNodes.isEmpty()) {
            return null;
        }
        Iterator<N> nodeIterator = parentNodes.iterator();
        SELF parentPath = constructor.apply(null, nodeIterator.next());
        while (nodeIterator.hasNext()) {
            parentPath = constructor.apply(parentPath, nodeIterator.next());
        }
        return parentPath;
    }

    /**
     * {@link TreePath#getPath()}.
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
     * {@link TreePath#getLastPathComponent()}.
     */
    @SuppressWarnings("unchecked")
    protected N lastNode() {
        return (N) getLastPathComponent();
    }

    @Override
    public final Object getLastPathComponent() {
        return super.getLastPathComponent();
    }

    /**
     * {@link TreePath#getPathCount()}.
     */
    protected int countNodes() {
        return getPathCount();
    }

    @Override
    public final int getPathCount() {
        return super.getPathCount();
    }

    /**
     * {@link TreePath#getPathComponent(int)}.
     */
    @SuppressWarnings("unchecked")
    protected N nodeAt(int index) {
        return (N) getPathComponent(index);
    }

    @Override
    public final Object getPathComponent(int index) {
        return super.getPathComponent(index);
    }

    /**
     * {@link TreePath#pathByAddingChild(Object)}.
     *
     * @see BaseGenericTreePath#BaseGenericTreePath(BaseGenericTreePath, GenericTreeNode)
     */
    protected abstract SELF withNode(N node);

    @Override
    @SuppressWarnings("unchecked")
    public final TreePath pathByAddingChild(Object child) {
        return withNode((N) child);
    }

    /**
     * {@link TreePath#getParentPath()}.
     */
    @SuppressWarnings("unchecked")
    protected SELF parent() {
        return (SELF) getParentPath();
    }

    @Override
    public final TreePath getParentPath() {
        return super.getParentPath();
    }

    @Override
    public final boolean isDescendant(TreePath aTreePath) {
        return super.isDescendant(aTreePath);
    }

    @Override
    public String toString() {
        return allNodes().toString();
    }

}
