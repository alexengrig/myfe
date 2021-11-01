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

import java.util.Objects;

/**
 * Converter from {@link FileObject} to {@link FePath}.
 */
public class FileObject2MyPathConverter implements Converter<FileObject, FePath> {

    private final Converter<FileObject, FeDirectory> directoryConverter;
    private final Converter<FileObject, FeFile> fileConverter;

    public FileObject2MyPathConverter() {
        this(//TODO: Get from context
                new FileObject2MyDirectoryConverter(),
                new FileObject2MyFileConverter());
    }

    public FileObject2MyPathConverter(
            Converter<FileObject, FeDirectory> directoryConverter,
            Converter<FileObject, FeFile> fileConverter) {
        this.directoryConverter = directoryConverter;
        this.fileConverter = fileConverter;
    }

    @Override
    public FePath convert(FileObject source) {
        Objects.requireNonNull(source, "The source must not be null");
        if (isDirectory(source)) {
            return directoryConverter.convert(source);
        } else {
            return fileConverter.convert(source);
        }
    }

    private boolean isDirectory(FileObject source) {
        try {
            return source.isFolder();
        } catch (FileSystemException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
