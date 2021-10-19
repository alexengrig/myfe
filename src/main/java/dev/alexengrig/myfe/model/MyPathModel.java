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

import java.util.LinkedList;
import java.util.List;

public class MyPathModel {

    private final List<MyPathModelListener> listeners = new LinkedList<>();

    private MyPath path;

    public boolean isEmpty() {
        return path == null;
    }

    public MyPath getPath() {
        return path;
    }

    public void setPath(MyPath path) {
        this.path = path;
        fireChangePath(new MyPathModelEvent(path));
    }

    public void addMyPathModelListener(MyPathModelListener listener) {
        listeners.add(listener);
    }

    private void fireChangePath(MyPathModelEvent event) {
        for (MyPathModelListener listener : listeners) {
            listener.changePath(event);
        }
    }

}
