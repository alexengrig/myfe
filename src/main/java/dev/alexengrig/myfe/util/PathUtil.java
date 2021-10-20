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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * A utility class for {@link Path}.
 */
public final class PathUtil {

    private PathUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility class");
    }

    public static String getName(Path path) {
        if (requireNonNullPath(path).getNameCount() == 0) {
            return path.toString();
        }
        return path.getFileName().toString();
    }

    public static String getAbsolutePath(Path path) {
        return requireNonNullPath(path).toAbsolutePath().toString();
    }

    public static boolean isDirectory(Path path) {
        return doIsDirectory(requireNonNullPath(path));
    }

    private static boolean doIsDirectory(Path path) {
        return Files.isDirectory(path);
    }

    public static boolean nonDirectory(Path path) {
        return doNonDirectory(requireNonNullPath(path));
    }

    private static boolean doNonDirectory(Path path) {
        return !doIsDirectory(path);
    }

    public static List<Path> getSubdirectories(Path path) {
        try (Stream<Path> stream = Files.list(requireDirectory(path))) {
            return stream.filter(PathUtil::doIsDirectory).collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException("Exception of getting subdirectories for path: " + path, e);
        }
    }

    public static List<Path> getChildren(Path path) {
        try (Stream<Path> stream = Files.list(requireDirectory(path))) {
            return stream.collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException("Exception of getting children for path: " + path, e);
        }
    }

    private static Path requireNonNullPath(Path path) {
        return requireNonNull(path, "The path must not be null");
    }

    private static Path requireDirectory(Path path) {
        if (doNonDirectory(requireNonNullPath(path))) {
            throw new IllegalArgumentException("The path isn't directory: " + path);
        }
        return path;
    }

}
