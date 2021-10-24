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

public class MyPathFilterEvent {

    private final String type;

    private MyPathFilterEvent(String type) {
        this.type = type;
    }

    public static MyPathFilterEvent type(String type) {
        return new MyPathFilterEvent(type);
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "MyPathFilterEvent{" +
                "type='" + type + '\'' +
                '}';
    }

}
