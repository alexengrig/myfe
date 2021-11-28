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

package dev.alexengrig.myfe.model;

import dev.alexengrig.myfe.domain.FePath;
import dev.alexengrig.myfe.model.event.FeContentFilterModelEvent;
import dev.alexengrig.myfe.model.event.FeContentFilterModelListener;
import dev.alexengrig.myfe.util.FePathUtil;
import dev.alexengrig.myfe.util.event.EventListenerGroup;
import dev.alexengrig.myfe.view.FeContentFilter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Model of file explorer content filter.
 *
 * @see FeContentFilter
 */
public class FeContentFilterModel {

    private final EventListenerGroup<FeContentFilterModelListener, FeContentFilterModelEvent> listenerGroup = new EventListenerGroup<>();

    private List<String> types;

    public FeContentFilterModel(List<? extends FePath> paths) {
        this.types = createTypes(paths);
    }

    private List<String> createTypes(List<? extends FePath> paths) {
        return paths.stream()
                .map(FePathUtil::getType)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public void setPaths(List<? extends FePath> paths) {
        List<String> types = createTypes(paths);
        setTypes(types);
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
        listenerGroup.fire(FeContentFilterModelEvent.changeTypes(types));
    }

    public void addFeContentFilterModelListener(FeContentFilterModelListener listener) {
        listenerGroup.add(listener);
    }

    public void removeFeContentFilterModelListener(FeContentFilterModelListener listener) {
        listenerGroup.remove(listener);
    }

}
