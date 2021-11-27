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
import dev.alexengrig.myfe.domain.FeDirectory;
import dev.alexengrig.myfe.domain.FeFile;
import dev.alexengrig.myfe.domain.FePath;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for {@link FePath}.
 */
public final class FePathUtil {

    private static final char SLASH_SEPARATOR = '/';
    private static final char BACKSLASH_SEPARATOR = '\\';

    private static final Pattern SEPARATOR_PATTERN = Pattern.compile("([/\\\\])");

    private FePathUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility class");
    }

    /**
     * Get type of path.
     *
     * <pre>{@code
     * directory              -> 'File folder'
     * file without extension -> 'File'
     * file with extension    -> 'EXTENSION file'
     * }</pre>
     *
     * @param path the path
     * @return type of {@code path}
     */
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

    /**
     * Get extension of file.
     *
     * @param file the file
     * @return extension of {@code file}
     */
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

    /**
     * Get parent of directory.
     *
     * <pre>{@code
     * /directory/subdirectory -> /directory
     * /                       -> {empty}
     * }</pre>
     *
     * @param directory the directory
     * @return parent of {@code directory}
     */
    public static Optional<FeDirectory> getParent(FeDirectory directory) {
        if (isRoot(directory)) {
            return Optional.empty();
        }
        String path = requireNonNullDirectory(directory).getPath();
        int lastIndexOfSeparator = lastIndexOfSeparator(path);
        if (lastIndexOfSeparator < 0) {
            throw new IllegalArgumentException("No path separator: " + path);
        }
        int nextLastIndexOfSeparator = lastIndexOfSeparator(path, lastIndexOfSeparator - 1);
        String parentPath;
        String parentName;
        if (nextLastIndexOfSeparator >= 0) {
            parentPath = path.substring(0, lastIndexOfSeparator);
            parentName = getNameByPath(parentPath);
        } else {
            parentPath = path.substring(0, lastIndexOfSeparator + 1);
            parentName = parentPath;
        }
        return Optional.of(new FeDirectory(parentPath, parentName));
    }

    /**
     * Get the number of directory levels.
     *
     * <pre>{@code
     * /         -> 0
     * /pub      -> 1
     * /pub/path -> 2
     * }</pre>
     *
     * @param directory the directory
     * @return the number of {@code directory} levels
     */
    public static int getLevelCount(FeDirectory directory) {
        if (isRoot(directory)) {
            return 0;
        }
        String path = requireNonNullDirectory(directory).getPath();
        Matcher matcher = SEPARATOR_PATTERN.matcher(path);
        if (!matcher.find()) {
            throw new IllegalArgumentException("No separator matches: " + directory);
        }
        int count = 1;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Get a directory name from the directory by the level.
     *
     * <pre>{@code
     * /path/to/directory:
     *   0 - /
     *   1 - path
     *   2 - to
     *   3 - directory
     * }</pre>
     *
     * @param directory the directory
     * @param level     the level
     * @return directory name from {@code directory} by {@code level}
     * @throws IndexOutOfBoundsException if {@code level} out of range
     * @see FePathUtil#getLevelCount(FeDirectory)
     */
    public static String getNameByLevel(FeDirectory directory, int level) {
        requireNonNullDirectory(directory);
        requirePositiveLevel(level);
        String path = directory.getPath();
        Matcher matcher = SEPARATOR_PATTERN.matcher(path);
        if (!matcher.find()) {
            throw new IllegalArgumentException("No separator matches: " + directory);
        }
        if (level == 0) {
            // Get left part with separator as root directory name, for example, "/" and "C:\"
            return path.substring(0, matcher.end());
        }
        int count = 1;
        while (count < level && matcher.find()) {
            count++;
        }
        if (count != level) {
            throw new IndexOutOfBoundsException("Level out of range: " + level);
        }
        int begin = matcher.end();
        if (matcher.find()) {
            int end = matcher.start();
            return path.substring(begin, end);
        } else {
            return path.substring(begin);
        }
    }

    /**
     * Split the directory by names.
     *
     * <pre>{@code
     * /path/to/directory -> [/, path, directory]
     * }</pre>
     *
     * @param directory the directory
     * @return names from {@code directory}
     */
    public static String[] splitByNames(FeDirectory directory) {
        String path = requireNonNullDirectory(directory).getPath();
        Matcher matcher = SEPARATOR_PATTERN.matcher(path);
        if (!matcher.find()) {
            throw new IllegalArgumentException("No separator matches: " + directory);
        }
        List<String> names = new LinkedList<>();
        int begin;
        names.add(path.substring(0, (begin = matcher.end())));
        while (matcher.find()) {
            names.add(path.substring(begin, matcher.start()));
            begin = matcher.end();
        }
        names.add(path.substring(begin));
        return names.toArray(String[]::new);
    }

    /**
     * Get a name by the path.
     *
     * <pre>{@code
     * /path/to/directory -> directory
     * /path/to/file.this -> file.this
     * }</pre>
     *
     * @param path the path
     * @return name by {@code path}
     */
    public static String getNameByPath(String path) {
        int lastIndexOfSeparator = lastIndexOfSeparator(path);
        if (lastIndexOfSeparator < 0) {
            throw new IllegalArgumentException("No path separator: " + path);
        }
        if (lastIndexOfSeparator == path.length() - 1) {
            return path;
        }
        return path.substring(lastIndexOfSeparator + 1);
    }

    /**
     * Check that the file is an image.
     *
     * @param file the file
     * @return true - if {@code file} is an image
     */
    public static boolean isImage(FeFile file) {
        Optional<String> fileExtension = getFileExtension(file);
        return fileExtension
                .filter(KnownExtensions.IMAGE_FILE_EXTENSIONS::contains)
                .isPresent();
    }

    /**
     * Check that the file is text.
     *
     * @param file the file
     * @return true - if {@code file} is text
     */
    public static boolean isText(FeFile file) {
        Optional<String> fileExtension = getFileExtension(file);
        return fileExtension
                .filter(KnownExtensions.TEXT_FILE_EXTENSIONS::contains)
                .isPresent();
    }

    /**
     * Check that the file is an archive.
     *
     * @param file the file
     * @return true - if {@code file} is an archive
     */
    public static boolean isArchive(FeFile file) {
        Optional<String> fileExtension = getFileExtension(file);
        return fileExtension
                .filter(KnownExtensions.ARCHIVE_FILE_EXTENSIONS::contains)
                .isPresent();
    }

    private static boolean isRoot(FeDirectory directory) {
        return directory.getPath().equals(directory.getName());
    }

    private static int lastIndexOfSeparator(String path) {
        int lastIndexOfSlash = path.lastIndexOf(SLASH_SEPARATOR);
        if (lastIndexOfSlash >= 0) {
            return lastIndexOfSlash;
        }
        return path.lastIndexOf(BACKSLASH_SEPARATOR);
    }

    private static int lastIndexOfSeparator(String path, int fromIndex) {
        int lastIndexOfSlash = path.lastIndexOf(SLASH_SEPARATOR, fromIndex);
        if (lastIndexOfSlash >= 0) {
            return lastIndexOfSlash;
        }
        return path.lastIndexOf(BACKSLASH_SEPARATOR, fromIndex);
    }

    private static FePath requireNonNullPath(FePath path) {
        return Objects.requireNonNull(path, "The path must not be null");
    }

    private static FeFile requireNonNullFile(FeFile file) {
        return Objects.requireNonNull(file, "The file must not be null");
    }

    private static FeDirectory requireNonNullDirectory(FeDirectory directory) {
        return Objects.requireNonNull(directory, "The directory must not be null");
    }

    private static void requirePositiveLevel(int level) {
        if (level < 0) {
            throw new IllegalArgumentException("The level must not be negative: " + level);
        }
    }

}
