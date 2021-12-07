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

package dev.alexengrig.myfe.util.swing;

import java.util.EventListener;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Group of event listeners (Swing).
 *
 * @param <L> the type of listener
 * @param <E> the type of event
 */
public class SwingEventListenerGroup<L extends EventListener, E extends EventObject> {

    private final List<L> listeners;

    public SwingEventListenerGroup() {
        this(new LinkedList<>());
    }

    public SwingEventListenerGroup(List<L> listeners) {
        this.listeners = listeners;
    }

    public void add(L listener) {
        listeners.add(listener);
    }

    public void remove(L listener) {
        listeners.remove(listener);
    }

    public void fire(Function<? super L, Consumer<? super E>> methodFactory, E event) {
        listeners.forEach(listener -> {
            Consumer<? super E> listenerMethod = methodFactory.apply(listener);
            listenerMethod.accept(event);
        });
    }

}
