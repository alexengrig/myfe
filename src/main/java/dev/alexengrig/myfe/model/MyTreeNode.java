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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.List;
import java.util.Vector;

public class MyTreeNode<T> extends DefaultMutableTreeNode {

    public MyTreeNode(T userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    public void addAll(List<? extends MyTreeNode<?>> nodes) {
        for (MyTreeNode<?> node : nodes) {
            MutableTreeNode oldParent = (MutableTreeNode) node.getParent();
            if (oldParent != null) {
                oldParent.remove(node);
            }
            node.setParent(this);
        }
        if (children == null) {
            children = new Vector<>();
        }
        children.addAll(nodes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getUserObject() {
        return (T) super.getUserObject();
    }

}
