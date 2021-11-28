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
import dev.alexengrig.myfe.domain.FtpFile;
import org.apache.commons.net.ftp.FTPFile;

import java.util.Objects;

/**
 * Converter from {@link ContextFTPFile} to {@link FtpFile}.
 */
public class ContextFTPFile2FtpFileConverter implements Converter<ContextFTPFile, FtpFile> {

    @Override
    public FtpFile convert(ContextFTPFile source) {
        Objects.requireNonNull(source, "The source must not be null");
        FTPFile file = source.getFile();
        String name = file.getName();
        String path = source.getParentPath() + source.getSeparator() + name;
        return new FtpFile(path, name);
    }

}
