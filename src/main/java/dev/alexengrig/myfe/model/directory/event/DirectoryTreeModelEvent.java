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

package dev.alexengrig.myfe.model.directory.event;

import dev.alexengrig.myfe.model.directory.DirectoryTreeModel;

import java.util.EventObject;
import java.util.Objects;

public class DirectoryTreeModelEvent extends EventObject {

    private final DirectoryTreeModel.Node node;

    public DirectoryTreeModelEvent(DirectoryTreeModel source, DirectoryTreeModel.Node node) {
        super(Objects.requireNonNull(source, "The source must not be null"));
        this.node = node;
    }

    public DirectoryTreeModel getModel() {
        return (DirectoryTreeModel) super.getSource();
    }

    public DirectoryTreeModel.Node getNode() {
        return node;
    }

}
