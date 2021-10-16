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

package dev.alexengrig.myfe.controller.directory;

import dev.alexengrig.myfe.model.directory.DirectoryModel;
import dev.alexengrig.myfe.model.directory.DirectoryTreeModel;
import dev.alexengrig.myfe.view.directory.DictionaryTreeView;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.TreePath;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DictionaryTreeController {

    private final DictionaryTreeView view;
    private final FileSystem fileSystem;

    public DictionaryTreeController(DictionaryTreeView view, FileSystem fileSystem) {
        this.view = view;
        this.fileSystem = fileSystem;
        init();
    }

    protected void init() {
        view.addTreeWillExpandListener(new MyTreeWillExpandListener());
    }

    protected class MyTreeWillExpandListener implements TreeWillExpandListener {

        @Override
        public void treeWillExpand(TreeExpansionEvent event) {
            Optional<DirectoryTreeModel.Node> optionalNode = Optional.of(event)
                    .map(TreeExpansionEvent::getPath)
                    .map(TreePath::getLastPathComponent)
                    .filter(DirectoryTreeModel.Node.class::isInstance)
                    .map(DirectoryTreeModel.Node.class::cast);
            if (optionalNode.isEmpty()) {
                return;
            }
            DirectoryTreeModel.Node node = optionalNode.get();
            DirectoryModel model = node.model();
            if (model.isLoaded()) {
                return;
            }
            model.setLoaded(true);
            ChildrenLoader loader = new ChildrenLoader(node, model);
            loader.execute();
        }

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) {
            // do nothing
        }

    }

    private class ChildrenLoader extends SwingWorker<List<DirectoryModel>, Void> {

        private final DirectoryTreeModel.Node node;
        private final DirectoryModel model;

        public ChildrenLoader(DirectoryTreeModel.Node node, DirectoryModel model) {
            this.node = node;
            this.model = model;
        }

        @Override
        protected void done() {
            try {
                List<DirectoryModel> models = get();
                node.setChildren(models);
            } catch (InterruptedException | ExecutionException e) {
                model.setLoaded(false);
                e.printStackTrace();
            }
        }

        @Override
        protected List<DirectoryModel> doInBackground() throws Exception {
            String path = model.getPath();
            if (path != null) {
                Path dir = fileSystem.getPath(path);
                return Files.list(dir)
                        .filter(Files::isDirectory)
                        .map(DirectoryModel::from)
                        .collect(Collectors.toList());
            } else {
                return StreamSupport.stream(fileSystem.getRootDirectories().spliterator(), false)
                        .map(DirectoryModel::from)
                        .collect(Collectors.toList());
            }
        }

    }

}
