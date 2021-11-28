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

/**
 * Event of {@link LookAndFeelModelListener}.
 */
public class LookAndFeelModelEvent implements Event {

    private final Type type;
    private final String name;
    private final String className;

    private LookAndFeelModelEvent(Type type, String name, String className) {
        this.type = type;
        this.name = name;
        this.className = className;
    }

    public static LookAndFeelModelEvent change(String name, String className) {
        return new LookAndFeelModelEvent(Type.CHANGE, name, className);
    }

    protected Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return "LookAndFeelModelEvent{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", className='" + className + '\'' +
                '}';
    }

    protected enum Type {
        CHANGE
    }

}
