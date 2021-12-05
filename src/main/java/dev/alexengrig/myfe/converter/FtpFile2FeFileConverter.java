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

import java.util.Objects;

public class FtpFile2FeFileConverter implements Converter<FtpFile, FeFile> {

    @Override
    public FeFile convert(FtpFile source) {
        Objects.requireNonNull(source, "The source must not be null");
        String path = source.getPath();
        String name = source.getName();
        return new FeFile(path, name);
    }

}
