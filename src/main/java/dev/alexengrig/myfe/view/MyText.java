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
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import java.awt.*;

public class MyText extends JTextArea {

    public MyText(Document document) {
        super(document);
        init();
    }

    private void init() {
        setEnabled(false);
        setDisabledTextColor(Color.BLACK);
        // Disable auto-scroll down after inserting text
        ((DefaultCaret) getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    }

}
