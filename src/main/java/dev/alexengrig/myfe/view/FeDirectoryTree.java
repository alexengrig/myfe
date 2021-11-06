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
import dev.alexengrig.myfe.model.FeDirectory;
import dev.alexengrig.myfe.model.FeDirectoryTreeModel;
import dev.alexengrig.myfe.model.RootDirectoryTreeNode;
import dev.alexengrig.myfe.service.DirectoryTreeBackgroundService;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class FeDirectoryTree extends JTree {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DirectoryTreeBackgroundService backgroundService;

    private final List<FeDirectoryTreeListener> listeners;

    public FeDirectoryTree(
            FeDirectoryTreeModel model,
            DirectoryTreeBackgroundService backgroundService) {
        super(model);
        this.backgroundService = backgroundService;
        this.listeners = new LinkedList<>();
        init();
    }

    private void init() {
        LOGGER.debug("Start init");
        LoadChildDirectoriesListener loadChildDirectoriesListener = new LoadChildDirectoriesListener();
        addTreeWillExpandListener(loadChildDirectoriesListener);
        SelectNodeListener selectNodeListener = new SelectNodeListener();
        addMouseListener(selectNodeListener);
        addKeyListener(selectNodeListener);
        LOGGER.debug("Finished init");
    }

    @Override
    public FeDirectoryTreeModel getModel() {
        return (FeDirectoryTreeModel) super.getModel();
    }

    private void handleLoadChildDirectories(DirectoryTreeNode node) {
        LOGGER.debug("Handle load child directories: {}", node);
        FeDirectory directory = node.getUserObject();
        node.setLoaded(true);
        try {
            backgroundService.loadSubdirectories(directory, children -> {
                FeDirectoryTreeModel model = getModel();
                model.addChildrenInto(node, children);
                LOGGER.debug("Finished loading child directories for: {}; directories: {}", directory, children);
            });
        } catch (Exception exception) {
            node.setLoaded(false);
            //TODO: Open dialog of warning?
            LOGGER.warn("Exception of load child directories: {}", node, exception);
        }
    }

    public void addFeDirectoryTreeListener(FeDirectoryTreeListener listener) {
        listeners.add(listener);
    }

    public void removeFeDirectoryTreeListener(FeDirectoryTreeListener listener) {
        listeners.remove(listener);
    }

    private void fireSelectRoot(FeDirectoryTreeEvent event) {
        LOGGER.debug("Fire select root: {}", event);
        for (FeDirectoryTreeListener listener : listeners) {
            listener.selectRoot(event);
        }
    }

    private void fireSelectDirectory(FeDirectoryTreeEvent event) {
        LOGGER.debug("Fire select directory: {}", event);
        for (FeDirectoryTreeListener listener : listeners) {
            listener.selectDirectory(event);
        }
    }

    /**
     * On a tree expands a node.
     *
     * @see FeDirectoryTree#handleLoadChildDirectories(DirectoryTreeNode)
     */
    private class LoadChildDirectoriesListener implements DoNothingTreeWillExpandListener {

        @Override
        public void treeWillExpand(TreeExpansionEvent event) {
            //TODO: Add benchmark: vs Plain style
            Optional.ofNullable(event)
                    .map(TreeExpansionEvent::getPath)
                    .map(TreePath::getLastPathComponent)
                    .filter(DirectoryTreeNode.class::isInstance)
                    .map(DirectoryTreeNode.class::cast)
                    .filter(Predicate.not(DirectoryTreeNode::isLoaded))
                    .ifPresent(FeDirectoryTree.this::handleLoadChildDirectories);
        }

    }

    /**
     * On click the left mouse button and press the Enter key on a node.
     *
     * @see FeDirectoryTree#fireSelectDirectory(FeDirectoryTreeEvent)
     */
    private class SelectNodeListener implements DoNothingMouseListener, DoNothingKeyListener {

        @Override
        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                TreePath path = getPathForLocation(event.getX(), event.getY());
                //TODO: Add benchmark: vs Plain style
                Optional.ofNullable(path)
                        .map(TreePath::getLastPathComponent)
                        .filter(DirectoryTreeNode.class::isInstance)
                        .map(DirectoryTreeNode.class::cast)
                        .ifPresentOrElse(node -> fireSelectDirectory(new FeDirectoryTreeEvent(node.getUserObject())), () ->
                                Optional.ofNullable(path)
                                        .map(TreePath::getLastPathComponent)
                                        .filter(RootDirectoryTreeNode.class::isInstance)
                                        .map(RootDirectoryTreeNode.class::cast)
                                        .ifPresent(root -> fireSelectRoot(new FeDirectoryTreeEvent(root.getUserObject()))));
            }
        }

        @Override
        public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                Object lastNode = getLastSelectedPathComponent();
                //TODO: Add benchmark: vs Plain style
                Optional.ofNullable(lastNode)
                        .filter(DirectoryTreeNode.class::isInstance)
                        .map(DirectoryTreeNode.class::cast)
                        .ifPresentOrElse(node -> fireSelectDirectory(new FeDirectoryTreeEvent(node.getUserObject())), () ->
                                Optional.ofNullable(lastNode)
                                        .filter(RootDirectoryTreeNode.class::isInstance)
                                        .map(RootDirectoryTreeNode.class::cast)
                                        .ifPresent(root -> fireSelectRoot(new FeDirectoryTreeEvent(root.getUserObject()))));
            }
        }

    }

}
