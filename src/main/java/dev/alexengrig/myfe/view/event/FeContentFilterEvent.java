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

import dev.alexengrig.myfe.util.event.Event;

/**
 * Event of {@link FeContentFilterListener}.
 */
public class FeContentFilterEvent implements Event {

    private final Type eventType;
    private final String type;

    private FeContentFilterEvent(Type eventType, String type) {
        this.eventType = eventType;
        this.type = type;
    }

    public static FeContentFilterEvent type(String type) {
        return new FeContentFilterEvent(Type.CHANGE_TYPE, type);
    }

    protected Type getEventType() {
        return eventType;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "FeContentFilterEvent{" +
                "eventType=" + eventType +
                ", type='" + type + '\'' +
                '}';
    }

    protected enum Type {
        CHANGE_TYPE
    }

}
