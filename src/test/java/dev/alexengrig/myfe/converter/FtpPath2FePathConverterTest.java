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
import dev.alexengrig.myfe.domain.FtpDirectory;
import dev.alexengrig.myfe.domain.FtpFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class FtpPath2FePathConverterTest {

    Converter<FtpDirectory, FeDirectory> directoryConverter;
    Converter<FtpFile, FeFile> fileConverter;

    FtpPath2FePathConverter converter;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void beforeEach() {
        directoryConverter = mock(Converter.class);
        fileConverter = mock(Converter.class);
        converter = new FtpPath2FePathConverter(directoryConverter, fileConverter);
    }

    @Test
    void should_convert_file() {
        // setup
        String path = "/path/to/file.this";
        String name = "file.this";
        FtpFile ftpFile = new FtpFile(path, name);
        // run
        converter.convert(ftpFile);
        // check
        verify(fileConverter).convert(same(ftpFile));
        verifyNoInteractions(directoryConverter);
    }

    @Test
    void should_convert_directory() {
        // setup
        String path = "/path/to/directory";
        String name = "directory";
        FtpDirectory ftpDirectory = new FtpDirectory(path, name);
        // run
        converter.convert(ftpDirectory);
        // check
        verify(directoryConverter).convert(same(ftpDirectory));
        verifyNoInteractions(fileConverter);
    }

}