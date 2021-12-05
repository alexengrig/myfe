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

import dev.alexengrig.myfe.domain.ContextFTPFile;
import dev.alexengrig.myfe.domain.FtpDirectory;

import java.util.Objects;

/**
 * Converter from {@link ContextFTPFile} to {@link FtpDirectory}.
 */
public class ContextFTPFile2FtpDirectoryConverter implements Converter<ContextFTPFile, FtpDirectory> {

    @Override
    public FtpDirectory convert(ContextFTPFile source) {
        Objects.requireNonNull(source, "The source must not be null");
        String name = source.getFile().getName();
        String path = getPath(source);
        return new FtpDirectory(path, name);
    }

    private String getPath(ContextFTPFile source) {
        String parentPath = source.getParentPath();
        String separator = source.getSeparator();
        String name = source.getFile().getName();
        if (parentPath.endsWith(separator)) {
            return parentPath + name;
        } else {
            return parentPath + separator + name;
        }
    }

}
