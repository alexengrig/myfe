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

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class DirectoryTreeNode extends DefaultMutableTreeNode {

    private boolean loaded;

    public DirectoryTreeNode(MyDirectory directory) {
        super(Objects.requireNonNull(directory, "The directory must not be null"), true);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void addAll(List<DirectoryTreeNode> nodes) {
        if (children == null) {
            children = new Vector<>();
        }
        children.addAll(nodes);
    }

    @Override
    public MyDirectory getUserObject() {
        return (MyDirectory) super.getUserObject();
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public String toString() {
        return getUserObject().getName();
    }

}
