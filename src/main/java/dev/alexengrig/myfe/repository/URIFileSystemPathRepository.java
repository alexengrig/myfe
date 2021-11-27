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

import dev.alexengrig.myfe.converter.Converter;
import dev.alexengrig.myfe.domain.FeDirectory;
import dev.alexengrig.myfe.domain.FePath;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

/**
 * {@link FileSystems#newFileSystem(URI, Map)}-based implementation.
 */
public class URIFileSystemPathRepository extends AidFileSystemPathRepository {

    public URIFileSystemPathRepository(
            URI uri,
            Map<String, Object> environment,
            Converter<Path, FeDirectory> directoryConverter,
            Converter<Path, FePath> pathConverter) {
        super(createFileSystem(uri, environment), directoryConverter, pathConverter);
    }

    private static FileSystem createFileSystem(URI uri, Map<String, Object> environment) {
        try {
            return FileSystems.newFileSystem(uri, environment);
        } catch (IOException e) {
            throw new UncheckedIOException("Exception of creating file system for URI: " + uri, e);
        }
    }

}
