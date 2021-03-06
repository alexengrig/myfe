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

package dev.alexengrig.myfe.model.event;

import dev.alexengrig.myfe.domain.FePath;
import dev.alexengrig.myfe.util.event.Event;

/**
 * Event of {@link FeSelectedPathModelListener}.
 */
public class FeSelectedPathModelEvent implements Event {

    private final Type type;
    private final FePath path;

    private FeSelectedPathModelEvent(Type type, FePath path) {
        this.type = type;
        this.path = path;
    }

    public static FeSelectedPathModelEvent changePath(FePath path) {
        return new FeSelectedPathModelEvent(Type.CHANGE_PATH, path);
    }

    protected Type getType() {
        return type;
    }

    public FePath getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "FeSelectedPathModelEvent{" +
                "type=" + type +
                ", path=" + path +
                '}';
    }

    protected enum Type {
        CHANGE_PATH
    }

}
