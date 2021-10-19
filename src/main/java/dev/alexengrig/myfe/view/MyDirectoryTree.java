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

package dev.alexengrig.myfe.view;

import dev.alexengrig.myfe.model.DirectoryTreeNode;
import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyDirectoryTreeModel;
import dev.alexengrig.myfe.model.RootDirectoryTreeNode;
import dev.alexengrig.myfe.util.ThrowableFunction;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.TreePath;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MyDirectoryTree extends JTree {

    //FIXME: Handlers - NPE
    private ThrowableFunction<MyDirectory, Iterable<MyDirectory>> loadSubdirectoriesHandler;
    private Runnable selectRootDirectoryHandler;
    private Consumer<MyDirectory> selectDirectoryHandler;

    public MyDirectoryTree(MyDirectoryTreeModel model) {
        super(model);
        init();
    }

    private void init() {
        LoadDirectoriesListener loadDirectoriesListener = new LoadDirectoriesListener();
        addTreeWillExpandListener(loadDirectoriesListener);
        SelectDirectoryListener selectDirectoryListener = new SelectDirectoryListener();
        addMouseListener(selectDirectoryListener);
        addKeyListener(selectDirectoryListener);
    }

    public void onLoadSubdirectories(ThrowableFunction<MyDirectory, Iterable<MyDirectory>> handler) {
        this.loadSubdirectoriesHandler = handler;
    }

    private void handleLoadDirectories(DirectoryTreeNode node) {
        node.setLoaded(true);
        MyDirectory directory = node.getUserObject();
        try {
            Iterable<MyDirectory> subdirectories = loadSubdirectoriesHandler.apply(directory);
            node.addAll(subdirectories);
        } catch (Exception e) {
            node.setLoaded(false);
        }
    }

    public void onSelectRootDirectory(Runnable handler) {
        this.selectRootDirectoryHandler = handler;
    }

    private void handleSelectRootDirectory(RootDirectoryTreeNode root) {
        selectRootDirectoryHandler.run();
    }

    public void onSelectDirectory(Consumer<MyDirectory> handler) {
        this.selectDirectoryHandler = handler;
    }

    private void handleSelectDirectory(DirectoryTreeNode node) {
        selectDirectoryHandler.accept(node.getUserObject());
    }

    /**
     * On a tree expands a node.
     *
     * @see MyDirectoryTree#handleLoadDirectories(DirectoryTreeNode)
     */
    private class LoadDirectoriesListener implements TreeWillExpandListener {

        @Override
        public void treeWillExpand(TreeExpansionEvent event) {
            //TODO: Add benchmark: vs Plain style
            Optional.ofNullable(event)
                    .map(TreeExpansionEvent::getPath)
                    .map(TreePath::getLastPathComponent)
                    .filter(DirectoryTreeNode.class::isInstance)
                    .map(DirectoryTreeNode.class::cast)
                    .filter(Predicate.not(DirectoryTreeNode::isLoaded))
                    .ifPresent(MyDirectoryTree.this::handleLoadDirectories);
        }

        //TODO: Create "do-nothing" interfaces

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) {
        }

    }

    /**
     * On click the left mouse button and press the Enter key on a node.
     *
     * @see MyDirectoryTree#handleSelectRootDirectory(RootDirectoryTreeNode)
     * @see MyDirectoryTree#handleSelectDirectory(DirectoryTreeNode)
     */
    private class SelectDirectoryListener implements MouseListener, KeyListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                TreePath path = getPathForLocation(e.getX(), e.getY());
                Optional.ofNullable(path)
                        .map(TreePath::getLastPathComponent)
                        .filter(DirectoryTreeNode.class::isInstance)
                        .map(DirectoryTreeNode.class::cast)
                        .ifPresentOrElse(MyDirectoryTree.this::handleSelectDirectory, () ->
                                Optional.ofNullable(path)
                                        .map(TreePath::getLastPathComponent)
                                        .filter(RootDirectoryTreeNode.class::isInstance)
                                        .map(RootDirectoryTreeNode.class::cast)
                                        .ifPresent(MyDirectoryTree.this::handleSelectRootDirectory));
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                Object lastNode = getLastSelectedPathComponent();
                Optional.ofNullable(lastNode)
                        .filter(DirectoryTreeNode.class::isInstance)
                        .map(DirectoryTreeNode.class::cast)
                        .ifPresentOrElse(MyDirectoryTree.this::handleSelectDirectory, () ->
                                Optional.ofNullable(lastNode)
                                        .filter(RootDirectoryTreeNode.class::isInstance)
                                        .map(RootDirectoryTreeNode.class::cast)
                                        .ifPresent(MyDirectoryTree.this::handleSelectRootDirectory));
            }
        }

        //TODO: Create "do-nothing" interfaces

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

    }

}
