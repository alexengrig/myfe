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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseGenericTreeNodeTest {

    @Test
    void testIsChild() {
        // Setup
        TestGenericTreeNode parent = new TestGenericTreeNode(new TestModel("1"));
        TestGenericTreeNode node = new TestGenericTreeNode(parent, new TestModel("2"));
        // Run
        boolean isChild = parent.isChild(node);
        // Check
        assertTrue(isChild, "Is child");
    }

    @Test
    void testChildAt() {
        // Setup
        TestGenericTreeNode parent = new TestGenericTreeNode(new TestModel("1"));
        new TestGenericTreeNode(parent, new TestModel("2"));
        TestGenericTreeNode secondNode = new TestGenericTreeNode(parent, new TestModel("3"));
        // Run
        TestGenericTreeNode actual = parent.childAt(1);
        // Check
        assertSame(secondNode, actual, "Child at 1");
    }

    @Test
    void testChildAtWithIndexOutOfBounds() {
        // Setup
        TestGenericTreeNode parent = new TestGenericTreeNode(new TestModel("1"));
        // Run
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () ->
                parent.childAt(100));
        // Check
        assertEquals("Index out of range: 100", exception.getMessage(), "Exception message");
    }

    @Test
    void testIndex() {
        // Setup
        TestGenericTreeNode parent = new TestGenericTreeNode(new TestModel("1"));
        new TestGenericTreeNode(parent, new TestModel("2"));
        TestGenericTreeNode secondNode = new TestGenericTreeNode(parent, new TestModel("3"));
        // Run
        int actual = parent.index(secondNode);
        // Check
        assertEquals(1, actual, "Index");
    }

    @Test
    void testIndexForAnotherNode() {
        // Setup
        TestGenericTreeNode parent = new TestGenericTreeNode(new TestModel("1"));
        TestGenericTreeNode another = new TestGenericTreeNode(new TestModel("2"));
        // Run
        int actual = parent.index(another);
        // Check
        assertEquals(-1, actual, "Index");
    }

    @Test
    void testGetChildCount() {
        // Setup
        TestGenericTreeNode parent = new TestGenericTreeNode(new TestModel("1"));
        new TestGenericTreeNode(parent, new TestModel("2"));
        new TestGenericTreeNode(parent, new TestModel("3"));
        // Run
        int actual = parent.getChildCount();
        // Check
        assertEquals(2, actual, "Child count");
    }

    @Test
    void testToString() {
        // Setup
        TestGenericTreeNode parent = new TestGenericTreeNode(new TestModel("Data"));
        // Run
        String actual = parent.toString();
        // Check
        assertEquals("Data", actual, "To string");
    }

}