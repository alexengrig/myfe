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

import dev.alexengrig.myfe.config.KnownExtensions;
import dev.alexengrig.myfe.model.FeDirectory;
import dev.alexengrig.myfe.model.FeFile;
import dev.alexengrig.myfe.model.FePath;

import java.util.Objects;
import java.util.Optional;

/**
 * A utility class for {@link FePath}.
 */
public final class FePathUtil {

    private FePathUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility class");
    }

    public static String getType(FePath path) {
        if (requireNonNullPath(path).isDirectory()) {
            return "File folder";
        } else {
            Optional<String> fileExtension = doGetFileExtension(path.asFile());
            return fileExtension
                    .map(extension -> extension.concat(" file"))
                    .orElse("File");
        }
    }

    public static Optional<String> getFileExtension(FeFile file) {
        return doGetFileExtension(requireNonNullFile(file));
    }

    private static Optional<String> doGetFileExtension(FeFile file) {
        String name = file.getName();
        int indexOfDot = name.lastIndexOf('.');
        if (indexOfDot >= 0) {
            return Optional.of(name.substring(indexOfDot + 1).toUpperCase());
        } else {
            return Optional.empty();
        }
    }

    public static Optional<FeDirectory> getParent(FeDirectory directory) {
        if (isRoot(directory)) {
            return Optional.empty();
        }
        String path = requireNonNullPath(directory).getPath();
        int lastIndexOfPathSeparator = lastIndexOfPathSeparator(path);
        if (lastIndexOfPathSeparator < 0) {
            throw new IllegalArgumentException("No path separator: " + path);
        }
        int nextLastIndexOfPathSeparator = lastIndexOfPathSeparator(path, lastIndexOfPathSeparator - 1);
        String parentPath;
        String parentName;
        if (nextLastIndexOfPathSeparator >= 0) {
            parentPath = path.substring(0, lastIndexOfPathSeparator);
            parentName = getNameByPath(parentPath);
        } else {
            parentPath = path.substring(0, lastIndexOfPathSeparator + 1);
            parentName = parentPath;
        }
        return Optional.of(new FeDirectory(parentPath, parentName));
    }

    public static boolean isImage(FeFile file) {
        Optional<String> fileExtension = getFileExtension(file);
        return fileExtension
                .filter(KnownExtensions.IMAGE_FILE_EXTENSIONS::contains)
                .isPresent();
    }

    public static boolean isText(FeFile file) {
        Optional<String> fileExtension = getFileExtension(file);
        return fileExtension
                .filter(KnownExtensions.TEXT_FILE_EXTENSIONS::contains)
                .isPresent();
    }

    public static boolean isArchive(FeFile file) {
        Optional<String> fileExtension = getFileExtension(file);
        return fileExtension
                .filter(KnownExtensions.ARCHIVE_FILE_EXTENSIONS::contains)
                .isPresent();
    }

    private static boolean isRoot(FeDirectory directory) {
        return directory.getPath().equals(directory.getName());
    }

    private static int lastIndexOfPathSeparator(String path) {
        int lastIndexOfSlash = path.lastIndexOf('/');
        if (lastIndexOfSlash >= 0) {
            return lastIndexOfSlash;
        }
        return path.lastIndexOf('\\');
    }

    private static int lastIndexOfPathSeparator(String path, int fromIndex) {
        int lastIndexOfSlash = path.lastIndexOf('/', fromIndex);
        if (lastIndexOfSlash >= 0) {
            return lastIndexOfSlash;
        }
        return path.lastIndexOf('\\', fromIndex);
    }

    private static String getNameByPath(String path) {
        int lastIndexOfPathSeparator = lastIndexOfPathSeparator(path);
        if (lastIndexOfPathSeparator < 0) {
            throw new IllegalArgumentException("No path separator: " + path);
        }
        if (lastIndexOfPathSeparator == path.length() - 1) {
            return path;
        }
        return path.substring(lastIndexOfPathSeparator + 1);
    }

    private static FePath requireNonNullPath(FePath path) {
        return Objects.requireNonNull(path, "The path must not be null");
    }

    private static FeFile requireNonNullFile(FeFile file) {
        return Objects.requireNonNull(file, "The file must not be null");
    }

}
