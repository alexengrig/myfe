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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BaseGenericTreePathTest {

    @Test
    void testAllNodes() {
        // Setup
        TestGenericTreeNode root = new TestGenericTreeNode(null, new TestModel("1"));
        TestGenericTreeNode node = new TestGenericTreeNode(root, new TestModel("2"));
        TestGenericTreeNode lastNode = new TestGenericTreeNode(node, new TestModel("3"));
        TestGenericTreePath path = new TestGenericTreePath(lastNode);
        // Run
        List<TestGenericTreeNode> nodes = path.allNodes();
        // Check
        assertEquals(List.of(root, node, lastNode), nodes, "All nodes of path");
    }

    @Test
    void testLastNode() {
        TestGenericTreeNode lastNode = new TestGenericTreeNode(null, new TestModel("1"));
        TestGenericTreePath path = new TestGenericTreePath(lastNode);
        // Run
        TestGenericTreeNode node = path.lastNode();
        // Check
        assertSame(lastNode, node, "Last node");
    }

    @Test
    void testCountNodes() {
        // Setup
        TestGenericTreeNode root = new TestGenericTreeNode(null, new TestModel("1"));
        TestGenericTreeNode node = new TestGenericTreeNode(root, new TestModel("2"));
        TestGenericTreeNode lastNode = new TestGenericTreeNode(node, new TestModel("3"));
        TestGenericTreePath path = new TestGenericTreePath(lastNode);
        // Run
        int countNodes = path.countNodes();
        // Check
        assertEquals(3, countNodes, "Number of nodes");
    }

    @Test
    void testNodeAt() {
        // Setup
        TestGenericTreeNode root = new TestGenericTreeNode(null, new TestModel("1"));
        TestGenericTreeNode node = new TestGenericTreeNode(root, new TestModel("2"));
        TestGenericTreeNode lastNode = new TestGenericTreeNode(node, new TestModel("3"));
        TestGenericTreePath path = new TestGenericTreePath(lastNode);
        // Run
        TestGenericTreeNode nodeAt1 = path.nodeAt(1);
        // Check
        assertEquals(node, nodeAt1, "Node at 1");
    }

    @Test
    void testParent() {
        // Setup
        TestGenericTreeNode root = new TestGenericTreeNode(null, new TestModel("1"));
        TestGenericTreeNode node = new TestGenericTreeNode(root, new TestModel("2"));
        TestGenericTreeNode lastNode = new TestGenericTreeNode(node, new TestModel("3"));
        TestGenericTreePath path = new TestGenericTreePath(lastNode);
        // Run
        TestGenericTreePath parent = path.parent();
        // Check
        assertEquals(new TestGenericTreePath(node), parent, "Parent path");
    }

    @Test
    void testToString() {
        // Setup
        TestGenericTreeNode root = new TestGenericTreeNode(null, new TestModel("1"));
        TestGenericTreeNode node = new TestGenericTreeNode(root, new TestModel("2"));
        TestGenericTreeNode lastNode = new TestGenericTreeNode(node, new TestModel("3"));
        TestGenericTreePath path = new TestGenericTreePath(lastNode);
        // Run
        String string = path.toString();
        // Check
        assertEquals("[1, 2, 3]", string, "String");
    }

}