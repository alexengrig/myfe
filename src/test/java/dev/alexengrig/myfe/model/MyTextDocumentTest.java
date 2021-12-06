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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MyTextDocumentTest {

    final MyTextDocument document = new MyTextDocument();

    @Test
    void should_setAndReturn_text() {
        String expected = "Test text";
        document.setText(expected);
        assertEquals(expected, document.getText(), "Text");
    }

    @Test
    void should_setAndReturn_chars() {
        String expected = "Test text";
        document.setText(expected);
        assertArrayEquals(expected.toCharArray(), document.getChars(), "Chars");
    }

}