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

import dev.alexengrig.myfe.util.PathUtil;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Base implementation of {@link FileSystemHelper}.
 */
public class SimpleFileSystemHelper implements FileSystemHelper {

    private final FileSystem fs;

    public SimpleFileSystemHelper(FileSystem fileSystem) {
        this.fs = fileSystem;
    }

    @Override
    public Path getPath(String path) {
        return fs.getPath(path);
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return fs.getRootDirectories();
    }

    @Override
    public List<Path> getChildren(Path directory) {
        return PathUtil.getChildren(directory);
    }

    @Override
    public List<Path> getSubdirectories(Path directory) {
        return PathUtil.getSubdirectories(directory);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path) throws IOException {
        return Files.newByteChannel(path);
    }

    @Override
    public byte[] readAllBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    @Override
    public void close() throws IOException {
        fs.close();
    }

}
