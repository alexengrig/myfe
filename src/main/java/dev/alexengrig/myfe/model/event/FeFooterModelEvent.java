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

import java.util.List;

/**
 * Event of {@link FeFooterModelListener}.
 */
public class FeFooterModelEvent {

    private final Integer numberOfElements;
    private final List<String> tasks;

    public FeFooterModelEvent(Integer numberOfElements, List<String> tasks) {
        this.numberOfElements = numberOfElements;
        this.tasks = tasks;
    }

    public static FeFooterModelEvent numberOfElements(int numberOfElements) {
        return new FeFooterModelEvent(numberOfElements, null);
    }

    public static FeFooterModelEvent tasks(List<String> tasks) {
        return new FeFooterModelEvent(null, tasks);
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
                "numberOfElements=" + numberOfElements +
                ", tasks=" + tasks +
                '}';
    }

}
