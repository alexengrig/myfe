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

import dev.alexengrig.myfe.model.directory.ContentModel;
import dev.alexengrig.myfe.model.directory.DirectoryContentModel;
import dev.alexengrig.myfe.model.directory.DirectoryModel;
import dev.alexengrig.myfe.model.directory.DirectoryTreeModel;
import dev.alexengrig.myfe.service.directory.DefaultFileSystemDirectoryService;
import dev.alexengrig.myfe.service.directory.DirectoryService;
import dev.alexengrig.myfe.view.directory.DirectoryContentView;
import dev.alexengrig.myfe.view.directory.DirectoryTreeView;

import javax.swing.tree.TreePath;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DirectoryContentController {

    //TODO: Get from context
    private final DirectoryService service = new DefaultFileSystemDirectoryService();

    private final DirectoryContentView contentView;

    public DirectoryContentController(DirectoryContentView contentView) {
        this.contentView = contentView;
    }

    private void changeDirectory(DirectoryModel directoryModel) {
        DirectoryContentModel contentModel = contentView.getModel();
        //TODO: Run in background
        List<ContentModel> content = service.getContent(directoryModel.getPath());
        contentModel.setContent(content);
    }

    public void subscribeOn(DirectoryTreeView treeView) {
        treeView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TreePath path = treeView.getPathForLocation(e.getX(), e.getY());
                if (path == null) {
                    return;
                }
                DirectoryTreeModel.Node node = (DirectoryTreeModel.Node) path.getLastPathComponent();
                DirectoryModel directoryModel = node.model();
                changeDirectory(directoryModel);
            }
        });
        treeView.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }
                DirectoryTreeModel.Node node = (DirectoryTreeModel.Node) treeView.getLastSelectedPathComponent();
                DirectoryModel model = node.model();
                changeDirectory(model);
            }
        });
    }

}
