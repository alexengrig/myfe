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

package dev.alexengrig.myfe.domain;

import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class RootTreeNode implements TreeNode {

    private final String name;

    private List<RootDirectoryTreeNode> children;

    public RootTreeNode(String name, List<FeDirectory> rootDirectories) {
        this.name = requireNonNull(name, "The name must not be null");
        this.children = createChildren(requireNonNull(rootDirectories, "The root directories must not be null"));
    }

    public String getName() {
        return name;
    }

    public List<RootDirectoryTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<FeDirectory> directories) {
        children = createChildren(directories);
    }

    private List<RootDirectoryTreeNode> createChildren(List<FeDirectory> directories) {
        return directories.stream()
                .map(RootDirectoryTreeNode.factory(this))
                .collect(Collectors.toList());
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public TreeNode getParent() {
        return null;
    }

    @Override
    public int getIndex(TreeNode node) {
        return children.indexOf(((RootDirectoryTreeNode) node));
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        return Collections.enumeration(children);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RootTreeNode that = (RootTreeNode) o;
        return Objects.equals(name, that.name)
                && Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, children);
    }

}
