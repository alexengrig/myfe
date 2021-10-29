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
import dev.alexengrig.myfe.model.MyFtpDirectory;

import java.util.Objects;

/**
 * Converter from {@link MyFtpDirectory} to {@link MyDirectory}.
 */
public class MyFtpDirectory2MyDirectoryConverter implements Converter<MyFtpDirectory, MyDirectory> {

    @Override
    public MyDirectory convert(MyFtpDirectory source) {
        Objects.requireNonNull(source, "The source must not be null");
        String path = source.getPath();
        String name = source.getName();
        return new MyDirectory(path, name);
    }

}
