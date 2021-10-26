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
import dev.alexengrig.myfe.model.MyFile;
import dev.alexengrig.myfe.model.MyFooterModel;
import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.model.MyPathFilterModel;
import dev.alexengrig.myfe.model.MyPathModel;
import dev.alexengrig.myfe.model.MyPathTableModel;
import dev.alexengrig.myfe.service.MyDirectoryTreeBackgroundService;
import dev.alexengrig.myfe.service.MyPathPreviewBackgroundService;
import dev.alexengrig.myfe.service.MyPathService;
import dev.alexengrig.myfe.util.BackgroundExecutor;
import dev.alexengrig.myfe.util.BackgroundStreamer;
import dev.alexengrig.myfe.util.MyPathUtil;
import dev.alexengrig.myfe.view.event.MyDirectoryTreeEvent;
import dev.alexengrig.myfe.view.event.MyDirectoryTreeListener;
import dev.alexengrig.myfe.view.event.MyPathFilterEvent;
import dev.alexengrig.myfe.view.event.MyPathFilterListener;
import dev.alexengrig.myfe.view.event.MyPathTableEvent;
import dev.alexengrig.myfe.view.event.MyPathTableListener;
import dev.alexengrig.myfe.view.event.MyTabEvent;
import dev.alexengrig.myfe.view.event.MyTabListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MyTab extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<MyTabListener> listeners = new LinkedList<>();

    private final MyPathService service;
    private final String title;
    private final String tip;
    /**
     * Current directory on the top; {@code null} - the root.
     */
    private final Deque<MyDirectory> directoryStack;

    private MyDirectoryTreeModel treeModel;
    private MyPathTableModel tableModel;
    private MyPathFilterModel filterModel;
    private MyPathModel pathModel;
    private MyFooterModel footerModel;

    private MyHeader headerView;
    private MyDirectoryTree treeView;
    private MyPathTable tableView;
    private MyPathDetails detailsView;
    private MyPathPreview previewView;
    private MyFooter footerView;
    private MyPathFilter filterView;

    public MyTab(MyPathService service, String title, String tip) {
        super(new BorderLayout());
        this.service = service;
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
        List<MyDirectory> rootDirectories = service.getRootDirectories();
        treeModel = new MyDirectoryTreeModel(service.getRootName(), rootDirectories);
        tableModel = new MyPathTableModel(rootDirectories);
        filterModel = new MyPathFilterModel(rootDirectories);
        pathModel = new MyPathModel();
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
        BackgroundExecutor.execute(service::getRootDirectories, paths -> {
            tableModel.setPaths(paths);
            filterModel.setPaths(paths);
        });
        pathModel.setPath(null);
        directoryStack.push(null);
    }

    private void handleSelectDirectory(MyDirectory directory) {
        LOGGER.debug("Handle select directory: {}", directory);
        //TODO: Spinner to table
        BackgroundExecutor.execute(() -> service.getDirectoryContent(directory), paths -> {
            tableModel.setPaths(paths);
            filterModel.setPaths(paths);
        });
        pathModel.setPath(null);
        directoryStack.push(directory);
    }

    private void handleSelectPath(MyPath path) {
        LOGGER.debug("Handle select path: {}", path);
        pathModel.setPath(path);
    }

    private void handleGoToPreviousDirectory() {
        MyDirectory currentDirectory = directoryStack.poll();
        MyDirectory previousDirectory = directoryStack.poll();
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
     * @see MyTab#handleSelectDirectory(MyDirectory)
     */
    private class TreeListener implements MyDirectoryTreeListener {

        @Override
        public void selectRoot(MyDirectoryTreeEvent event) {
            handleSelectRoot();
        }

        @Override
        public void selectDirectory(MyDirectoryTreeEvent event) {
            MyDirectory directory = event.getDirectory();
            handleSelectDirectory(directory);
        }

    }

    /**
     * Events from table.
     *
     * @see MyTab#handleSelectPath(MyPath)
     * @see MyTab#handleSelectDirectory(MyDirectory)
     * @see MyTab#handleGoToPreviousDirectory()
     * @see MyTab#handleChangeNumberOfElements(Integer)
     * @see MyTab#fireOpenArchive(MyTabEvent)
     */
    private class TableListener implements MyPathTableListener {

        @Override
        public void selectPath(MyPathTableEvent event) {
            MyPath path = event.getPath();
            handleSelectPath(path);
        }

        @Override
        public void doubleClickOnPath(MyPathTableEvent event) {
            MyPath path = event.getPath();
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
        public void loadSubdirectories(MyDirectory directory, Consumer<List<MyDirectory>> handler) {
            BackgroundExecutor.execute(() -> service.getSubdirectories(directory), handler);
        }

    }

    /**
     * Service for preview.
     */
    private class PreviewService implements MyPathPreviewBackgroundService {

        private BackgroundStreamer.Task<String> previousTask;

        @Override
        public void loadTextPreview(MyFile file, Consumer<String> handler) {
            // cancel previous task if need
            if (previousTask != null && !previousTask.isDone() && !previousTask.cancel()) {
                LOGGER.warn("Failed to cancel previous task");
            }
            previousTask = BackgroundStreamer.stream(
                    () -> service.readFileContentInBatches(file),
                    strings -> handler.accept(strings.collect(Collectors.joining())));
        }

    }

}
