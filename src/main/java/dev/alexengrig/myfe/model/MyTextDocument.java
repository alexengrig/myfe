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

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;
import java.util.Arrays;

/**
 * Document of text.
 */
public class MyTextDocument extends PlainDocument {

    public String getText() {
        try {
            return getText(0, getLength());
        } catch (BadLocationException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setText(String text) {
        try {
            replace(0, getLength(), text, null);
        } catch (BadLocationException e) {
            throw new IllegalStateException(e);
        }
    }

    public char[] getChars() {
        try {
            Segment segment = new Segment();
            int length = getLength();
            getText(0, length, segment);
            return Arrays.copyOf(segment.array, length);
        } catch (BadLocationException e) {
            throw new IllegalStateException(e);
        }
    }

}
