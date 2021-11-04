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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class Path2FePathConverterTest {

    Converter<Path, FeDirectory> directoryConverter;
    Converter<Path, FeFile> fileConverter;
    Path2FePathConverter converter;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void beforeEach() {
        directoryConverter = mock(Converter.class);
        fileConverter = mock(Converter.class);
        converter = new Path2FePathConverter(directoryConverter, fileConverter);
    }

    @Test
    void should_convert_directory() throws IOException {
        // setup
        Path source = mock(Path.class);
        FileSystem fs = mock(FileSystem.class);
        when(source.getFileSystem()).thenReturn(fs);
        FileSystemProvider provider = mock(FileSystemProvider.class);
        when(fs.provider()).thenReturn(provider);
        BasicFileAttributes attributes = mock(BasicFileAttributes.class);
        when(attributes.isDirectory()).thenReturn(true);
        when(provider.readAttributes(same(source), eq(BasicFileAttributes.class), any())).thenReturn(attributes);
        FeDirectory directory = mock(FeDirectory.class);
        when(directoryConverter.convert(same(source))).thenReturn(directory);
        // run
        FePath result = converter.convert(source);
        // check
        assertNotNull(result, "Result");
        assertSame(directory, result, "Result");
        verify(directoryConverter).convert(same(source));
        verify(fileConverter, never()).convert(same(source));
    }

    @Test
    void should_convert_file() throws IOException {
        // setup
        Path source = mock(Path.class);
        FileSystem fs = mock(FileSystem.class);
        when(source.getFileSystem()).thenReturn(fs);
        FileSystemProvider provider = mock(FileSystemProvider.class);
        when(fs.provider()).thenReturn(provider);
        BasicFileAttributes attributes = mock(BasicFileAttributes.class);
        when(attributes.isDirectory()).thenReturn(false);
        when(provider.readAttributes(same(source), eq(BasicFileAttributes.class), any())).thenReturn(attributes);
        FeFile file = mock(FeFile.class);
        when(fileConverter.convert(same(source))).thenReturn(file);
        // run
        FePath result = converter.convert(source);
        // check
        assertNotNull(result, "Result");
        assertSame(file, result, "Result");
        verify(directoryConverter, never()).convert(same(source));
        verify(fileConverter).convert(same(source));
    }

}