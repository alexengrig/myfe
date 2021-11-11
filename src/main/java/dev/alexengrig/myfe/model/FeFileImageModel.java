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

import dev.alexengrig.myfe.model.event.FeFileImageModelEvent;
import dev.alexengrig.myfe.model.event.FeFileImageModelListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class FeFileImageModel {

    private final List<FeFileImageModelListener> listeners = new LinkedList<>();

    private FeFile file;

    public void setFile(FeFile file) {
        if (!Objects.equals(this.file, file)) {
            this.file = file;
            fireChangeFile(new FeFileImageModelEvent(file));
        }
    }

    public void addFeFileImageModelListener(FeFileImageModelListener listener) {
        listeners.add(listener);
    }

    public void removeFeFileImageModelListener(FeFileImageModelListener listener) {
        listeners.remove(listener);
    }

    private void fireChangeFile(FeFileImageModelEvent event) {
        for (FeFileImageModelListener listener : listeners) {
            listener.changeFile(event);
        }
    }

}
