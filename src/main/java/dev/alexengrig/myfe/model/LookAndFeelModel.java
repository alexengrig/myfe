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

import dev.alexengrig.myfe.model.event.LookAndFeelModelEvent;
import dev.alexengrig.myfe.model.event.LookAndFeelModelListener;

import javax.swing.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class LookAndFeelModel {

    private static final Map<String, String> LAF_CLASS_NAME_BY_NAME;

    static {
        LAF_CLASS_NAME_BY_NAME = Arrays.stream(UIManager.getInstalledLookAndFeels())
                .collect(Collectors.toMap(UIManager.LookAndFeelInfo::getName, UIManager.LookAndFeelInfo::getClassName));
    }

    private final List<LookAndFeelModelListener> listeners = new LinkedList<>();

    private String currentClassName;
    private String currentName;

    public LookAndFeelModel() {
        this(UIManager.getSystemLookAndFeelClassName());
    }

    public LookAndFeelModel(String className) {
        this.currentClassName = className;
        this.currentName = LAF_CLASS_NAME_BY_NAME.entrySet().stream()
                .filter(e -> e.getValue().equals(className))
                .findAny()
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("No Look and Feel: " + className));
    }

    public List<String> getAllNames() {
        return LAF_CLASS_NAME_BY_NAME.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public String getCurrentName() {
        return currentName;
    }

    public String getCurrentClassName() {
        return currentClassName;
    }

    public void setByName(String name) {
        if (!Objects.equals(currentName, name)) {
            currentName = name;
            currentClassName = LAF_CLASS_NAME_BY_NAME.get(name);
            fireChange(new LookAndFeelModelEvent(currentName, currentClassName));
        }
    }

    public void addLookAndFeelModelListener(LookAndFeelModelListener listener) {
        listeners.add(listener);
    }

    public void removeLookAndFeelModelListener(LookAndFeelModelListener listener) {
        listeners.remove(listener);
    }

    private void fireChange(LookAndFeelModelEvent event) {
        for (LookAndFeelModelListener listener : listeners) {
            listener.change(event);
        }
    }

}
