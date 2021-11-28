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
 * Event of {@link FeFooterModelListener}.
 */
public class FeFooterModelEvent implements Event {

    private final Type type;
    private final Integer numberOfElements;
    private final List<String> tasks;

    public FeFooterModelEvent(Type type, Integer numberOfElements, List<String> tasks) {
        this.type = type;
        this.numberOfElements = numberOfElements;
        this.tasks = tasks;
    }

    public static FeFooterModelEvent changeNumberOfElements(int numberOfElements) {
        return new FeFooterModelEvent(Type.CHANGE_NUMBER_OF_ELEMENTS, numberOfElements, null);
    }

    public static FeFooterModelEvent changeTasks(List<String> tasks) {
        return new FeFooterModelEvent(Type.CHANGE_TASKS, null, tasks);
    }

    protected Type getType() {
        return type;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public List<String> getTasks() {
        return tasks;
    }

    @Override
    public String toString() {
        return "FeFooterModelEvent{" +
                "type=" + type +
                ", numberOfElements=" + numberOfElements +
                ", tasks=" + tasks +
                '}';
    }

    protected enum Type {
        CHANGE_NUMBER_OF_ELEMENTS,
        CHANGE_TASKS
    }

}
