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

package dev.alexengrig.myfe.domain;

import javax.swing.tree.TreeNode;
import java.util.function.Function;

public class SubdirectoryTreeNode extends FeDirectoryTreeNode {

    private final FeDirectoryTreeNode parent;

    public SubdirectoryTreeNode(FeDirectoryTreeNode parent, FeDirectory directory) {
        super(directory);
        this.parent = parent;
    }

    public static Function<FeDirectory, SubdirectoryTreeNode> factory(FeDirectoryTreeNode parent) {
        return directory -> new SubdirectoryTreeNode(parent, directory);
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

}
