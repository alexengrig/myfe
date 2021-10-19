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

import dev.alexengrig.myfe.model.DirectoryTreeNode;
import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyTreeModel;
import dev.alexengrig.myfe.model.RootDirectoryTreeNode;
import dev.alexengrig.myfe.service.MyPathService;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
        RootDirectoryTreeNode treeRootNode = new RootDirectoryTreeNode(service.getName(), service.getRootDirectories());
        MyTreeModel treeModel = new MyTreeModel(treeRootNode);
        MyTree tree = new MyTree(treeModel);
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) {
                //TODO: Add benchmark: vs Plain style
                Optional.of(event)
                        .map(TreeExpansionEvent::getPath)
                        .map(TreePath::getLastPathComponent)
                        .filter(DirectoryTreeNode.class::isInstance)
                        .map(DirectoryTreeNode.class::cast)
                        .filter(Predicate.not(DirectoryTreeNode::isLoaded))
                        .ifPresent(node -> {
                            node.setLoaded(true);
                            MyDirectory directory = node.getUserObject();
                            try {
                                //TODO: Run in background
                                List<MyDirectory> subdirectories = service.getSubdirectories(directory);
                                node.addAll(subdirectories);
                            } catch (Exception e) {
                                node.setLoaded(false);
                            }
                        });
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) {
            }
        });
        // Table
        MyTable table = new MyTable();
        MyDetails details = new MyDetails();
        MyPreview preview = new MyPreview();
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
