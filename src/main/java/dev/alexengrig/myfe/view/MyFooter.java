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

import dev.alexengrig.myfe.model.MyFooterModel;

import javax.swing.*;
import java.awt.*;

public class MyFooter extends JPanel {

    private final MyFooterModel model;

    public MyFooter(MyFooterModel model) {
        super(new BorderLayout());
        this.model = model;
        init();
    }

    private void init() {
        add(new JLabel("Number of elements: 123"), BorderLayout.WEST);
    }

}
