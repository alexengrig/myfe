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
import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ContextFTPFile2FtpPathConverterTest {

    Converter<ContextFTPFile, FtpDirectory> directoryConverter;
    Converter<ContextFTPFile, FtpFile> fileConverter;

    ContextFTPFile2FtpPathConverter converter;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void beforeEach() {
        directoryConverter = mock(Converter.class);
        fileConverter = mock(Converter.class);
        converter = new ContextFTPFile2FtpPathConverter(directoryConverter, fileConverter);
    }

    @Test
    void should_convert_file() {
        // setup
        FTPFile ftpFile = mock(FTPFile.class);
        when(ftpFile.getName()).thenReturn("file.this");
        when(ftpFile.isDirectory()).thenReturn(false);
        ContextFTPFile contextFile = new ContextFTPFile("/path/to", "/", ftpFile);
        // run
        converter.convert(contextFile);
        // check
        verify(fileConverter).convert(same(contextFile));
        verifyNoInteractions(directoryConverter);
    }

    @Test
    void should_convert_directory() {
        // setup
        FTPFile ftpDirectory = mock(FTPFile.class);
        when(ftpDirectory.getName()).thenReturn("directory");
        when(ftpDirectory.isDirectory()).thenReturn(true);
        ContextFTPFile contextDirectory = new ContextFTPFile("/path/to", "/", ftpDirectory);
        // run
        converter.convert(contextDirectory);
        // check
        verify(directoryConverter).convert(same(contextDirectory));
        verifyNoInteractions(fileConverter);
    }

}