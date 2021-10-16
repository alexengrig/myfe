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

package dev.alexengrig.myfe.model.tree.event;

import dev.alexengrig.myfe.model.tree.TestGenericTreeModel;
import dev.alexengrig.myfe.model.tree.TestGenericTreeNode;
import dev.alexengrig.myfe.model.tree.TestGenericTreePath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

class BaseGenericTreeModelEventTest {

    @Test
    void testPath() {
        // Setup
        TestGenericTreeModel model = mock(TestGenericTreeModel.class);
        TestGenericTreePath path = mock(TestGenericTreePath.class);
        TestGenericTreeModelEvent event = new TestGenericTreeModelEvent(model, path);
        // Run
        TestGenericTreePath actual = event.path();
        // Check
        assertSame(path, actual, "Path");
    }

    @Test
    void testAllNodes() {
        // Setup
        TestGenericTreeModel model = mock(TestGenericTreeModel.class);
        TestGenericTreeNode node = mock(TestGenericTreeNode.class);
        TestGenericTreePath path = new TestGenericTreePath(node);
        TestGenericTreeModelEvent event = new TestGenericTreeModelEvent(model, path);
        // Run
        List<TestGenericTreeNode> nodes = event.allNodes();
        // Check
        assertEquals(1, nodes.size(), "Node count");
        assertSame(node, nodes.get(0), "Node");
    }

    @Test
    void testChildren() {
        // Setup
        TestGenericTreeModel model = mock(TestGenericTreeModel.class);
        TestGenericTreePath path = mock(TestGenericTreePath.class);
        TestGenericTreeNode node = mock(TestGenericTreeNode.class);
        TestGenericTreeModelEvent event = new TestGenericTreeModelEvent(model, path, 0, node);
        // Run
        List<TestGenericTreeNode> children = event.children();
        // Check
        assertEquals(1, children.size(), "Child count");
        assertSame(node, children.get(0), "Child");
    }

}