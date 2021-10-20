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

import javax.swing.*;

public class MyTab {

    private final String title;
    private final String tip;
    private final Icon icon;
    private final MyTabComponent component;

    public MyTab(String title, String tip, MyTabComponent component) {
        this(title, tip, null, component);
    }

    public MyTab(String title, String tip, Icon icon, MyTabComponent component) {
        this.title = title;
        this.tip = tip;
        this.icon = icon;
        this.component = component;
    }

    public String title() {
        return title;
    }

    public String tip() {
        return tip;
    }

    public Icon icon() {
        return icon;
    }

    public MyTabComponent component() {
        return component;
    }

}
