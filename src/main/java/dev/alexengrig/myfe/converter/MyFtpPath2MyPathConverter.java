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

import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyFile;
import dev.alexengrig.myfe.model.MyFtpDirectory;
import dev.alexengrig.myfe.model.MyFtpFile;
import dev.alexengrig.myfe.model.MyFtpPath;
import dev.alexengrig.myfe.model.MyPath;

import java.util.Objects;

/**
 * Converter from {@link MyFtpPath} to {@link MyPath}.
 */
public class MyFtpPath2MyPathConverter implements Converter<MyFtpPath, MyPath> {

    private final Converter<MyFtpDirectory, MyDirectory> directoryConverter;
    private final Converter<MyFtpFile, MyFile> fileConverter;

    public MyFtpPath2MyPathConverter() {
        this(//TODO: Get from context
                new MyFtpDirectory2MyDirectoryConverter(),
                new MyFtpFile2MyFileConverter());
    }

    public MyFtpPath2MyPathConverter(
            Converter<MyFtpDirectory, MyDirectory> directoryConverter,
            Converter<MyFtpFile, MyFile> fileConverter) {
        this.directoryConverter = directoryConverter;
        this.fileConverter = fileConverter;
    }

    @Override
    public MyPath convert(MyFtpPath source) {
        Objects.requireNonNull(source, "The source must not be null");
        if (source.isDirectory()) {
            return directoryConverter.convert(source.asDirectory());
        } else {
            return fileConverter.convert(source.asFile());
        }
    }

}
