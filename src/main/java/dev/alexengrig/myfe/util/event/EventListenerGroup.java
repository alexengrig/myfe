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

package dev.alexengrig.myfe.util.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

/**
 * Group of event listeners.
 *
 * @param <L> the type of listener
 * @param <E> the type of event
 */
public final class EventListenerGroup<L extends EventListener<E>, E extends Event> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<L> listeners;

    public EventListenerGroup() {
        this(new LinkedList<>());
    }

    public EventListenerGroup(List<L> listeners) {
        this.listeners = listeners;
    }

    public void add(L listener) {
        LOGGER.debug("Add listener: {}", listener);
        listeners.add(listener);
    }

    public void remove(L listener) {
        LOGGER.debug("Remove listener: {}", listener);
        listeners.add(listener);
    }

    public void fire(E event) {
        LOGGER.debug("Fire event: {}", event);
        for (L listener : listeners) {
            listener.notify(event);
        }
    }

}
