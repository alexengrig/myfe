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

import dev.alexengrig.myfe.domain.FeFile;

public class FeTabEvent {

    private final FeFile file;

    public FeTabEvent(FeFile file) {
        this.file = file;
    }

    public FeFile getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "MyTabComponentEvent{" +
                "file=" + file +
                '}';
    }

}
