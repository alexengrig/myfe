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

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.util.List;

/**
 * Helper of file system.
 */
public interface FileSystemHelper extends Closeable {

    Path getPath(String path);

    Iterable<Path> getRootDirectories();

    List<Path> getChildren(Path directory);

    List<Path> getSubdirectories(Path directory);

    SeekableByteChannel newByteChannel(Path path) throws IOException;

    byte[] readAllBytes(Path path) throws IOException;

}
