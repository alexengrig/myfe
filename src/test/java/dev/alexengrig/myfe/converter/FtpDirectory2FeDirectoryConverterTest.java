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
import dev.alexengrig.myfe.domain.FtpDirectory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FtpDirectory2FeDirectoryConverterTest {

    final FtpDirectory2FeDirectoryConverter converter = new FtpDirectory2FeDirectoryConverter();

    @Test
    void should_convert() {
        // setup
        String path = "/path/to/directory";
        String name = "directory";
        FtpDirectory ftpDirectory = new FtpDirectory(path, name);
        // run
        FeDirectory directory = converter.convert(ftpDirectory);
        // check
        assertEquals(path, directory.getPath(), "Path");
        assertEquals(name, directory.getName(), "Name");
    }

}