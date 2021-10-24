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

import dev.alexengrig.myfe.model.event.MyPathFilterModelEvent;
import dev.alexengrig.myfe.model.event.MyPathFilterModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MyPathFilterModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<MyPathFilterModelListener> listeners = new LinkedList<>();

    private List<String> types;

    public MyPathFilterModel(List<? extends MyPath> paths) {
        this.types = createTypes(paths);
    }

    private List<String> createTypes(List<? extends MyPath> paths) {
        //FIXME: Improve it
        return paths.stream().map(MyPath::getExtension).distinct().sorted().collect(Collectors.toList());
    }

    public void setPaths(List<? extends MyPath> paths) {
        List<String> types = createTypes(paths);
        setTypes(types);
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
        fireChangeTypes(new MyPathFilterModelEvent(types));
    }

    public void addMyPathFilterModelListener(MyPathFilterModelListener listener) {
        listeners.add(listener);
    }

    public void removeMyPathFilterModelListener(MyPathFilterModelListener listener) {
        listeners.remove(listener);
    }

    private void fireChangeTypes(MyPathFilterModelEvent event) {
        LOGGER.debug("Fire change types: {}", event);
        for (MyPathFilterModelListener listener : listeners) {
            listener.changeTypes(event);
        }
    }

}
