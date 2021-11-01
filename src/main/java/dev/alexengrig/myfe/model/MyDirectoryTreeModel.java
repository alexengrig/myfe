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

package dev.alexengrig.myfe.model;

import javax.swing.tree.DefaultTreeModel;
import java.util.List;
import java.util.stream.Collectors;

public class MyDirectoryTreeModel extends DefaultTreeModel {

    public MyDirectoryTreeModel(String rootName, List<FeDirectory> rootDirectories) {
        super(new RootDirectoryTreeNode(rootName), true);
        addChildrenInto(getRoot(), rootDirectories);
    }

    public void addChildrenInto(MyTreeNode<?> parent, List<FeDirectory> children) {
        List<MyDirectoryTreeNode> nodes = children.stream()
                .map(MyDirectoryTreeNode::new)
                .collect(Collectors.toList());
        parent.addAll(nodes);
        nodeStructureChanged(parent);
    }

    @Override
    public RootDirectoryTreeNode getRoot() {
        return (RootDirectoryTreeNode) super.getRoot();
    }

}
