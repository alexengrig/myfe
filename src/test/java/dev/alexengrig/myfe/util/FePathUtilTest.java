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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @ParameterizedTest
    @MethodSource("provide_childDirectory_expectedDirectory")
    void should_return_parent(FeDirectory childDirectory, FeDirectory expectedDirectory) {
        Optional<FeDirectory> optionalParentDirectory = FePathUtil.getParent(childDirectory);
        assertTrue(optionalParentDirectory.isPresent(), "Is empty");
        FeDirectory parentDirectory = optionalParentDirectory.get();
        assertEquals(expectedDirectory.getPath(), parentDirectory.getPath(), "Path");
        assertEquals(expectedDirectory.getName(), parentDirectory.getName(), "Name");
    }

    @Test
    void should_throw_IAE_for_gettingParent() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                FePathUtil.getParent(new FeDirectory("invalid-path", "invalid-name")));
        assertEquals("No path separator: invalid-path", exception.getMessage(), "Exception message");
    }

    @ParameterizedTest
    @MethodSource("provide_rootDirectory")
    void shouldNot_return_parent(FeDirectory rootDirectory) {
        Optional<FeDirectory> optionalParentDirectory = FePathUtil.getParent(rootDirectory);
        assertFalse(optionalParentDirectory.isPresent(), "Is present");
    }

}