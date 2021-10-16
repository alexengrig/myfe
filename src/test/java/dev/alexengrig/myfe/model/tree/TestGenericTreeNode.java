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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

class TestGenericTreeNode
        extends BaseGenericTreeNode<TestModel, TestGenericTreeNode> {

    private final TestGenericTreeNode parent;
    private final List<TestGenericTreeNode> children;
    private final TestModel model;

    TestGenericTreeNode(TestModel model) {
        this(null, model);
    }

    TestGenericTreeNode(TestGenericTreeNode parent, TestModel model) {
        this.parent = parent;
        this.model = model;
        this.children = new LinkedList<>();
        if (this.parent != null) {
            this.parent.children.add(this);
        }
    }

    @Override
    public TestGenericTreeNode parent() {
        return parent;
    }

    @Override
    public TestModel model() {
        return model;
    }

    @Override
    public Collection<TestGenericTreeNode> collection() {
        return children;
    }

    @Override
    public boolean getAllowsChildren() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLeaf() {
        throw new UnsupportedOperationException();
    }

}
