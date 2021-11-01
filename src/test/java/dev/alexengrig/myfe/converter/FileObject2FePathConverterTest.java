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

import dev.alexengrig.myfe.model.FeDirectory;
import dev.alexengrig.myfe.model.FeFile;
import dev.alexengrig.myfe.model.FePath;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileObject2FePathConverterTest {

    @Mock
    FileObject2MyDirectoryConverter directoryConverter;
    @Mock
    FileObject2MyFileConverter fileConverter;

    @InjectMocks
    FileObject2MyPathConverter converter;

    @Test
    void should_convert_directory() throws FileSystemException {
        // setup
        FileObject source = mock(FileObject.class);
        when(source.isFolder()).thenReturn(true);
        FeDirectory directory = mock(FeDirectory.class);
        when(directoryConverter.convert(same(source))).thenReturn(directory);
        // run
        FePath result = converter.convert(source);
        // check
        assertSame(directory, result, "Result");
        verify(directoryConverter).convert(same(source));
        verify(fileConverter, never()).convert(same(source));
    }

    @Test
    void should_convert_file() throws FileSystemException {
        // setup
        FileObject source = mock(FileObject.class);
        when(source.isFolder()).thenReturn(false);
        FeFile file = mock(FeFile.class);
        when(fileConverter.convert(same(source))).thenReturn(file);
        // run
        FePath result = converter.convert(source);
        // check
        assertSame(file, result, "Result");
        verify(directoryConverter, never()).convert(same(source));
        verify(fileConverter).convert(same(source));
    }

}