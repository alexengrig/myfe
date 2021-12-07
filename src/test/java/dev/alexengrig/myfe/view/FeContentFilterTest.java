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

package dev.alexengrig.myfe.view;

import dev.alexengrig.myfe.model.FeContentFilterModel;
import dev.alexengrig.myfe.model.MyComboBoxModel;
import dev.alexengrig.myfe.view.event.FeContentFilterEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeContentFilterTest {

    FeContentFilterModel model;

    MyComboBoxModel<String> typeModel;
    List<String> types;

    FeContentFilter view;
    LinkedList<FeContentFilterEvent> events;


    @BeforeEach
    void beforeEach() {
        model = new FeContentFilterModel();
        typeModel = new MyComboBoxModel<>("default", () -> types);
        types = new LinkedList<>();
        view = new FeContentFilter(model, typeModel);
        events = new LinkedList<>();
        view.addFeContentFilterListener(events::add);
    }

    @Test
    void should_change_type() {
        String selectedType = "selected type";
        typeModel.setSelectedItem(selectedType);
        assertEquals(1, events.size(), "Number of model events");
        FeContentFilterEvent event = events.get(0);
        assertEquals(selectedType, event.getType(), "Selected type");
    }

}