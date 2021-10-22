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

package dev.alexengrig.myfe.util;

import dev.alexengrig.myfe.model.MyFile;
import dev.alexengrig.myfe.model.MyPath;

import java.util.Objects;
import java.util.Set;

/**
 * A utility class for {@link MyPath}.
 */
public class MyPathUtil {

    private static final Set<String> IMAGE_FILE_EXTENSIONS = Set.of("JPEG", "JPG", "GIF", "XBM");
    private static final Set<String> TEXT_FILE_EXTENSIONS = Set.of("TXT", "LOG");
    private static final Set<String> ARCHIVE_FILE_EXTENSIONS = Set.of("JAR", "ZIP");

    private MyPathUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility class");
    }

    public static String getExtension(MyPath path) {
        if (requireNonNullPath(path).isDirectory()) {
            return "File folder";
        } else {
            String name = path.getName();
            int indexOfDot = name.lastIndexOf('.');
            if (indexOfDot >= 0) {
                return name.substring(indexOfDot + 1).toUpperCase();
            } else {
                return "File";
            }
        }
    }

    public static boolean isImage(MyFile file) {
        return IMAGE_FILE_EXTENSIONS.contains(requireNonNullFile(file).getExtension());
    }

    public static boolean isText(MyFile file) {
        return TEXT_FILE_EXTENSIONS.contains(requireNonNullFile(file).getExtension());
    }

    public static boolean isArchive(MyFile file) {
        return ARCHIVE_FILE_EXTENSIONS.contains(requireNonNullFile(file).getExtension());
    }

    private static MyPath requireNonNullPath(MyPath path) {
        return Objects.requireNonNull(path, "The path must not be null");
    }

    private static MyFile requireNonNullFile(MyFile file) {
        return Objects.requireNonNull(file, "The file must not be null");
    }

}
