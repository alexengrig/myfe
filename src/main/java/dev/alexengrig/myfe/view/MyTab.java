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

import dev.alexengrig.myfe.model.FeDirectory;
import dev.alexengrig.myfe.model.FeFile;
import dev.alexengrig.myfe.model.FePath;
import dev.alexengrig.myfe.model.MyDirectoryTreeModel;
import dev.alexengrig.myfe.model.MyFooterModel;
import dev.alexengrig.myfe.model.MyPathFilterModel;
import dev.alexengrig.myfe.model.MyPathTableModel;
import dev.alexengrig.myfe.model.SelectedFePathModel;
import dev.alexengrig.myfe.service.BackgroundExecutorService;
import dev.alexengrig.myfe.service.MyDirectoryTreeBackgroundService;
import dev.alexengrig.myfe.service.MyPathPreviewBackgroundService;
import dev.alexengrig.myfe.service.MyPathService;
import dev.alexengrig.myfe.util.MyPathUtil;
import dev.alexengrig.myfe.util.logging.LazyLogger;
import dev.alexengrig.myfe.util.logging.LazyLoggerFactory;
import dev.alexengrig.myfe.util.swing.BackgroundTask;
import dev.alexengrig.myfe.view.event.MyDirectoryTreeEvent;
import dev.alexengrig.myfe.view.event.MyDirectoryTreeListener;
import dev.alexengrig.myfe.view.event.MyPathFilterEvent;
import dev.alexengrig.myfe.view.event.MyPathFilterListener;
import dev.alexengrig.myfe.view.event.MyPathTableEvent;
import dev.alexengrig.myfe.view.event.MyPathTableListener;
import dev.alexengrig.myfe.view.event.MyTabEvent;
import dev.alexengrig.myfe.view.event.MyTabListener;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MyTab extends JPanel {

    private static final LazyLogger LOGGER = LazyLoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<MyTabListener> listeners = new LinkedList<>();

    private final MyPathService service;
    private final BackgroundExecutorService backgroundExecutor;
    private final String title;
    private final String tip;
    /**
     * Current directory on the top; {@code null} - the root.
     */
    private final Deque<FeDirectory> directoryStack;

    private MyDirectoryTreeModel treeModel;
    private MyPathTableModel tableModel;
    private MyPathFilterModel filterModel;
    private SelectedFePathModel pathModel;
    private MyFooterModel footerModel;

    private MyHeader headerView;
    private MyDirectoryTree treeView;
    private MyPathTable tableView;
    private MyPathDetails detailsView;
    private MyPathPreview previewView;
    private MyFooter footerView;
    private MyPathFilter filterView;

    public MyTab(MyPathService service, BackgroundExecutorService backgroundExecutor, String title, String tip) {
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
        treeModel = new MyDirectoryTreeModel(service.getRootName(), rootDirectories);
        tableModel = new MyPathTableModel(rootDirectories);
        filterModel = new MyPathFilterModel(rootDirectories);
        pathModel = new SelectedFePathModel();
        footerModel = new MyFooterModel(rootDirectories.size());
        LOGGER.debug("Finished initializing models");
    }

    private void initViews() {
        LOGGER.debug("Start initializing views");
        headerView = new MyHeader();
        treeView = new MyDirectoryTree(treeModel, new TreeService());
        tableView = new MyPathTable(tableModel);
        filterView = new MyPathFilter(filterModel);
        detailsView = new MyPathDetails(pathModel);
        previewView = new MyPathPreview(pathModel, new PreviewService());
        footerView = new MyFooter(footerModel);
        LOGGER.debug("Finished initializing views");
    }

    private void initListeners() {
        LOGGER.debug("Start initializing listeners");
        treeView.addMyDirectoryTreeListener(new TreeListener());
        tableView.addMyPathTableListener(new TableListener());
        filterView.addMyPathFilterListener(new FilterListener());
        LOGGER.debug("Finished initializing listeners");
    }

    private void addComponents() {
        LOGGER.debug("Start adding components");
        add(headerView, BorderLayout.NORTH);
        MySplitPane info = new MySplitPane.Vertical(new MyScrollPane(detailsView), new MyScrollPane(previewView));
        JPanel filteredTable = new JPanel(new BorderLayout());
        filteredTable.add(new MyScrollPane(tableView), BorderLayout.CENTER);
        filteredTable.add(filterView, BorderLayout.SOUTH);
        MySplitPane content = new MySplitPane.Horizontal(filteredTable, info);
        MySplitPane center = new MySplitPane.Horizontal(new MyScrollPane(treeView), content);
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

    public void addMyTabListener(MyTabListener listener) {
        listeners.add(listener);
    }

    public void removeMyTabComponentListener(MyTabListener listener) {
        listeners.remove(listener);
    }

    private void fireOpenArchive(MyTabEvent event) {
        LOGGER.debug("Fire open archive: {}", event);
        for (MyTabListener listener : listeners) {
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
     * @see MyTab#handleSelectRoot()
     * @see MyTab#handleSelectDirectory(FeDirectory)
     */
    private class TreeListener implements MyDirectoryTreeListener {

        @Override
        public void selectRoot(MyDirectoryTreeEvent event) {
            handleSelectRoot();
        }

        @Override
        public void selectDirectory(MyDirectoryTreeEvent event) {
            FeDirectory directory = event.getDirectory();
            handleSelectDirectory(directory);
        }

    }

    /**
     * Events from table.
     *
     * @see MyTab#handleSelectPath(FePath)
     * @see MyTab#handleSelectDirectory(FeDirectory)
     * @see MyTab#handleGoToPreviousDirectory()
     * @see MyTab#handleChangeNumberOfElements(Integer)
     * @see MyTab#fireOpenArchive(MyTabEvent)
     */
    private class TableListener implements MyPathTableListener {

        @Override
        public void selectPath(MyPathTableEvent event) {
            FePath path = event.getPath();
            handleSelectPath(path);
        }

        @Override
        public void doubleClickOnPath(MyPathTableEvent event) {
            FePath path = event.getPath();
            if (path.isDirectory()) {
                handleSelectDirectory(path.asDirectory());
            } else if (MyPathUtil.isArchive(path.asFile())) {
                fireOpenArchive(new MyTabEvent(path.asFile()));
            }
        }

        @Override
        public void goBack(MyPathTableEvent event) {
            handleGoToPreviousDirectory();
        }

        @Override
        public void changeRowCount(MyPathTableEvent event) {
            handleChangeNumberOfElements(event.getRowCount());
        }

    }

    /**
     * Events from table filter.
     *
     * @see MyTab#handleFilterType(String)
     */
    private class FilterListener implements MyPathFilterListener {

        @Override
        public void filterType(MyPathFilterEvent event) {
            String type = event.getType();
            handleFilterType(type);
        }

    }

    /**
     * Service for tree.
     */
    private class TreeService implements MyDirectoryTreeBackgroundService {

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
    private class PreviewService implements MyPathPreviewBackgroundService {

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
