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

import dev.alexengrig.myfe.model.event.FePathModelEvent;
import dev.alexengrig.myfe.model.event.MyPathModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

public class MyPathModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<MyPathModelListener> listeners = new LinkedList<>();

    private FePath path;

    public boolean isEmpty() {
        return path == null;
    }

    public FePath getPath() {
        return path;
    }

    public void setPath(FePath path) {
        this.path = path;
        fireChangePath(new FePathModelEvent(path));
    }

    public void addMyPathModelListener(MyPathModelListener listener) {
        listeners.add(listener);
    }

    private void fireChangePath(FePathModelEvent event) {
        LOGGER.debug("Fire change path: {}", event);
        for (MyPathModelListener listener : listeners) {
            listener.changePath(event);
        }
    }

}
