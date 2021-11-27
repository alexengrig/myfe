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

import dev.alexengrig.myfe.domain.FeDirectory;
import dev.alexengrig.myfe.domain.FeFile;
import dev.alexengrig.myfe.domain.FePath;
import dev.alexengrig.myfe.util.PathUtil;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Converter from {@link Path} to {@link FePath}.
 */
public class Path2FePathConverter implements Converter<Path, FePath> {

    private final Converter<Path, FeDirectory> directoryConverter;
    private final Converter<Path, FeFile> fileConverter;

    public Path2FePathConverter() {
        this(//TODO: Get from context
                new Path2FeDirectoryConverter(),
                new Path2FeFileConverter());
    }

    public Path2FePathConverter(
            Converter<Path, FeDirectory> directoryConverter,
            Converter<Path, FeFile> fileConverter) {
        this.directoryConverter = directoryConverter;
        this.fileConverter = fileConverter;
    }

    @Override
    public FePath convert(Path source) {
        Objects.requireNonNull(source, "The source must not be null");
        if (PathUtil.isDirectory(source)) {
            return directoryConverter.convert(source);
        } else {
            return fileConverter.convert(source);
        }
    }

}
