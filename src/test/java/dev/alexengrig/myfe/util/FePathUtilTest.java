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

import dev.alexengrig.myfe.model.FeDirectory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FePathUtilTest {

    static Stream<Arguments> provide_childDirectory_expectedDirectory() {
        return Stream.of(
                Arguments.of(
                        new FeDirectory("C:\\folder\\subfolder", "subfolder"),
                        new FeDirectory("C:\\folder", "folder")),
                Arguments.of(
                        new FeDirectory("C:\\folder", "folder"),
                        new FeDirectory("C:\\", "C:\\")),
                Arguments.of(
                        new FeDirectory("/folder/subfolder", "subfolder"),
                        new FeDirectory("/folder", "folder")),
                Arguments.of(
                        new FeDirectory("/folder", "folder"),
                        new FeDirectory("/", "/"))
        );
    }

    static Stream<Arguments> provide_rootDirectory() {
        return Stream.of(
                Arguments.of(new FeDirectory("C:\\", "C:\\")),
                Arguments.of(new FeDirectory("/", "/"))
        );
    }

    static Stream<Arguments> provide_directory_expectedLevelCount() {
        return Stream.of(
                Arguments.of(new FeDirectory("/", "/"), 0),
                Arguments.of(new FeDirectory("/pub", "pub"), 1),
                Arguments.of(new FeDirectory("/pub/path", "path"), 2),
                Arguments.of(new FeDirectory("/pub/path/folder", "folder"), 3),
                Arguments.of(new FeDirectory("/pub/path/folder/subfolder", "subfolder"), 4),
                Arguments.of(new FeDirectory("C:\\", "C:\\"), 0),
                Arguments.of(new FeDirectory("C:\\path", "path"), 1),
                Arguments.of(new FeDirectory("C:\\path\\folder", "folder"), 2),
                Arguments.of(new FeDirectory("C:\\path\\folder\\subfolder", "subfolder"), 3)
        );
    }

    static Stream<Arguments> provide_directory_level_expectedName() {
        FeDirectory first = new FeDirectory("/pub/path/folder/subfolder", "subfolder");
        FeDirectory second = new FeDirectory("C:\\path\\folder\\subfolder", "subfolder");
        return Stream.of(
                Arguments.of(first, 0, "/"),
                Arguments.of(first, 1, "pub"),
                Arguments.of(first, 2, "path"),
                Arguments.of(first, 3, "folder"),
                Arguments.of(first, 4, "subfolder"),
                Arguments.of(second, 0, "C:\\"),
                Arguments.of(second, 1, "path"),
                Arguments.of(second, 2, "folder"),
                Arguments.of(second, 3, "subfolder")
        );
    }

    @ParameterizedTest
    @MethodSource("provide_childDirectory_expectedDirectory")
    void should_return_parent(FeDirectory childDirectory, FeDirectory expectedDirectory) {
        Optional<FeDirectory> optionalParentDirectory = FePathUtil.getParent(childDirectory);
        assertTrue(optionalParentDirectory.isPresent(), "Is empty");
        FeDirectory parentDirectory = optionalParentDirectory.get();
        assertEquals(expectedDirectory.getPath(), parentDirectory.getPath(), "Path");
        assertEquals(expectedDirectory.getName(), parentDirectory.getName(), "Name");
    }

    @ParameterizedTest
    @MethodSource("provide_rootDirectory")
    void shouldNot_return_parent(FeDirectory rootDirectory) {
        Optional<FeDirectory> optionalParentDirectory = FePathUtil.getParent(rootDirectory);
        assertFalse(optionalParentDirectory.isPresent(), "Is present");
    }

    @ParameterizedTest
    @MethodSource("provide_directory_expectedLevelCount")
    void should_return_levelCount(FeDirectory directory, int expectedLevelCount) {
        assertEquals(expectedLevelCount, FePathUtil.getLevelCount(directory), () ->
                "Level count for directory: " + directory);
    }

    @ParameterizedTest
    @MethodSource("provide_directory_level_expectedName")
    void should_return_nameByLevel(FeDirectory directory, int level, String expectedName) {
        assertEquals(expectedName, FePathUtil.getNameByLevel(directory, level), () ->
                "Name by level: " + level);
    }

}