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

package dev.alexengrig.myfe;

import dev.alexengrig.myfe.controller.directory.DirectoryTreeController;
import dev.alexengrig.myfe.factory.directory.DefaultFileSystemDirectoryTreeFactory;
import dev.alexengrig.myfe.factory.directory.DirectoryTreeFactory;
import dev.alexengrig.myfe.model.directory.DirectoryTreeModel;
import dev.alexengrig.myfe.view.directory.DirectoryTreeView;

import javax.swing.*;

public final class MyfeApplication extends JFrame {

    private static final String TITLE = "myfe";

    private DirectoryTreeController treeController;

    public MyfeApplication() {
        super(TITLE);
        setVisible(true);
    }

    @Override
    protected void frameInit() {
        super.frameInit();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center of screen
        componentsInit();
    }

    private void componentsInit() {
        // Tree //TODO: Get factory from context
        DirectoryTreeFactory treeFactory = new DefaultFileSystemDirectoryTreeFactory();
        DirectoryTreeModel treeModel = treeFactory.createModel();
        DirectoryTreeView treeView = treeFactory.createView(treeModel);
        this.treeController = treeFactory.createController(treeView);
        //TODO: Content view (+ Preview view)
        JPanel contentView = new JPanel();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, treeView, contentView);
        add(splitPane);
    }

}
