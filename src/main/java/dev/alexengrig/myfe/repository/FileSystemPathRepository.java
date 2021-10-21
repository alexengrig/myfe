/*
 * Copyright 2020-2021 Alexengrig Dev.
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

import dev.alexengrig.myfe.converter.Converter;
import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.util.CloseOnTerminalOperationStreams;
import dev.alexengrig.myfe.util.PathUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

/**
 * {@link FileSystem}-based implementation.
 */
public class FileSystemPathRepository implements MyPathRepository {

    private final FileSystem fileSystem;
    private final Converter<Path, MyDirectory> directoryConverter;
    private final Converter<Path, MyPath> pathConverter;

    public FileSystemPathRepository(
            FileSystem fileSystem,
            Converter<Path, MyDirectory> directoryConverter,
            Converter<Path, MyPath> pathConverter) {
        this.fileSystem = fileSystem;
        this.directoryConverter = directoryConverter;
        this.pathConverter = pathConverter;
    }

    @Override
    public List<MyDirectory> getRootDirectories() {
        Iterable<Path> directories = fileSystem.getRootDirectories();
        return StreamSupport.stream(directories.spliterator(), false)
                .map(directoryConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<MyPath> getChildren(String directoryPath) {
        Path directory = fileSystem.getPath(requireNonNullPath(directoryPath));
        List<Path> children = PathUtil.getChildren(directory);
        return children.stream()
                .map(pathConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<MyDirectory> getSubdirectories(String directoryPath) {
        Path directory = fileSystem.getPath(requireNonNullPath(directoryPath));
        final List<Path> subdirectories = PathUtil.getSubdirectories(directory);
        return subdirectories.stream()
                .map(directoryConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Stream<String> readByLine(String filePath) {
        try {
            Path path = fileSystem.getPath(requireNonNullPath(filePath));
            Stream<String> readStream = Files.lines(path, StandardCharsets.UTF_8);
            return CloseOnTerminalOperationStreams.wrap(readStream);
        } catch (IOException e) {
            throw new UncheckedIOException("Exception of reading by line for path: " + filePath, e);
        }
    }

    private String requireNonNullPath(String path) {
        return requireNonNull(path, "The path must not be null");
    }

}
