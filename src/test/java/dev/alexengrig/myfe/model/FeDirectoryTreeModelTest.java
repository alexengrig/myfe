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

import dev.alexengrig.myfe.domain.FeDirectory;
import dev.alexengrig.myfe.domain.FeDirectoryTreeNode;
import dev.alexengrig.myfe.domain.RootDirectoryTreeNode;
import dev.alexengrig.myfe.domain.RootTreeNode;
import dev.alexengrig.myfe.model.event.FeDirectoryTreeModelListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.event.TreeModelEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class FeDirectoryTreeModelTest {

    static final String ROOT_NAME = "test-root";

    final List<TreeModelEvent> events = new LinkedList<>();

    final FeDirectoryTreeModel model = new FeDirectoryTreeModel(ROOT_NAME, Collections.emptyList());

    FeDirectoryTreeModelListener listener;

    @BeforeEach
    void beforeEach() {
        listener = new FeDirectoryTreeModelListener() {

            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                fail();
            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {
                fail();
            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {
                fail();
            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) {
                events.add(e);
            }

        };
        model.addTreeModelListener(listener);
    }

    @Test
    void should_do_event() {
        assertTrue(events.isEmpty(), "There are events");
        model.setRootDirectories(Collections.emptyList());
        assertEquals(1, events.size(), "Number of events");
        events.clear();
        model.removeTreeModelListener(listener);
        model.setRootDirectories(Collections.emptyList());
        assertTrue(events.isEmpty(), "There are events");
    }

    @Test
    void should_return_root() {
        RootTreeNode root = model.getRoot();
        assertInstanceOf(RootTreeNode.class, root, "Root");
        assertEquals(ROOT_NAME, root.toString(), "To string");
        assertEquals(ROOT_NAME, root.getName(), "Name");
        assertTrue(root.getChildren().isEmpty(), "Root has children");
    }

    @Test
    void should_set_rootDirectories() {
        List<FeDirectory> expectedRootDirectories = Collections.singletonList(new FeDirectory("/", "/"));
        model.setRootDirectories(expectedRootDirectories);
        List<FeDirectory> rootDirectories = model.getRoot().getChildren().stream()
                .map(FeDirectoryTreeNode::getDirectory)
                .collect(Collectors.toList());
        assertIterableEquals(expectedRootDirectories, rootDirectories, "Root directories");
        assertEquals(1, events.size(), "Number of events");
    }

    @Test
    void should_set_subdirectories_by_node() {
        model.setRootDirectories(Collections.singletonList(new FeDirectory("/", "/")));
        RootDirectoryTreeNode parent = model.getRoot().getChildAt(0);
        assertEquals(0, parent.getChildCount(), "Number of parent children");
        events.clear();
        model.setSubdirectories(parent, Collections.singletonList(new FeDirectory("/pub", "pub")));
        assertEquals(1, parent.getChildCount(), "Number of parent children");
        assertEquals(1, events.size(), "Number of events");
    }

    @Test
    void should_set_subdirectories_by_loadedDirectory() {
        model.setRootDirectories(Collections.singletonList(new FeDirectory("/", "/")));
        RootDirectoryTreeNode rootDirectoryNode = model.getRoot().getChildAt(0);
        rootDirectoryNode.setLoaded(true);
        FeDirectory directory = new FeDirectory("/pub", "pub");
        model.setSubdirectories(rootDirectoryNode, Collections.singletonList(directory));
        FeDirectoryTreeNode subdirectoryNode = rootDirectoryNode.getChildAt(0);
        subdirectoryNode.setLoaded(true);
        events.clear();
        model.setSubdirectories(directory, Collections.singletonList(new FeDirectory("/pub/folder", "folder")));
        assertEquals(1, subdirectoryNode.getChildCount(), "Number of subdirectory children");
        assertEquals(1, events.size(), "Number of events");
    }

    @Test
    void should_set_subdirectories_by_unloadedDirectory() {
        model.setRootDirectories(Collections.singletonList(new FeDirectory("/", "/")));
        RootDirectoryTreeNode rootDirectoryNode = model.getRoot().getChildAt(0);
        rootDirectoryNode.setLoaded(true);
        FeDirectory directory = new FeDirectory("/pub", "pub");
        model.setSubdirectories(rootDirectoryNode, Collections.singletonList(directory));
        FeDirectoryTreeNode subdirectoryNode = rootDirectoryNode.getChildAt(0);
        subdirectoryNode.setLoaded(false);
        events.clear();
        model.setSubdirectories(directory, Collections.singletonList(new FeDirectory("/pub/folder", "folder")));
        assertEquals(0, subdirectoryNode.getChildCount(), "Number of subdirectory children");
        assertEquals(0, events.size(), "Number of events");
    }

}










