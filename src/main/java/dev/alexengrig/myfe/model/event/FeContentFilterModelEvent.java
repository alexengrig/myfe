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

import dev.alexengrig.myfe.util.event.Event;

import java.util.List;

/**
 * Event of {@link FeContentFilterModelListener}.
 */
public class FeContentFilterModelEvent implements Event {

    private final Type type;
    private final List<String> types;

    private FeContentFilterModelEvent(Type type, List<String> types) {
        this.type = type;
        this.types = types;
    }

    public static FeContentFilterModelEvent changeTypes(List<String> types) {
        return new FeContentFilterModelEvent(Type.CHANGE_TYPES, types);
    }

    protected Type getType() {
        return type;
    }

    public List<String> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return "FeContentFilterModelEvent{" +
                "type=" + type +
                ", types=" + types +
                '}';
    }

    protected enum Type {
        CHANGE_TYPES
    }

}
