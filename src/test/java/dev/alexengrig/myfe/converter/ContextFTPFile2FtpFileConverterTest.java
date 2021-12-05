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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContextFTPFile2FtpFileConverterTest {

    final ContextFTPFile2FtpFileConverter converter = new ContextFTPFile2FtpFileConverter();

    @Test
    void should_convert() {
        // setup
        FTPFile ftpFile = mock(FTPFile.class);
        when(ftpFile.getName()).thenReturn("file.this");
        ContextFTPFile contextFile = new ContextFTPFile("/path/to", "/", ftpFile);
        // run
        FtpFile file = converter.convert(contextFile);
        // check
        assertEquals("/path/to/file.this", file.getPath(), "Path");
        assertEquals("file.this", file.getName(), "Name");
    }

}