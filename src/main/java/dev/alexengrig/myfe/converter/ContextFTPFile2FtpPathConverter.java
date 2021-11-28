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
import dev.alexengrig.myfe.domain.FtpFile;
import dev.alexengrig.myfe.domain.FtpPath;
import org.apache.commons.net.ftp.FTPFile;

import java.util.Objects;

/**
 * Converter from {@link ContextFTPFile} to {@link FtpPath}.
 */
public class ContextFTPFile2FtpPathConverter implements Converter<ContextFTPFile, FtpPath> {

    private final Converter<ContextFTPFile, FtpDirectory> directoryConverter;
    private final Converter<ContextFTPFile, FtpFile> fileConverter;

    public ContextFTPFile2FtpPathConverter() {
        this(//TODO: Get from context
                new ContextFTPFile2FtpDirectoryConverter(),
                new ContextFTPFile2FtpFileConverter());
    }

    public ContextFTPFile2FtpPathConverter(
            Converter<ContextFTPFile, FtpDirectory> directoryConverter,
            Converter<ContextFTPFile, FtpFile> fileConverter) {
        this.directoryConverter = directoryConverter;
        this.fileConverter = fileConverter;
    }

    @Override
    public FtpPath convert(ContextFTPFile source) {
        Objects.requireNonNull(source, "The source must not be null");
        FTPFile file = source.getFile();
        if (file.isDirectory()) {
            return directoryConverter.convert(source);
        } else {
            return fileConverter.convert(source);
        }
    }

}
