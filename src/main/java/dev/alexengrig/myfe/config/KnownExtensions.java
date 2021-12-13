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

package dev.alexengrig.myfe.config;

import java.util.Set;

/**
 * Supported file extensions.
 */
public interface KnownExtensions {

    Set<String> IMAGE_FILE_EXTENSIONS = Set.of("JPEG", "JPG", "PNG", "GIF", "XBM");

    //FIXME: Add other extensions
    Set<String> TEXT_FILE_EXTENSIONS = Set.of("TXT", "LOG", "XML", "JSON", "YAML", "YML", "PROPERTIES");

    //FIXME: Add other extensions
    Set<String> ARCHIVE_FILE_EXTENSIONS = Set.of("JAR", "ZIP", "7Z");

}
