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

package dev.alexengrig.myfe.repository;

import dev.alexengrig.myfe.converter.Path2FeDirectoryConverter;
import dev.alexengrig.myfe.converter.Path2FePathConverter;
import dev.alexengrig.myfe.model.FeDirectory;
import dev.alexengrig.myfe.model.FePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileSystemPathRepositoryTest {

    @Mock
    FileSystemHelper fs;
    @Mock
    Path2FeDirectoryConverter directoryConverter;
    @Mock
    Path2FePathConverter pathConverter;

    FileSystemPathRepository repository;

    @BeforeEach
    void beforeEach() {
        repository = new FileSystemPathRepository(fs, directoryConverter, pathConverter);
    }

    @Test
    void should_close_fileSystem() throws Exception {
        // run
        repository.close();
        // check
        verify(fs).close();
    }

    @Test
    void should_return_rootDirectories() {
        // setup
        Path path = mock(Path.class);
        when(fs.getRootDirectories()).thenReturn(Collections.singleton(path));
        FeDirectory directory = mock(FeDirectory.class);
        when(directoryConverter.convert(same(path))).thenReturn(directory);
        // run
        List<FeDirectory> rootDirectories = repository.getRootDirectories();
        // check
        assertIterableEquals(Collections.singleton(directory), rootDirectories, "Root directories");
    }

    @Test
    void should_return_children() {
        // setup
        String stringPath = "/path/to/test";
        Path directoryPath = mock(Path.class);
        when(fs.getPath(same(stringPath))).thenReturn(directoryPath);
        Path childPath = mock(Path.class);
        when(fs.getChildren(same(directoryPath))).thenReturn(Collections.singletonList(childPath));
        FePath path = mock(FePath.class);
        when(pathConverter.convert(same(childPath))).thenReturn(path);
        // run
        List<FePath> children = repository.getChildren(stringPath);
        // check
        assertIterableEquals(Collections.singleton(path), children);
    }

    @Test
    void should_return_subdirectories() {
        // setup
        String stringPath = "/path/to/test";
        Path directoryPath = mock(Path.class);
        when(fs.getPath(same(stringPath))).thenReturn(directoryPath);
        Path childDirectoryPath = mock(Path.class);
        when(fs.getSubdirectories(same(directoryPath))).thenReturn(Collections.singletonList(childDirectoryPath));
        FeDirectory directory = mock(FeDirectory.class);
        when(directoryConverter.convert(same(childDirectoryPath))).thenReturn(directory);
        // run
        List<FeDirectory> subdirectories = repository.getSubdirectories(stringPath);
        // check
        assertIterableEquals(Collections.singleton(directory), subdirectories);
    }

    @Test
    void should_read_batch() throws IOException {
        // setup
        String stringPath = "/path/to/test";
        Path path = mock(Path.class);
        when(fs.getPath(stringPath)).thenReturn(path);
        SeekableByteChannel channel = mock(SeekableByteChannel.class);
        when(fs.newByteChannel(same(path))).thenReturn(channel);
        when(channel.size()).thenReturn(128L);
        String expectedBatch = "So few";
        when(channel.read(any(ByteBuffer.class))).then(invocation -> {
            ByteBuffer buffer = invocation.getArgument(0, ByteBuffer.class);
            byte[] bytes = expectedBatch.getBytes(StandardCharsets.UTF_8);
            buffer.put(bytes);
            return bytes.length;
        });
        // run
        String batch = repository.readBatch(stringPath, 128);
        // check
        assertEquals(expectedBatch, batch, "Batch");
    }

    @Test
    void should_read_batches() throws IOException {
        // setup
        String stringPath = "/path/to/test";
        Path path = mock(Path.class);
        when(fs.getPath(stringPath)).thenReturn(path);
        SeekableByteChannel channel = mock(SeekableByteChannel.class);
        when(fs.newByteChannel(same(path))).thenReturn(channel);
        when(channel.size()).thenReturn(128L);
        String batch = "on-repeat";
        when(channel.read(any(ByteBuffer.class))).then(invocation -> {
            ByteBuffer buffer = invocation.getArgument(0, ByteBuffer.class);
            byte[] bytes = batch.getBytes(StandardCharsets.UTF_8);
            buffer.put(bytes);
            return bytes.length;
        });
        // run
        Stream<String> stream = repository.readInBatches(stringPath, 128, 2);
        // check
        assertIterableEquals(List.of(batch, batch), stream.collect(Collectors.toList()), "Batches");
    }

    @Test
    void should_read_allBytes() throws IOException {
        // setup
        String stringPath = "";
        Path path = mock(Path.class);
        when(fs.getPath(stringPath)).thenReturn(path);
        byte[] expectedBytes = new byte[]{1, 2, 3};
        when(fs.readAllBytes(same(path))).thenReturn(expectedBytes);
        // run
        byte[] bytes = repository.readAllBytes(stringPath);
        // check
        assertArrayEquals(expectedBytes, bytes, "Bytes");
    }

}