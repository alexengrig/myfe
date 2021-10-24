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

import dev.alexengrig.myfe.model.event.MyFooterModelEvent;
import dev.alexengrig.myfe.model.event.MyFooterModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MyFooterModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<MyFooterModelListener> listeners = new LinkedList<>();

    private Integer numberOfElements;

    public MyFooterModel(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Integer numberOfElements) {
        if (!Objects.equals(this.numberOfElements, numberOfElements)) {
            this.numberOfElements = numberOfElements;
            fireChangeNumberOfElements(new MyFooterModelEvent(numberOfElements));
        }
    }

    public void addMyFooterModelListener(MyFooterModelListener listener) {
        listeners.add(listener);
    }

    public void removeMyFooterModelListener(MyFooterModelListener listener) {
        listeners.remove(listener);
    }

    private void fireChangeNumberOfElements(MyFooterModelEvent event) {
        LOGGER.debug("Fire change number of elements: {}", event);
        for (MyFooterModelListener listener : listeners) {
            listener.changeNumberOfElements(event);
        }
    }

}
