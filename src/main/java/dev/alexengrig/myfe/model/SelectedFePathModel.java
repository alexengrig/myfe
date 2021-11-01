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

import dev.alexengrig.myfe.model.event.SelectedFePathModelEvent;
import dev.alexengrig.myfe.model.event.SelectedFePathModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Model of selected {@link FePath}.
 */
public class SelectedFePathModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<SelectedFePathModelListener> listeners = new LinkedList<>();

    private FePath path;

    public boolean isEmpty() {
        return path == null;
    }

    public FePath getPath() {
        return path;
    }

    public void setPath(FePath path) {
        FePath oldPath = this.path;
        this.path = path;
        if (!Objects.equals(oldPath, path)) {
            fireChangePath(new SelectedFePathModelEvent(path));
        }
    }

    public void addFePathModelListener(SelectedFePathModelListener listener) {
        listeners.add(listener);
    }

    private void fireChangePath(SelectedFePathModelEvent event) {
        LOGGER.debug("Fire change path: {}", event);
        for (SelectedFePathModelListener listener : listeners) {
            listener.changePath(event);
        }
    }

    @Override
    public String toString() {
        return "SelectedFePathModel{" +
                "path=" + path +
                '}';
    }

}
