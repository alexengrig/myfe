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

/**
 * Tree node of {@link FeDirectory}.
 */
public abstract class FeDirectoryTreeNode implements TreeNode {

    private final FeDirectory directory;

    private List<FeDirectoryTreeNode> children;
    private boolean loaded;

    public FeDirectoryTreeNode(FeDirectory directory) {
        this.directory = Objects.requireNonNull(directory, "The directory must not be null");
    }

    public FeDirectory getDirectory() {
        return directory;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public List<FeDirectoryTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<FeDirectory> directories) {
        children = directories.stream()
                .map(SubdirectoryTreeNode.factory(this))
                .collect(Collectors.toList());
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        if (children == null) {
            throw new IllegalStateException("No children");
        }
        return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
        if (children == null) {
            return 0;
        }
        return children.size();
    }

    @Override
    public int getIndex(TreeNode node) {
        if (children == null) {
            throw new IllegalStateException("No children");
        }
        return children.indexOf(((FeDirectoryTreeNode) node));
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
        return directory.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeDirectoryTreeNode that = (FeDirectoryTreeNode) o;
        return loaded == that.loaded
                && Objects.equals(directory, that.directory)
                && Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directory);
    }

}
