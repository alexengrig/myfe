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

import dev.alexengrig.myfe.domain.FeDirectory;
import dev.alexengrig.myfe.domain.FeDirectoryTreeNode;
import dev.alexengrig.myfe.domain.RootTreeNode;
import dev.alexengrig.myfe.model.FeDirectoryTreeModel;
import dev.alexengrig.myfe.service.DirectoryTreeBackgroundService;
import dev.alexengrig.myfe.util.event.EventListenerGroup;
import dev.alexengrig.myfe.view.event.DoNothingKeyListener;
import dev.alexengrig.myfe.view.event.DoNothingMouseListener;
import dev.alexengrig.myfe.view.event.DoNothingTreeWillExpandListener;
import dev.alexengrig.myfe.view.event.FeDirectoryTreeEvent;
import dev.alexengrig.myfe.view.event.FeDirectoryTreeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.TreePath;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.function.Predicate;

public class FeDirectoryTree extends JTree {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventListenerGroup<FeDirectoryTreeListener, FeDirectoryTreeEvent> listenerGroup = new EventListenerGroup<>();

    private final DirectoryTreeBackgroundService backgroundService;

    public FeDirectoryTree(
            FeDirectoryTreeModel model,
            DirectoryTreeBackgroundService backgroundService) {
        super(model);
        this.backgroundService = backgroundService;
        init();
    }

    private void init() {
        LoadChildNodesListener loadChildNodesListener = new LoadChildNodesListener();
        addTreeWillExpandListener(loadChildNodesListener);
        SelectNodeListener selectNodeListener = new SelectNodeListener();
        addMouseListener(selectNodeListener);
        addKeyListener(selectNodeListener);
    }

    @Override
    public FeDirectoryTreeModel getModel() {
        return (FeDirectoryTreeModel) super.getModel();
    }

    private void handleLoadChildDirectories(FeDirectoryTreeNode node) {
        LOGGER.debug("Handle load child directories: {}", node);
        FeDirectory directory = node.getDirectory();
        node.setLoaded(true);
        try {
            backgroundService.loadSubdirectories(directory, children -> {
                FeDirectoryTreeModel model = getModel();
                model.setSubdirectories(node, children);
                LOGGER.debug("Finished loading child directories for: {}; directories: {}", directory, children);
            });
        } catch (Exception exception) {
            node.setLoaded(false);
            //TODO: Open dialog of warning?
            LOGGER.warn("Exception of load child directories: {}", node, exception);
        }
    }

    private void handleSelectRoot(String rootName) {
        listenerGroup.fire(FeDirectoryTreeEvent.selectRoot(rootName));
    }

    private void handleSelectDirectory(FeDirectory directory) {
        listenerGroup.fire(FeDirectoryTreeEvent.selectDirectory(directory));
    }

    public void addFeDirectoryTreeListener(FeDirectoryTreeListener listener) {
        listenerGroup.add(listener);
    }

    public void removeFeDirectoryTreeListener(FeDirectoryTreeListener listener) {
        listenerGroup.remove(listener);
    }

    /**
     * On a tree expands a node.
     *
     * @see FeDirectoryTree#handleLoadChildDirectories(FeDirectoryTreeNode)
     */
    private class LoadChildNodesListener implements DoNothingTreeWillExpandListener {

        @Override
        public void treeWillExpand(TreeExpansionEvent event) {
            //TODO: Add benchmark: vs Plain style
            Optional.ofNullable(event)
                    .map(TreeExpansionEvent::getPath)
                    .map(TreePath::getLastPathComponent)
                    .filter(FeDirectoryTreeNode.class::isInstance)
                    .map(FeDirectoryTreeNode.class::cast)
                    .filter(Predicate.not(FeDirectoryTreeNode::isLoaded))
                    .ifPresent(FeDirectoryTree.this::handleLoadChildDirectories);
        }

    }

    /**
     * On click the left mouse button and press the Enter key on a node.
     *
     * @see FeDirectoryTree#handleSelectRoot(String)
     * @see FeDirectoryTree#handleSelectDirectory(FeDirectory)
     */
    private class SelectNodeListener implements DoNothingMouseListener, DoNothingKeyListener {

        @Override
        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                TreePath path = getPathForLocation(event.getX(), event.getY());
                Optional.ofNullable(path)
                        .map(TreePath::getLastPathComponent)
                        .filter(FeDirectoryTreeNode.class::isInstance)
                        .map(FeDirectoryTreeNode.class::cast)
                        .ifPresentOrElse(
                                node -> handleSelectDirectory(node.getDirectory()),
                                () -> Optional.ofNullable(path)
                                        .map(TreePath::getLastPathComponent)
                                        .filter(RootTreeNode.class::isInstance)
                                        .map(RootTreeNode.class::cast)
                                        .ifPresent(root -> handleSelectRoot(root.getName())));
            }
        }

        @Override
        public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                Object lastNode = getLastSelectedPathComponent();
                Optional.ofNullable(lastNode)
                        .filter(FeDirectoryTreeNode.class::isInstance)
                        .map(FeDirectoryTreeNode.class::cast)
                        .ifPresentOrElse(
                                node -> handleSelectDirectory(node.getDirectory()),
                                () -> Optional.ofNullable(lastNode)
                                        .filter(RootTreeNode.class::isInstance)
                                        .map(RootTreeNode.class::cast)
                                        .ifPresent(root -> handleSelectRoot(root.getName())));
            }
        }

    }

}
