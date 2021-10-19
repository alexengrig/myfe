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
import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.model.MyPathModel;
import dev.alexengrig.myfe.model.MyPathTableModel;
import dev.alexengrig.myfe.service.MyDirectoryTreeBackgroundService;
import dev.alexengrig.myfe.service.MyPathService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class MyTabComponent extends JPanel {

    private final MyPathService service;

    private MyDirectoryTreeModel treeModel;
    private MyPathTableModel tableModel;
    private MyPathModel pathModel;

    private MyHeader headerView;
    private MyDirectoryTree treeView;
    private MyPathTable tableView;
    private MyPathDetails detailsView;
    private MyPathPreview previewView;
    private MyFooter footerView;

    public MyTabComponent(MyPathService service) {
        super(new BorderLayout());
        this.service = service;
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
        tableModel = new MyPathTableModel(rootDirectories, new Object[]{"Name", "Type"});
        pathModel = new MyPathModel();
    }

    private void initViews() {
        headerView = new MyHeader();
        treeView = new MyDirectoryTree(treeModel, new TreeService());
        tableView = new MyPathTable(tableModel);
        detailsView = new MyPathDetails(pathModel);
        previewView = new MyPathPreview(pathModel);
        footerView = new MyFooter();
    }

    private void initListeners() {
        treeView.addMyDirectoryTreeListener(new TreeListener());
        tableView.addMyPathTableListener(new TableListener());
    }

    private void addComponents() {
        add(headerView, BorderLayout.NORTH);
        MySplitPane info = new MySplitPane.Vertical(new MyScrollPane(detailsView), new MyScrollPane(previewView));
        MySplitPane content = new MySplitPane.Horizontal(new MyScrollPane(tableView), info);
        MySplitPane center = new MySplitPane.Horizontal(new MyScrollPane(treeView), content);
        add(center, BorderLayout.CENTER);
        add(footerView, BorderLayout.SOUTH);
    }

    private static class BackgroundWorker<T> extends SwingWorker<T, Void> {

        private final Callable<T> task;
        private final Consumer<T> handler;

        private BackgroundWorker(Callable<T> task, Consumer<T> handler) {
            this.task = task;
            this.handler = handler;
        }

        public static <T> void execute(Callable<T> task, Consumer<T> handler) {
            BackgroundWorker<T> worker = new BackgroundWorker<>(task, handler);
            worker.execute();
        }

        @Override
        protected T doInBackground() throws Exception {
            return task.call();
        }

        @Override
        protected void done() {
            T result;
            try {
                result = get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            handler.accept(result);
        }

    }

    private class TreeListener implements MyDirectoryTreeListener {

        @Override
        public void selectRoot(MyDirectoryTreeEvent event) {
            //TODO: Spinner to table
            BackgroundWorker.execute(service::getRootDirectories, tableModel::update);
            pathModel.setPath(null);
        }

        @Override
        public void selectDirectory(MyDirectoryTreeEvent event) {
            MyDirectory directory = event.getDirectory();
            //TODO: Spinner to table
            BackgroundWorker.execute(() -> service.getContent(directory), tableModel::update);
            pathModel.setPath(null);
        }

    }

    private class TableListener implements MyPathTableListener {

        @Override
        public void selectPath(MyPathTableEvent event) {
            MyPath path = event.getPath();
            pathModel.setPath(path);
        }

    }

    private class TreeService implements MyDirectoryTreeBackgroundService {

        @Override
        public void loadSubdirectories(MyDirectory directory, Consumer<List<MyDirectory>> handler) {
            BackgroundWorker.execute(() -> service.getSubdirectories(directory), handler);
        }

    }

}
