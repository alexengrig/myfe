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

package dev.alexengrig.myfe.service.directory;

import dev.alexengrig.myfe.model.directory.ContentModel;
import dev.alexengrig.myfe.model.directory.DirectoryModel;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DefaultFileSystemDirectoryService implements DirectoryService {

    private final FileSystem fileSystem = FileSystems.getDefault();

    @Override
    public List<DirectoryModel> getSubdirectories(String parentPath) {
        if (parentPath != null) {
            Path directory = fileSystem.getPath(parentPath);
            try (Stream<Path> stream = Files.list(directory)) {
                return stream
                        .filter(Files::isDirectory)
                        .map(DirectoryModel::from)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException("For path: " + parentPath, e); //TODO: Create exception class
            }
        } else {
            Iterable<Path> rootDirectories = fileSystem.getRootDirectories();
            try (Stream<Path> stream = StreamSupport.stream(rootDirectories.spliterator(), false)) {
                return stream
                        .filter(Files::isDirectory)
                        .map(DirectoryModel::from)
                        .collect(Collectors.toList());
            }
        }
    }

    @Override
    public List<ContentModel> getContent(String parentPath) {
        if (parentPath != null) {
            Path directory = fileSystem.getPath(parentPath);
            try (Stream<Path> stream = Files.list(directory)) {
                return stream
                        .map(ContentModel::from)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException("For path: " + parentPath, e); //TODO: Create exception class
            }
        } else {
            Iterable<Path> rootDirectories = fileSystem.getRootDirectories();
            try (Stream<Path> stream = StreamSupport.stream(rootDirectories.spliterator(), false)) {
                return stream
                        .map(ContentModel::from)
                        .collect(Collectors.toList());
            }
        }
    }
}
