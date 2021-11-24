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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class PathUtilTest {

    static Path TEMP_DIRECTORY;
    static Path TEMP_SUBDIRECTORY;
    static Path TEMP_FILE;

    @BeforeAll
    static void beforeAll() throws IOException {
        TEMP_DIRECTORY = Files.createTempDirectory("PathUtilTest-test-directory");
        TEMP_SUBDIRECTORY = Files.createTempDirectory(TEMP_DIRECTORY, "PathUtilTest-test-subdirectory");
        TEMP_FILE = Files.createTempFile(TEMP_DIRECTORY, "PathUtilTest-test-file", "");
    }

    @AfterAll
    static void afterAll() throws IOException {
        Files.deleteIfExists(TEMP_FILE);
        Files.deleteIfExists(TEMP_SUBDIRECTORY);
        Files.deleteIfExists(TEMP_DIRECTORY);
    }

    static Stream<Path> getTestPaths() {
        return Stream.concat(
                StreamSupport.stream(FileSystems.getDefault().getRootDirectories().spliterator(), false),
                Stream.of(TEMP_DIRECTORY, TEMP_SUBDIRECTORY, TEMP_FILE)
        );
    }

    static Stream<Arguments> provide_path_expectedName() {
        return Stream.concat(
                StreamSupport.stream(FileSystems.getDefault().getRootDirectories().spliterator(), false)
                        .map(path -> Arguments.of(path, path.toString())),
                Stream.of(TEMP_DIRECTORY, TEMP_SUBDIRECTORY, TEMP_FILE)
                        .map(path -> Arguments.of(path, path.getFileName().toString()))
        );
    }

    static Stream<Arguments> provide_path_expectedAbsolutePath() {
        return getTestPaths()
                .map(path -> Arguments.of(path, path.toAbsolutePath().toString()));
    }

    static Stream<Arguments> provide_path_isDirectory() {
        return getTestPaths()
                .map(path -> Arguments.of(path, Files.isDirectory(path)));
    }

    static Stream<Arguments> provide_path_nonDirectory() {
        return getTestPaths()
                .map(path -> Arguments.of(path, !Files.isDirectory(path)));
    }

    static Stream<Arguments> provide_path_expectedSubdirectories() {
        return Stream.of(
                Arguments.of(TEMP_DIRECTORY, Collections.singletonList(TEMP_SUBDIRECTORY)),
                Arguments.of(TEMP_SUBDIRECTORY, Collections.emptyList())
        );
    }

    static Stream<Arguments> provide_path_expectedChildren() {
        return Stream.of(
                Arguments.of(TEMP_DIRECTORY, Arrays.asList(TEMP_SUBDIRECTORY, TEMP_FILE)),
                Arguments.of(TEMP_SUBDIRECTORY, Collections.emptyList())
        );
    }

    @ParameterizedTest
    @MethodSource("provide_path_expectedName")
    void should_return_pathName(Path path, String expectedName) {
        assertEquals(expectedName, PathUtil.getName(path), () ->
                "Name for: " + path);
    }

    @ParameterizedTest
    @MethodSource("provide_path_expectedAbsolutePath")
    void should_return_absolutePath(Path path, String expectedAbsolutePath) {
        assertEquals(expectedAbsolutePath, PathUtil.getAbsolutePath(path), () ->
                "Path for: " + path);
    }

    @ParameterizedTest
    @MethodSource("provide_path_isDirectory")
    void should_check_isDirectory(Path path, boolean isDirectory) {
        assertEquals(isDirectory, PathUtil.isDirectory(path), () ->
                "Check for: " + path);
    }

    @ParameterizedTest
    @MethodSource("provide_path_nonDirectory")
    void should_check_nonDirectory(Path path, boolean nonDirectory) {
        assertEquals(nonDirectory, PathUtil.nonDirectory(path), () ->
                "Check for: " + path);
    }

    @ParameterizedTest
    @MethodSource("provide_path_expectedSubdirectories")
    void should_return_subdirectories(Path path, List<Path> expectedSubdirectories) {
        assertIterableEquals(expectedSubdirectories, PathUtil.getSubdirectories(path), () ->
                "Subdirectories for: " + path);
    }

    @ParameterizedTest
    @MethodSource("provide_path_expectedChildren")
    void should_return_children(Path path, List<Path> expectedChildren) {
        List<Path> actualChildren = PathUtil.getChildren(path);
        Object[] expected = new HashSet<>(expectedChildren).toArray();
        Object[] actual = new HashSet<>(actualChildren).toArray();
        assertArrayEquals(expected, actual, () -> "Children for: " + path);
    }

}