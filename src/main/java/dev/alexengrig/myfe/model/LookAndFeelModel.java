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

import com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;
import dev.alexengrig.myfe.model.event.LookAndFeelModelEvent;
import dev.alexengrig.myfe.model.event.LookAndFeelModelListener;
import dev.alexengrig.myfe.util.event.EventListenerGroup;

import javax.swing.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class LookAndFeelModel {

    private static final TreeMap<String, String> LAF_CLASS_NAME_BY_NAME;

    static {
        Map<String, String> laf = Arrays.stream(UIManager.getInstalledLookAndFeels())
                .collect(Collectors.toMap(UIManager.LookAndFeelInfo::getName, UIManager.LookAndFeelInfo::getClassName));
        laf.put(FlatDraculaIJTheme.NAME, FlatDraculaIJTheme.class.getName());
        laf.put(FlatDarkFlatIJTheme.NAME, FlatDarkFlatIJTheme.class.getName());
        laf.put(FlatLightFlatIJTheme.NAME, FlatLightFlatIJTheme.class.getName());
        LAF_CLASS_NAME_BY_NAME = new TreeMap<>(laf);
    }

    private final EventListenerGroup<LookAndFeelModelListener, LookAndFeelModelEvent> listenerGroup = new EventListenerGroup<>();

    private String currentClassName;
    private String currentName;

    public LookAndFeelModel() {
        this(UIManager.getLookAndFeel().getClass().getName());
    }

    public LookAndFeelModel(String className) {
        this.currentClassName = className;
        this.currentName = LAF_CLASS_NAME_BY_NAME.entrySet().stream()
                .filter(e -> e.getValue().equals(className))
                .findAny()
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("No Look and Feel: " + className));
    }

    public SortedSet<String> getAllNames() {
        return LAF_CLASS_NAME_BY_NAME.navigableKeySet();
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
            listenerGroup.fire(LookAndFeelModelEvent.change(currentName, currentClassName));
        }
    }

    public void addLookAndFeelModelListener(LookAndFeelModelListener listener) {
        listenerGroup.add(listener);
    }

    public void removeLookAndFeelModelListener(LookAndFeelModelListener listener) {
        listenerGroup.remove(listener);
    }

}
