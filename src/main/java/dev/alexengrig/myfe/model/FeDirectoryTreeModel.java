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

import dev.alexengrig.myfe.util.FePathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.lang.invoke.MethodHandles;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Tree model of {@link FeDirectory FeDirectories}.
 */
public class FeDirectoryTreeModel implements TreeModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<TreeModelListener> listeners = new LinkedList<>();

    private final RootTreeNode root;

    public FeDirectoryTreeModel(String rootName, List<FeDirectory> rootDirectories) {
        this.root = new RootTreeNode(rootName, rootDirectories);
    }

    private static TreePath getPath(TreeNode node) {
        TreeNode parent = node.getParent();
        if (parent == null) {
            return new TreePath(node);
        }
        Deque<TreeNode> nodes = new LinkedList<>();
        nodes.push(node);
        do {
            nodes.push(parent);
            parent = parent.getParent();
        } while (parent != null);
        return new TreePath(nodes.toArray());
    }

    public void setRootDirectories(List<FeDirectory> rootDirectories) {
        root.setChildren(rootDirectories);
        fireTreeStructureChanged(new TreeModelEvent(this, getPath(root)));
    }

    public void setSubdirectories(FeDirectoryTreeNode parent, List<FeDirectory> subdirectories) {
        parent.setChildren(subdirectories);
        fireTreeStructureChanged(new TreeModelEvent(this, getPath(parent), null, null));
    }

    public void setSubdirectories(FeDirectory directory, List<FeDirectory> subdirectories) {
        findLoadedDirectoryNode(directory).ifPresent(node -> setSubdirectories(node, subdirectories));
    }

    /**
     * Find the node with given directory. Stop on not loaded node.
     */
    //TODO: Improve it
    private Optional<FeDirectoryTreeNode> findLoadedDirectoryNode(FeDirectory directory) {
        String[] names = FePathUtil.splitByNames(directory);
        if (names.length == 0) {
            return Optional.empty();
        }
        boolean found = true;
        int lastLevel = names.length - 1;
        String directoryName;
        List<? extends FeDirectoryTreeNode> nodes = root.getChildren();
        for (int level = 0; found && level < lastLevel; level++) {
            found = false;
            directoryName = names[level];
            for (FeDirectoryTreeNode node : nodes) {
                if (directoryName.equals(node.getDirectory().getName())) {
                    if (!node.isLoaded()) {
                        return Optional.empty();
                    }
                    nodes = node.getChildren();
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            return Optional.empty();
        }
        directoryName = names[names.length - 1];
        for (FeDirectoryTreeNode node : nodes) {
            if (directoryName.equals(node.getDirectory().getName())) {
                if (!node.isLoaded()) {
                    return Optional.empty();
                }
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((TreeNode) parent).getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((TreeNode) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((TreeNode) node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("path=" + path + ", newValue=" + newValue);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((TreeNode) parent).getIndex((TreeNode) child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    private void fireTreeStructureChanged(TreeModelEvent event) {
        LOGGER.debug("Fire tree structure changed: {}", event);
        for (TreeModelListener listener : listeners) {
            listener.treeStructureChanged(event);
        }
    }

}
