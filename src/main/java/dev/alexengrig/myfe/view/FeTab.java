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

import dev.alexengrig.myfe.model.FeContentFilterModel;
import dev.alexengrig.myfe.model.FeContentTableModel;
import dev.alexengrig.myfe.model.FeDirectory;
import dev.alexengrig.myfe.model.FeDirectoryTreeModel;
import dev.alexengrig.myfe.model.FeFile;
import dev.alexengrig.myfe.model.FeFooterModel;
import dev.alexengrig.myfe.model.FePath;
import dev.alexengrig.myfe.model.FeSelectedPathModel;
import dev.alexengrig.myfe.service.BackgroundExecutorService;
import dev.alexengrig.myfe.service.ContentPreviewBackgroundService;
import dev.alexengrig.myfe.service.DirectoryTreeBackgroundService;
import dev.alexengrig.myfe.service.FePathService;
import dev.alexengrig.myfe.util.FePathUtil;
import dev.alexengrig.myfe.util.logging.LazyLogger;
import dev.alexengrig.myfe.util.logging.LazyLoggerFactory;
import dev.alexengrig.myfe.util.swing.BackgroundTask;
import dev.alexengrig.myfe.view.event.FeContentFilterEvent;
import dev.alexengrig.myfe.view.event.FeContentFilterListener;
import dev.alexengrig.myfe.view.event.FeContentTableEvent;
import dev.alexengrig.myfe.view.event.FeContentTableListener;
import dev.alexengrig.myfe.view.event.FeDirectoryTreeEvent;
import dev.alexengrig.myfe.view.event.FeDirectoryTreeListener;
import dev.alexengrig.myfe.view.event.FeTabEvent;
import dev.alexengrig.myfe.view.event.FeTabListener;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class FeTab extends JPanel {

    private static final LazyLogger LOGGER = LazyLoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<FeTabListener> listeners = new LinkedList<>();

    private final FePathService service;
    private final BackgroundExecutorService backgroundExecutor;
    private final String title;
    private final String tip;
    /**
     * Current directory on the top; {@code null} - the root.
     */
    private final Deque<FeDirectory> directoryStack;

    private FeDirectoryTreeModel treeModel;
    private FeContentTableModel tableModel;
    private FeContentFilterModel filterModel;
    private FeSelectedPathModel pathModel;
    private FeFooterModel footerModel;

    private FeHeader headerView;
    private FeDirectoryTree treeView;
    private FeContentTable tableView;
    private FePathDetails detailsView;
    private FePathPreview previewView;
    private FeFooter footerView;
    private FeContentFilter filterView;

    public FeTab(FePathService service, BackgroundExecutorService backgroundExecutor, String title, String tip) {
        super(new BorderLayout());
        this.service = service;
        this.backgroundExecutor = backgroundExecutor;
        this.title = title;
        this.tip = tip;
        this.directoryStack = new LinkedList<>();
        init();
        addComponents();
    }

    public String title() {
        return title;
    }

    public String tip() {
        return tip;
    }

    private void init() {
        LOGGER.debug("Start initializing");
        initModels();
        initViews();
        initListeners();
        LOGGER.debug("Finished initializing");
    }

    private void initModels() {
        LOGGER.debug("Start initializing models");
        //TODO: Getting root directories is slow - add spinner and background task
        List<FeDirectory> rootDirectories = service.getRootDirectories();
        treeModel = new FeDirectoryTreeModel(service.getRootName(), rootDirectories);
        tableModel = new FeContentTableModel(rootDirectories);
        filterModel = new FeContentFilterModel(rootDirectories);
        pathModel = new FeSelectedPathModel();
        footerModel = new FeFooterModel(rootDirectories.size());
        LOGGER.debug("Finished initializing models");
    }

    private void initViews() {
        LOGGER.debug("Start initializing views");
        headerView = new FeHeader();
        treeView = new FeDirectoryTree(treeModel, new TreeService());
        tableView = new FeContentTable(tableModel);
        filterView = new FeContentFilter(filterModel);
        detailsView = new FePathDetails(pathModel);
        previewView = new FePathPreview(pathModel, new PreviewService());
        footerView = new FeFooter(footerModel);
        LOGGER.debug("Finished initializing views");
    }

    private void initListeners() {
        LOGGER.debug("Start initializing listeners");
        treeView.addFeDirectoryTreeListener(new TreeListener());
        tableView.addFeContentTableListener(new TableListener());
        filterView.addFeContentFilterListener(new FilterListener());
        LOGGER.debug("Finished initializing listeners");
    }

    private void addComponents() {
        LOGGER.debug("Start adding components");
        add(headerView, BorderLayout.NORTH);
        MySplitPane info = new MySplitPane.Vertical(new MyScrollPane(detailsView), new MyScrollPane(previewView));
        info.setDividerLocation(50);
        JPanel filteredTable = new JPanel(new BorderLayout());
        filteredTable.add(new MyScrollPane(tableView), BorderLayout.CENTER);
        filteredTable.add(filterView, BorderLayout.SOUTH);
        MySplitPane content = new MySplitPane.Horizontal(filteredTable, info);
        content.setDividerLocation(content.getPreferredSize().width / 2);
        content.setResizeWeight(.5);
        MySplitPane center = new MySplitPane.Horizontal(new MyScrollPane(treeView), content);
        center.setDividerLocation(100);
        add(center, BorderLayout.CENTER);
        add(footerView, BorderLayout.SOUTH);
        LOGGER.debug("Finished adding components");
    }

    private void handleSelectRoot() {
        LOGGER.debug("Handle select root");
        //TODO: Spinner to table
        backgroundExecutor.execute(
                () -> "Getting root directories",
                service::getRootDirectories,
                paths -> {
                    tableModel.setPaths(paths);
                    filterModel.setPaths(paths);
                });
        pathModel.setPath(null);
        directoryStack.push(null);
    }

    private void handleSelectDirectory(FeDirectory directory) {
        LOGGER.debug("Handle select directory: {}", directory);
        //TODO: Spinner to table
        backgroundExecutor.execute(
                "Getting directory content:",
                () -> service.getDirectoryContent(directory),
                paths -> {
                    tableModel.setPaths(paths);
                    filterModel.setPaths(paths);
                });
        pathModel.setPath(null);
        directoryStack.push(directory);
    }

    private void handleSelectPath(FePath path) {
        LOGGER.debug("Handle select path: {}", path);
        pathModel.setPath(path);
    }

    private void handleGoToPreviousDirectory() {
        FeDirectory currentDirectory = directoryStack.poll();
        FeDirectory previousDirectory = directoryStack.poll();
        LOGGER.debug("Handle go to previous directory: {}", previousDirectory);
        if (previousDirectory == null) {
            handleSelectRoot();
        } else {
            handleSelectDirectory(previousDirectory);
        }
    }

    private void handleFilterType(String type) {
        LOGGER.debug("Handle filter type: {}", type);
        tableModel.setFilteredType(type);
    }

    private void handleChangeNumberOfElements(Integer numberOfElements) {
        LOGGER.debug("Handle change number of elements: {}", numberOfElements);
        footerModel.setNumberOfElements(numberOfElements);
    }

    public void addFeTabListener(FeTabListener listener) {
        listeners.add(listener);
    }

    public void removeFeTabListener(FeTabListener listener) {
        listeners.remove(listener);
    }

    private void fireOpenArchive(FeTabEvent event) {
        LOGGER.debug("Fire open archive: {}", event);
        for (FeTabListener listener : listeners) {
            listener.openArchive(event);
        }
    }

    public void destroy() {
        LOGGER.debug("Start destroying");
        service.destroy();
        LOGGER.debug("Finished destroying");
    }

    /**
     * Events from Tree.
     *
     * @see FeTab#handleSelectRoot()
     * @see FeTab#handleSelectDirectory(FeDirectory)
     */
    private class TreeListener implements FeDirectoryTreeListener {

        @Override
        public void selectRoot(FeDirectoryTreeEvent event) {
            handleSelectRoot();
        }

        @Override
        public void selectDirectory(FeDirectoryTreeEvent event) {
            FeDirectory directory = event.getDirectory();
            handleSelectDirectory(directory);
        }

    }

    /**
     * Events from table.
     *
     * @see FeTab#handleSelectPath(FePath)
     * @see FeTab#handleSelectDirectory(FeDirectory)
     * @see FeTab#handleGoToPreviousDirectory()
     * @see FeTab#handleChangeNumberOfElements(Integer)
     * @see FeTab#fireOpenArchive(FeTabEvent)
     */
    private class TableListener implements FeContentTableListener {

        @Override
        public void selectPath(FeContentTableEvent event) {
            FePath path = event.getPath();
            handleSelectPath(path);
        }

        @Override
        public void doubleClickOnPath(FeContentTableEvent event) {
            FePath path = event.getPath();
            if (path.isDirectory()) {
                handleSelectDirectory(path.asDirectory());
            } else if (FePathUtil.isArchive(path.asFile())) {
                fireOpenArchive(new FeTabEvent(path.asFile()));
            }
        }

        @Override
        public void goBack(FeContentTableEvent event) {
            handleGoToPreviousDirectory();
        }

        @Override
        public void changeRowCount(FeContentTableEvent event) {
            handleChangeNumberOfElements(event.getRowCount());
        }

    }

    /**
     * Events from table filter.
     *
     * @see FeTab#handleFilterType(String)
     */
    private class FilterListener implements FeContentFilterListener {

        @Override
        public void filterType(FeContentFilterEvent event) {
            String type = event.getType();
            handleFilterType(type);
        }

    }

    /**
     * Service for tree.
     */
    private class TreeService implements DirectoryTreeBackgroundService {

        @Override
        public void loadSubdirectories(FeDirectory directory, Consumer<List<FeDirectory>> handler) {
            LOGGER.debug("Start loading subdirectories for: {}", directory);
            backgroundExecutor.execute(
                    () -> "Loading subdirectories for: " + directory,
                    () -> service.getSubdirectories(directory),
                    result -> {
                        handler.accept(result);
                        LOGGER.debug("Finished loading subdirectories for: {}", directory);
                    });
        }

    }

    /**
     * Service for preview.
     */
    private class PreviewService implements ContentPreviewBackgroundService {

        private BackgroundTask previousTask;

        @Override
        public void loadTextPreview(FeFile file, Consumer<String> handler) {
            LOGGER.debug("Start loading text preview for: {}", file);
            cancelPreviousTaskIfNeed();
            previousTask = backgroundExecutor.execute(() -> "Loading text preview for: " + file, () -> service.getFileContentPreview(file), result -> {
                handler.accept(result);
                LOGGER.debug("Finished loading text preview for: {}", file);
            });
        }

        private void cancelPreviousTaskIfNeed() {
            if (previousTask == null || previousTask.isDone()) {
                return;
            }
            if (!previousTask.cancel() && !previousTask.isDone()) {
                LOGGER.warn("Failed to cancel previous task");
            }
        }

    }

}
