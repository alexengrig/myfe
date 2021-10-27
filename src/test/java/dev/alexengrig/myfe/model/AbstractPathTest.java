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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractPathTest {

    AbstractPath createInstance(String path, String name) {
        return new AbstractPath(path, name) {

            @Override
            public boolean isDirectory() {
                throw new UnsupportedOperationException();
            }

        };
    }

    @Test
    void should_throw_NPE_in_constructor() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                createInstance(null, null));
        assertEquals("The path must not be null", exception.getMessage());
        exception = assertThrows(NullPointerException.class, () ->
                createInstance("non-null", null));
        assertEquals("The name must not be null", exception.getMessage());
    }

    @Test
    void should_return_values() {
        String path = "path";
        String name = "name";
        AbstractPath instance = createInstance(path, name);
        assertEquals(path, instance.getPath(), "Path");
        assertEquals(name, instance.getName(), "Name");
    }

    @Test
    void should_return_toString() {
        String path = "path";
        String name = "name";
        AbstractPath instance = createInstance(path, name);
        assertEquals(path, instance.toString(), "To string");
    }

}