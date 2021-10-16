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

package dev.alexengrig.myfe.model.tree;

import dev.alexengrig.myfe.model.tree.event.TestGenericTreeModelEvent;
import org.junit.jupiter.api.Test;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class BaseGenericTreeModelTest {

    @Test
    void testChild() {
        // Setup
        TestGenericTreeNode root = new TestGenericTreeNode(new TestModel("1"));
        TestGenericTreeNode node = new TestGenericTreeNode(root, new TestModel("2"));
        new TestGenericTreeNode(node, new TestModel("3"));
        TestGenericTreeNode subNode = new TestGenericTreeNode(node, new TestModel("4"));
        TestGenericTreeModel treeModel = new TestGenericTreeModel(root);
        // Run
        TestGenericTreeNode actual = treeModel.child(node, 1);
        // Check
        assertSame(subNode, actual, "Child at 1");
    }

    @Test
    void testChildCount() {
        // Setup
        TestGenericTreeNode root = new TestGenericTreeNode(new TestModel("1"));
        TestGenericTreeNode node = new TestGenericTreeNode(root, new TestModel("2"));
        new TestGenericTreeNode(node, new TestModel("3"));
        new TestGenericTreeNode(node, new TestModel("4"));
        TestGenericTreeModel treeModel = new TestGenericTreeModel(root);
        // Run
        int actual = treeModel.childCount(node);
        // Check
        assertEquals(2, actual, "Child count");
    }

    @Test
    void testIsLeaf() {
        // Setup
        TestGenericTreeNode root = new TestGenericTreeNode(new TestModel("1"));
        TestGenericTreeNode node = new TestGenericTreeNode(root, new TestModel("2"));
        TestGenericTreeModel treeModel = new TestGenericTreeModel(root);
        // Run
        boolean rootIsLeaf = treeModel.isLeaf(root);
        boolean nodeIsLeaf = treeModel.isLeaf(node);
        // Check
        assertFalse(rootIsLeaf, "Root is leaf");
        assertTrue(nodeIsLeaf, "Node isn't leaf");
    }

    @Test
    void testIndexOfChild() {
        // Setup
        TestGenericTreeNode root = new TestGenericTreeNode(new TestModel("1"));
        TestGenericTreeNode node = new TestGenericTreeNode(root, new TestModel("2"));
        new TestGenericTreeNode(node, new TestModel("3"));
        TestGenericTreeNode subNode = new TestGenericTreeNode(node, new TestModel("4"));
        TestGenericTreeModel treeModel = new TestGenericTreeModel(root);
        // Run
        int actual = treeModel.indexOfChild(node, subNode);
        // Check
        assertEquals(1, actual, "Child index");
    }

    @Test
    void testChange() {
        // Setup
        TestGenericTreeNode root = new TestGenericTreeNode(new TestModel("1"));
        TestGenericTreeNode node = new TestGenericTreeNode(root, new TestModel("2"));
        TestGenericTreeModel treeModel = new TestGenericTreeModel(root);
        TestGenericTreePath path = new TestGenericTreePath(node);
        AtomicReference<TreeModelEvent> eventHolder = new AtomicReference<>();
        treeModel.addTreeModelListener(new TestFailTreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                eventHolder.set(e);
            }
        });
        TestModel expected = new TestModel("20");
        // Run
        treeModel.change(path, expected);
        // Check
        assertSame(expected, node.model(), "Node model");
        TreeModelEvent event = eventHolder.get();
        assertNotNull(event, "Event");
        assertTrue(event instanceof TestGenericTreeModelEvent, "Instance of TestGenericTreeModelEvent");
        TestGenericTreeModelEvent modelEvent = (TestGenericTreeModelEvent) event;
        assertSame(path, modelEvent.path(), "Path");
    }

    @Test
    void testChangeWithoutFire() {
        // Setup
        TestGenericTreeNode root = new TestGenericTreeNode(new TestModel("1"));
        TestGenericTreeNode node = new TestGenericTreeNode(root, new TestModel("2"));
        TestGenericTreeModel treeModel = new TestGenericTreeModel(root);
        TestGenericTreePath path = new TestGenericTreePath(node);
        AtomicReference<TreeModelEvent> eventHolder = new AtomicReference<>();
        treeModel.addTreeModelListener(new TestFailTreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                eventHolder.set(e);
            }
        });
        // Run
        treeModel.change(path, node.model());
        // Check
        TreeModelEvent event = eventHolder.get();
        assertNull(event, "Event");
    }

    @Test
    void testListening() {
        // Setup
        TestModel model = new TestModel("Data");
        TestGenericTreeNode root = new TestGenericTreeNode(model);
        TestGenericTreeModel treeModel = new TestGenericTreeModel(root);
        List<TreeModelEvent> events = new ArrayList<>(4);
        TreeModelListener listener = new TreeModelListener() {
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                events.add(e);
            }

            @Override
            public void treeNodesInserted(TreeModelEvent e) {
                events.add(e);
            }

            @Override
            public void treeNodesRemoved(TreeModelEvent e) {
                events.add(e);
            }

            @Override
            public void treeStructureChanged(TreeModelEvent e) {
                events.add(e);
            }
        };
        treeModel.addTreeModelListener(listener);
        TreeModelEvent event = mock(TreeModelEvent.class);
        // Run
        treeModel.fireTreeStructureChanged(event);
        treeModel.fireTreeNodesInserted(event);
        treeModel.fireTreeNodesChanged(event);
        treeModel.fireTreeNodesRemoved(event);
        // Check
        assertEquals(4, events.size(), "Event count");
        // Setup
        events.clear();
        treeModel.removeTreeModelListener(listener);
        // Run
        treeModel.fireTreeStructureChanged(event);
        treeModel.fireTreeNodesInserted(event);
        treeModel.fireTreeNodesChanged(event);
        treeModel.fireTreeNodesRemoved(event);
        // Check
        assertEquals(0, events.size(), "Event count");
    }

}