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

package dev.alexengrig.myfe.model.directory;

import dev.alexengrig.myfe.model.ModelUpdater;
import dev.alexengrig.myfe.model.tree.BaseGenericTreeModel;
import dev.alexengrig.myfe.model.tree.BaseGenericTreeNode;
import dev.alexengrig.myfe.model.tree.BaseGenericTreePath;
import dev.alexengrig.myfe.model.tree.event.BaseGenericTreeModelEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class DirectoryTreeModel
        extends BaseGenericTreeModel<
        DirectoryModel,
        DirectoryTreeModel.Node, DirectoryTreeModel.Path, DirectoryTreeModel.Event, DirectoryTreeModel> {

    private final Node root;

    public DirectoryTreeModel(DirectoryModel model) {
        this.root = new Node(model);
    }

    @Override
    public Node root() {
        return root;
    }

    @Override
    protected Event createEventOnChangeNode(Path path, int childIndex, Node child) {
        return new Event(this, path, new int[]{childIndex}, new Node[]{child});
    }

    private void nodeChildrenWereSet(Node node) {
        Event event = createEventOnSetNodeChildren(node);
        fireTreeStructureChanged(event);
    }

    private Event createEventOnSetNodeChildren(Node node) {
        Path path = new Path(node);
        return new Event(this, path);
    }

    public class Node
            extends BaseGenericTreeNode<DirectoryModel, Node>
            implements ModelUpdater<DirectoryModel> {

        private final Node parent;

        private DirectoryModel model;
        private List<Node> children;

        protected Node(DirectoryModel model) {
            this(null, model);
        }

        protected Node(Node parent, DirectoryModel model) {
            this.parent = parent;
            this.model = model;
            this.children = Collections.emptyList();
        }

        public void setChildren(List<DirectoryModel> children) {
            this.children = children.stream().map(Node::new).collect(Collectors.toList());
            nodeChildrenWereSet(this);
        }

        @Override
        public Node parent() {
            return parent;
        }

        @Override
        public DirectoryModel model() {
            return model;
        }

        @Override
        public boolean update(UnaryOperator<DirectoryModel> updater) {
            DirectoryModel oldModel = model;
            this.model = updater.apply(oldModel);
            return true;
        }

        @Override
        public Collection<Node> collection() {
            return children;
        }

        @Override
        public boolean getAllowsChildren() {
            return true;
        }

        @Override
        public boolean isLeaf() {
            return model.isLoaded() && children.isEmpty();
        }

    }

    public class Path
            extends BaseGenericTreePath<DirectoryModel, Node, Path> {

        protected Path(Node lastNode) {
            super(createParent(lastNode, Path::new), lastNode);
        }

        protected Path(Path parent, Node lastNode) {
            super(parent, lastNode);
        }

        @Override
        protected Path withNode(Node node) {
            return new Path(this, node);
        }

        @Override
        public Node lastNode() {
            return super.lastNode();
        }

    }

    public class Event
            extends BaseGenericTreeModelEvent<DirectoryModel, Node, Path, DirectoryTreeModel> {

        protected Event(DirectoryTreeModel source, Path path) {
            super(source, path);
        }

        protected Event(DirectoryTreeModel source, Path path,
                        int[] childIndices, Node[] children) {
            super(source, path, childIndices, children);
        }

    }

}
