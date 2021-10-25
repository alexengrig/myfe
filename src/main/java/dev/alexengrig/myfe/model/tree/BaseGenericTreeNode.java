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

import dev.alexengrig.myfe.model.Model;

import java.util.Objects;

/**
 * Base implementation {@link GenericTreeNode}.
 */
@Deprecated
public abstract class BaseGenericTreeNode<
        M extends Model,
        SELF extends GenericTreeNode<M, SELF>>
        implements GenericTreeNode<M, SELF> {

    protected boolean isChild(SELF child) {
        return this == Objects.requireNonNull(child, "The child must not be null").parent();
    }

    @Override
    public SELF childAt(int index) {
        if (index < 0 || index >= getChildCount()) {
            throw new IndexOutOfBoundsException(index);
        }
        return stream().skip(index).findFirst().orElse(null);
    }

    @Override
    public int index(SELF node) {
        if (!isChild(node)) {
            return -1;
        }
        int index = 0;
        for (SELF child : collection()) {
            if (Objects.equals(node, child)) {
                return index;
            }
            index++;
        }
        throw new IllegalStateException("The node has this node as parent, " +
                                        "but this node doesn't have the node as child");
    }

    @Override
    public int getChildCount() {
        return collection().size();
    }

    @Override
    public String toString() {
        return model().toString();
    }

}
