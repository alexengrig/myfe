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

package dev.alexengrig.myfe.view.event;

import dev.alexengrig.myfe.model.MyPath;

public class MyPathTableEvent {

    private final MyPath path;
    private final Integer rowCount;

    public MyPathTableEvent(MyPath path, Integer rowCount) {
        this.path = path;
        this.rowCount = rowCount;
    }

    public MyPathTableEvent() {
        this(null, null);
    }

    public MyPathTableEvent(MyPath path) {
        this(path, null);
    }

    public MyPathTableEvent(Integer rowCount) {
        this(null, rowCount);
    }

    public MyPath getPath() {
        return path;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    @Override
    public String toString() {
        return "MyPathTableEvent{" +
                "path=" + path +
                ", rowCount=" + rowCount +
                '}';
    }

}
