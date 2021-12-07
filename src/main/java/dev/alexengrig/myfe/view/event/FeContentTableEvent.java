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

package dev.alexengrig.myfe.view.event;

import dev.alexengrig.myfe.domain.FePath;
import dev.alexengrig.myfe.util.event.Event;

/**
 * Event of {@link FeContentTableListener}.
 */
public class FeContentTableEvent implements Event {

    private final Type type;
    private final FePath path;
    private final Integer rowCount;

    private FeContentTableEvent(Type type, FePath path, Integer rowCount) {
        this.type = type;
        this.path = path;
        this.rowCount = rowCount;
    }

    public static FeContentTableEvent selectPath(FePath path) {
        return new FeContentTableEvent(Type.SELECT_PATH, path, null);
    }

    public static FeContentTableEvent goToPath(FePath path) {
        return new FeContentTableEvent(Type.GO_TO_PATH, path, null);
    }

    public static FeContentTableEvent changeRowCount(Integer rowCount) {
        return new FeContentTableEvent(Type.CHANGE_ROW_COUNT, null, rowCount);
    }

    protected Type getType() {
        return type;
    }

    public FePath getPath() {
        return path;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    @Override
    public String toString() {
        return "FeContentTableEvent{" +
                "type=" + type +
                ", path=" + path +
                ", rowCount=" + rowCount +
                '}';
    }

    protected enum Type {
        SELECT_PATH,
        GO_TO_PATH,
        CHANGE_ROW_COUNT
    }

}
