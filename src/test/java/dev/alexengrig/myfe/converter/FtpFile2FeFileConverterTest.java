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

import dev.alexengrig.myfe.domain.FeFile;
import dev.alexengrig.myfe.domain.FtpFile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FtpFile2FeFileConverterTest {

    final FtpFile2FeFileConverter converter = new FtpFile2FeFileConverter();

    @Test
    void should_convert() {
        // setup
        String path = "/path/to/file.this";
        String name = "file.this";
        FtpFile ftpFile = new FtpFile(path, name);
        // run
        FeFile file = converter.convert(ftpFile);
        // check
        assertEquals(path, file.getPath(), "Path");
        assertEquals(name, file.getName(), "Name");
    }

}