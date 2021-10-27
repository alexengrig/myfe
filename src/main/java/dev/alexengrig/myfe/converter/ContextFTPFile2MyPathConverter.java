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

import dev.alexengrig.myfe.model.ContextFTPFile;
import dev.alexengrig.myfe.model.MyFtpDirectory;
import dev.alexengrig.myfe.model.MyFtpFile;
import dev.alexengrig.myfe.model.MyFtpPath;

import java.util.Objects;

/**
 * Converter from {@link ContextFTPFile} to {@link MyFtpPath}.
 */
public class ContextFTPFile2MyPathConverter implements Converter<ContextFTPFile, MyFtpPath> {

    private final Converter<ContextFTPFile, MyFtpDirectory> directoryConverter;
    private final Converter<ContextFTPFile, MyFtpFile> fileConverter;

    public ContextFTPFile2MyPathConverter() {
        //TODO: Get from context
        this(new ContextFTPFile2MyFtpDirectoryConverter(), new ContextFTPFile2MyFtpFileConverter());
    }

    public ContextFTPFile2MyPathConverter(
            Converter<ContextFTPFile, MyFtpDirectory> directoryConverter,
            Converter<ContextFTPFile, MyFtpFile> fileConverter) {
        this.directoryConverter = directoryConverter;
        this.fileConverter = fileConverter;
    }

    @Override
    public MyFtpPath convert(ContextFTPFile source) {
        Objects.requireNonNull(source, "The source must not be null");
        if (source.isDirectory()) {
            return directoryConverter.convert(source);
        } else {
            return fileConverter.convert(source);
        }
    }

}
