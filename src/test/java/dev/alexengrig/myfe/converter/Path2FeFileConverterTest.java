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

package dev.alexengrig.myfe.converter;

import dev.alexengrig.myfe.model.FeFile;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Path2FeFileConverterTest {

    final Path2FeFileConverter converter = new Path2FeFileConverter();

    @Test
    void should_convert() {
        // setup
        Path source = mock(Path.class);
        String path = "/path/to/file.this";
        Path absolutPath = mock(Path.class);
        when(source.toAbsolutePath()).thenReturn(absolutPath);
        when(absolutPath.toString()).thenReturn(path);
        String name = "file.this";
        Path namedPath = mock(Path.class);
        when(source.getNameCount()).thenReturn(3);
        when(source.getFileName()).thenReturn(namedPath);
        when(namedPath.toString()).thenReturn(name);
        // run
        FeFile result = converter.convert(source);
        // check
        assertNotNull(result, "Result");
        assertEquals(path, result.getPath(), "Path");
        assertEquals(name, result.getName(), "Name");
    }

}