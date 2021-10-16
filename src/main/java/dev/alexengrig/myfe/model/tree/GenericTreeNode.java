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

import javax.swing.tree.TreeNode;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * {@link TreeNode} with generics.
 *
 * @param <M>    the type of {@link Model}
 * @param <SELF> the type of {@link GenericTreeNode}
 */
public interface GenericTreeNode<
        M extends Model,
        SELF extends GenericTreeNode<M, SELF>>
        extends TreeNode {

    M model();

    /**
     * {@link TreeNode#getChildAt(int)}.
     */
    SELF childAt(int index);

    @Override
    default TreeNode getChildAt(int childIndex) {
        return childAt(childIndex);
    }

    /**
     * {@link TreeNode#getParent()}.
     */
    SELF parent();

    @Override
    default TreeNode getParent() {
        return parent();
    }

    /**
     * {@link TreeNode#getIndex(TreeNode)}.
     */
    int index(SELF node);

    @Override
    @SuppressWarnings("unchecked")
    default int getIndex(TreeNode node) {
        return index((SELF) node);
    }

    /**
     * {@link TreeNode#children()}.
     */
    Collection<SELF> collection();

    @Override
    default Enumeration<? extends TreeNode> children() {
        return Collections.enumeration(collection());
    }

    default Iterator<SELF> iterator() {
        return collection().iterator();
    }

    default Stream<SELF> stream() {
        return collection().stream();
    }

}
