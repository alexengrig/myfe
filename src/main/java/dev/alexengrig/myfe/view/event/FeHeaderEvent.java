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

import dev.alexengrig.myfe.model.FeDirectory;

public class FeHeaderEvent {

    private final FeDirectory directory;

    private FeHeaderEvent(FeDirectory directory) {
        this.directory = directory;
    }

    public static FeHeaderEvent root() {
        return new FeHeaderEvent(null);
    }

    public static FeHeaderEvent directory(FeDirectory directory) {
        return new FeHeaderEvent(directory);
    }

    public static FeHeaderEvent refreshing() {
        return new FeHeaderEvent(null);
    }

    public FeDirectory getDirectory() {
        return directory;
    }

    @Override
    public String toString() {
        return "FeHeaderEvent{" +
                "directory=" + directory +
                '}';
    }

}
