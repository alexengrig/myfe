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

import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyDirectoryTreeModel;
import dev.alexengrig.myfe.model.MyDirectoryTreeNode;
import dev.alexengrig.myfe.model.RootDirectoryTreeNode;
import dev.alexengrig.myfe.service.MyDirectoryTreeBackgroundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.TreePath;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MyDirectoryTree extends JTree {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MyDirectoryTreeBackgroundService backgroundService;

    private final List<MyDirectoryTreeListener> listeners;

    public MyDirectoryTree(
            MyDirectoryTreeModel model,
            MyDirectoryTreeBackgroundService backgroundService) {
        super(model);
        this.backgroundService = backgroundService;
        this.listeners = new LinkedList<>();
        init();
    }

    private void init() {
        LOGGER.debug("Start init");
        ExpandNodeListener expandNodeListener = new ExpandNodeListener();
        addTreeWillExpandListener(expandNodeListener);
        SelectNodeListener selectNodeListener = new SelectNodeListener();
        addMouseListener(selectNodeListener);
        addKeyListener(selectNodeListener);
        LOGGER.debug("Finished init");
    }

    @Override
    public MyDirectoryTreeModel getModel() {
        return (MyDirectoryTreeModel) super.getModel();
    }

    private void handleLoadChildDirectories(MyDirectoryTreeNode node) {
        MyDirectory directory = node.getUserObject();
        LOGGER.debug("Start loading child directories for: {}", directory);
        node.setLoaded(true);
        try {
            backgroundService.loadSubdirectories(directory, children -> {
                MyDirectoryTreeModel model = getModel();
                model.addChildrenInto(node, children);
                LOGGER.debug("Finished loading child directories for: {}; directories: {}", directory, children);
            });
        } catch (Exception exception) {
            node.setLoaded(false);
            LOGGER.warn("Exception of loading child directories for: {}", directory, exception);
        }
    }

    private void handleSelectRoot(RootDirectoryTreeNode root) {
        fireSelectRoot(new MyDirectoryTreeEvent(root.getUserObject()));
    }

    private void handleSelectDirectory(MyDirectoryTreeNode node) {
        fireSelectDirectory(new MyDirectoryTreeEvent(node.getUserObject()));
    }

    public void addMyDirectoryTreeListener(MyDirectoryTreeListener listener) {
        listeners.add(listener);
    }

    public void removeMyDirectoryTreeListener(MyDirectoryTreeListener listener) {
        listeners.remove(listener);
    }

    private void fireSelectRoot(MyDirectoryTreeEvent event) {
        for (MyDirectoryTreeListener listener : listeners) {
            listener.selectRoot(event);
        }
    }

    private void fireSelectDirectory(MyDirectoryTreeEvent event) {
        for (MyDirectoryTreeListener listener : listeners) {
            listener.selectDirectory(event);
        }
    }

    /**
     * On a tree expands a node.
     *
     * @see MyDirectoryTree#handleLoadChildDirectories(MyDirectoryTreeNode)
     */
    private class ExpandNodeListener implements TreeWillExpandListener {

        @Override
        public void treeWillExpand(TreeExpansionEvent event) {
            //TODO: Add benchmark: vs Plain style
            Optional.ofNullable(event)
                    .map(TreeExpansionEvent::getPath)
                    .map(TreePath::getLastPathComponent)
                    .filter(MyDirectoryTreeNode.class::isInstance)
                    .map(MyDirectoryTreeNode.class::cast)
                    .filter(Predicate.not(MyDirectoryTreeNode::isLoaded))
                    .ifPresent(MyDirectoryTree.this::handleLoadChildDirectories);
        }

        //TODO: Create "do-nothing" interfaces

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) {
        }

    }

    /**
     * On click the left mouse button and press the Enter key on a node.
     *
     * @see MyDirectoryTree#handleSelectRoot(RootDirectoryTreeNode)
     * @see MyDirectoryTree#handleSelectDirectory(MyDirectoryTreeNode)
     */
    private class SelectNodeListener implements MouseListener, KeyListener {

        @Override
        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                TreePath path = getPathForLocation(event.getX(), event.getY());
                Optional.ofNullable(path)
                        .map(TreePath::getLastPathComponent)
                        .filter(MyDirectoryTreeNode.class::isInstance)
                        .map(MyDirectoryTreeNode.class::cast)
                        .ifPresentOrElse(MyDirectoryTree.this::handleSelectDirectory, () ->
                                Optional.ofNullable(path)
                                        .map(TreePath::getLastPathComponent)
                                        .filter(RootDirectoryTreeNode.class::isInstance)
                                        .map(RootDirectoryTreeNode.class::cast)
                                        .ifPresent(MyDirectoryTree.this::handleSelectRoot));
            }
        }

        @Override
        public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                Object lastNode = getLastSelectedPathComponent();
                Optional.ofNullable(lastNode)
                        .filter(MyDirectoryTreeNode.class::isInstance)
                        .map(MyDirectoryTreeNode.class::cast)
                        .ifPresentOrElse(MyDirectoryTree.this::handleSelectDirectory, () ->
                                Optional.ofNullable(lastNode)
                                        .filter(RootDirectoryTreeNode.class::isInstance)
                                        .map(RootDirectoryTreeNode.class::cast)
                                        .ifPresent(MyDirectoryTree.this::handleSelectRoot));
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
