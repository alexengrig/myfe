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
import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.model.MyPathFilterModel;
import dev.alexengrig.myfe.model.MyPathModel;
import dev.alexengrig.myfe.model.MyPathTableModel;
import dev.alexengrig.myfe.service.MyDirectoryTreeBackgroundService;
import dev.alexengrig.myfe.service.MyPathPreviewBackgroundService;
import dev.alexengrig.myfe.service.MyPathService;
import dev.alexengrig.myfe.util.BackgroundExecutor;
import dev.alexengrig.myfe.util.BackgroundStreamer;
import dev.alexengrig.myfe.view.event.MyDirectoryTreeEvent;
import dev.alexengrig.myfe.view.event.MyDirectoryTreeListener;
import dev.alexengrig.myfe.view.event.MyPathFilterEvent;
import dev.alexengrig.myfe.view.event.MyPathFilterListener;
import dev.alexengrig.myfe.view.event.MyPathTableEvent;
import dev.alexengrig.myfe.view.event.MyPathTableListener;

import javax.swing.*;
import java.awt.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MyTabComponent extends JPanel {

    private final MyPathService service;
    /**
     * Current directory on the top; {@code null} - the root.
     */
    private final Deque<MyDirectory> directoryStack;

    private MyDirectoryTreeModel treeModel;
    private MyPathTableModel tableModel;
    private MyPathFilterModel filterModel;
    private MyPathModel pathModel;

    private MyHeader headerView;
    private MyDirectoryTree treeView;
    private MyPathTable tableView;
    private MyPathDetails detailsView;
    private MyPathPreview previewView;
    private MyFooter footerView;
    private MyPathFilter filterView;

    public MyTabComponent(MyPathService service) {
        super(new BorderLayout());
        this.service = service;
        this.directoryStack = new LinkedList<>();
        init();
        addComponents();
    }

    private void init() {
        initModels();
        initViews();
        initListeners();
    }

    private void initModels() {
        //TODO: Getting root directories is slow - add spinner and background task
        List<MyDirectory> rootDirectories = service.getRootDirectories();
        treeModel = new MyDirectoryTreeModel(service.getName(), rootDirectories);
        tableModel = new MyPathTableModel(rootDirectories);
        filterModel = new MyPathFilterModel(rootDirectories);
        pathModel = new MyPathModel();
    }

    private void initViews() {
        headerView = new MyHeader();
        treeView = new MyDirectoryTree(treeModel, new TreeService());
        tableView = new MyPathTable(tableModel);
        filterView = new MyPathFilter(filterModel);
        detailsView = new MyPathDetails(pathModel);
        previewView = new MyPathPreview(pathModel, new PreviewService());
        footerView = new MyFooter();
    }

    private void initListeners() {
        treeView.addMyDirectoryTreeListener(new TreeListener());
        tableView.addMyPathTableListener(new TableListener());
        filterView.addMyPathFilterListener(new FilterListener());
    }

    private void addComponents() {
        add(headerView, BorderLayout.NORTH);
        MySplitPane info = new MySplitPane.Vertical(new MyScrollPane(detailsView), new MyScrollPane(previewView));
        JPanel filteredTable = new JPanel(new BorderLayout());
        filteredTable.add(new MyScrollPane(tableView), BorderLayout.CENTER);
        filteredTable.add(filterView, BorderLayout.SOUTH);
        MySplitPane content = new MySplitPane.Horizontal(filteredTable, info);
        MySplitPane center = new MySplitPane.Horizontal(new MyScrollPane(treeView), content);
        add(center, BorderLayout.CENTER);
        add(footerView, BorderLayout.SOUTH);
    }

    private void handleSelectRoot() {
        //TODO: Spinner to table
        BackgroundExecutor.execute(service::getRootDirectories, paths -> {
            tableModel.setPaths(paths);
            filterModel.setPaths(paths);
        });
        pathModel.setPath(null);
        directoryStack.push(null);
    }

    private void handleSelectDirectory(MyDirectory directory) {
        //TODO: Spinner to table
        BackgroundExecutor.execute(() -> service.getDirectoryContent(directory), paths -> {
            tableModel.setPaths(paths);
            filterModel.setPaths(paths);
        });
        pathModel.setPath(null);
        directoryStack.push(directory);
    }

    private void handleGoToPreviousDirectory() {
        MyDirectory currentDirectory = directoryStack.poll();
        MyDirectory previousDirectory = directoryStack.poll();
        if (previousDirectory == null) {
            handleSelectRoot();
        } else {
            handleSelectDirectory(previousDirectory);
        }
    }

    private void handleFilterType(String type) {
        tableModel.setFilteredType(type);
    }

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

    private class TableListener implements MyPathTableListener {

        @Override
        public void selectPath(MyPathTableEvent event) {
            MyPath path = event.getPath();
            pathModel.setPath(path);
        }

        @Override
        public void doubleClickOnPath(MyPathTableEvent event) {
            MyPath path = event.getPath();
            if (path.isDirectory()) {
                handleSelectDirectory(path.asDirectory());
            }
        }

        @Override
        public void goBack(MyPathTableEvent event) {
            handleGoToPreviousDirectory();
        }

    }

    private class FilterListener implements MyPathFilterListener {

        @Override
        public void filterType(MyPathFilterEvent event) {
            String type = event.getType();
            handleFilterType(type);
        }

    }

    private class TreeService implements MyDirectoryTreeBackgroundService {

        @Override
        public void loadSubdirectories(MyDirectory directory, Consumer<List<MyDirectory>> handler) {
            BackgroundExecutor.execute(() -> service.getSubdirectories(directory), handler);
        }

    }

    private class PreviewService implements MyPathPreviewBackgroundService {

        @Override
        public void loadTextPreview(MyFile file, Consumer<Stream<String>> handler) {
            BackgroundStreamer.stream(() -> service.readByLineFileContent(file), handler);
        }

    }

}
