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

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * {@link FileSystems#getDefault()}-based implementation.
 */
public class LocalFileSystemPathRepository extends AidFileSystemPathRepository {

    public LocalFileSystemPathRepository(
            Converter<Path, FeDirectory> directoryConverter,
            Converter<Path, FePath> pathConverter) {
        super(FileSystems.getDefault(), directoryConverter, pathConverter);
    }

}
