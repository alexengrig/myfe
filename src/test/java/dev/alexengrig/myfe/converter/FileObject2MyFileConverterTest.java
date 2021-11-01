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
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileObject2MyFileConverterTest {

    final FileObject2MyFileConverter converter = new FileObject2MyFileConverter();

    @Test
    void should_convert() {
        // setup
        FileObject source = mock(FileObject.class);
        FileName fileName = mock(FileName.class);
        String path = "/path/to/file.this";
        when(fileName.getPath()).thenReturn(path);
        String name = "file.this";
        when(fileName.getBaseName()).thenReturn(name);
        when(source.getName()).thenReturn(fileName);
        // run
        FeFile result = converter.convert(source);
        // check
        assertNotNull(result, "Result");
        assertEquals(path, result.getPath(), "Path");
        assertEquals(name, result.getName(), "File");
    }

}