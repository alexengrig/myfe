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
import dev.alexengrig.myfe.service.MyPathService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MyTabComponent extends JPanel {

    private final MyPathService service;

    public MyTabComponent(MyPathService service) {
        super(new BorderLayout());
        this.service = service;
        initComponents();
    }

    private void initComponents() {
        initHeader();
        initBody();
        initFooter();
    }

    private void initHeader() {
        //TODO: Create header component
        JPanel top = new JPanel();
        top.add(new JLabel("<- -> ^     C:\\Users\\admin     refresh"));
        add(top, BorderLayout.NORTH);
    }

    private void initBody() {
        // Tree
        //TODO: Getting root directories is slow - add spinner and background task
        List<MyDirectory> rootDirectories = service.getRootDirectories();
        MyDirectoryTreeModel treeModel = new MyDirectoryTreeModel(service.getName(), rootDirectories);
        MyDirectoryTree tree = new MyDirectoryTree(treeModel);
        //TODO: Run in background
        tree.onLoadSubdirectories(service::getSubdirectories);
        // Table
        MyPathTableModel tableModel = new MyPathTableModel(rootDirectories, new Object[]{"Name", "Type"});
        MyPathTable table = new MyPathTable(tableModel);
        tree.onSelectRootDirectory(() -> {
            //TODO: Run in background
            tableModel.update(rootDirectories);
        });
        tree.onSelectDirectory(directory -> {
            //TODO: Run in background
            List<MyPath> content = service.getContent(directory);
            tableModel.update(content);
        });
        // Details
        MyPathModel pathModel = new MyPathModel();
        MyPathDetails details = new MyPathDetails(pathModel);
        table.onSelectPath(pathModel::setPath);
        // Preview
        MyPathPreview preview = new MyPathPreview(pathModel);
        MySplitPane info = new MySplitPane.Vertical(new MyScrollPane(details), new MyScrollPane(preview));
        MySplitPane content = new MySplitPane.Horizontal(new MyScrollPane(table), info);
        MySplitPane center = new MySplitPane.Horizontal(new MyScrollPane(tree), content);
        add(center, BorderLayout.CENTER);
    }

    private void initFooter() {
        //TODO: Create footer component
        JPanel bottom = new JPanel();
        bottom.add(new JLabel("Number of elements: 123"));
        add(bottom, BorderLayout.SOUTH);
    }

}
