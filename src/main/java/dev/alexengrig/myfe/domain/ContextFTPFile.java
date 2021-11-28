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

package dev.alexengrig.myfe.domain;

import org.apache.commons.net.ftp.FTPFile;

import java.util.function.Function;

public class ContextFTPFile {

    private final String parentPath;
    private final String separator;
    private final FTPFile file;

    public ContextFTPFile(String parentPath, String separator, FTPFile file) {
        this.parentPath = parentPath;
        this.separator = separator;
        this.file = file;
    }

    public static Function<FTPFile, ContextFTPFile> factory(String parentPath, String separator) {
        return ftpFile -> new ContextFTPFile(parentPath, separator, ftpFile);
    }

    public String getParentPath() {
        return parentPath;
    }

    public String getSeparator() {
        return separator;
    }

    public FTPFile getFile() {
        return file;
    }

}
