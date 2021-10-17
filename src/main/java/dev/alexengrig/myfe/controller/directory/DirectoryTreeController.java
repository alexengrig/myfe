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
import dev.alexengrig.myfe.service.directory.DirectoryService;
import dev.alexengrig.myfe.util.BackgroundWorker;
import dev.alexengrig.myfe.view.directory.DirectoryTreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.TreePath;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class DirectoryTreeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DirectoryTreeView view;
    private final DirectoryService service;

    public DirectoryTreeController(DirectoryTreeView view, DirectoryService service) {
        this.view = view;
        this.service = service;
        init();
    }

    private void init() {
        view.addTreeWillExpandListener(new MyTreeWillExpandListener());
    }

    private <T> void runInBackground(
            Callable<T> action, Consumer<T> onSuccess, Consumer<Exception> onFailure) {
        BackgroundWorker.from(action, onSuccess, Exception.class, onFailure).execute();
    }

    private void loadChildrenDirectories(DirectoryTreeModel.Node node) {
        DirectoryModel model = node.model();
        if (model.isLoaded()) {
            LOGGER.debug("Model already loaded: {}", model);
            return;
        }
        model.setLoaded(true);
        LOGGER.debug("Start loading subdirectories for: {}", model);
        runInBackground(
                () -> service.getSubdirectories(model.getPath()),
                children -> {
                    LOGGER.debug("Subdirectories loaded successfully for: {}", model);
                    node.setChildren(children);
                },
                exception -> {
                    LOGGER.warn("Exception of subdirectories loading for: {}", model, exception);
                    model.setLoaded(false);
                });
    }

    private class MyTreeWillExpandListener implements TreeWillExpandListener {

        @Override
        public void treeWillExpand(TreeExpansionEvent event) {
            Optional.of(event)
                    .map(TreeExpansionEvent::getPath)
                    .map(TreePath::getLastPathComponent)
                    .filter(DirectoryTreeModel.Node.class::isInstance)
                    .map(DirectoryTreeModel.Node.class::cast)
                    .ifPresent(DirectoryTreeController.this::loadChildrenDirectories);
        }

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) {
            // do nothing
        }

    }

}
