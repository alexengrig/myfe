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

import dev.alexengrig.myfe.domain.FeFile;
import dev.alexengrig.myfe.util.event.Event;

import java.util.Arrays;

/**
 * Event of {@link FeFileImageModelListener}.
 */
public class FeFileImageModelEvent implements Event {

    private final Type type;
    private final FeFile file;
    private final byte[] data;

    private FeFileImageModelEvent(Type type, FeFile file, byte[] data) {
        this.type = type;
        this.file = file;
        this.data = data;
    }

    public static FeFileImageModelEvent changeFile(FeFile file, byte[] data) {
        return new FeFileImageModelEvent(Type.CHANGE_FILE, file, data);
    }

    protected Type getType() {
        return type;
    }

    public FeFile getFile() {
        return file;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "FeFileImageModelEvent{" +
                "type=" + type +
                ", file=" + file +
                ", data=" + Arrays.toString(data) +
                '}';
    }

    protected enum Type {
        CHANGE_FILE
    }

}
