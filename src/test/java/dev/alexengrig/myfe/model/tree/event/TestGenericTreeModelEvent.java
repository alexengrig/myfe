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
import dev.alexengrig.myfe.model.tree.TestModel;

public class TestGenericTreeModelEvent
        extends BaseGenericTreeModelEvent<TestModel, TestGenericTreeNode, TestGenericTreePath, TestGenericTreeModel> {

    public TestGenericTreeModelEvent(TestGenericTreeModel source, TestGenericTreePath path) {
        super(source, path);
    }

    public TestGenericTreeModelEvent(
            TestGenericTreeModel source, TestGenericTreePath path, int childIndex, TestGenericTreeNode child) {
        super(source, path, new int[]{childIndex}, new TestGenericTreeNode[]{child});
    }

    @Override
    public TestGenericTreePath path() {
        return super.path();
    }

}
