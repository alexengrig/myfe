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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertSame;

class ReflectionUtilTest {

    public static Stream<Arguments> provide_superType_expectedGenericType() {
        return Stream.of(
                Arguments.of(StringSuperGeneric.class, String.class),
                Arguments.of(StringLongSuperGeneric.class, String.class)
        );
    }

    public static Stream<Arguments> provide_superType_index_expectedGenericType() {
        return Stream.of(
                Arguments.of(StringSuperGeneric.class, 0, String.class),
                Arguments.of(StringLongSuperGeneric.class, 0, String.class),
                Arguments.of(StringLongSuperGeneric.class, 1, Long.class)
        );
    }

    @ParameterizedTest
    @MethodSource("provide_superType_expectedGenericType")
    void should_return_firstGenericTypeFromSuperclass(Class<?> superType, Class<?> expectedGenericType) {
        Class<?> genericType = ReflectionUtil.getGenericTypeFromSuperclass(superType);
        assertSame(expectedGenericType, genericType, "Generic type of superclass");
    }

    @ParameterizedTest
    @MethodSource("provide_superType_index_expectedGenericType")
    void should_return_genericTypeFromSuperclassByIndex(Class<?> superType, int index, Class<?> expectedType) {
        Class<?> genericType = ReflectionUtil.getGenericTypeFromSuperclass(superType, index);
        assertSame(expectedType, genericType, "Generic type of superclass");
    }

    @SuppressWarnings("unused")
    static class SingleSuperGeneric<T> {
    }

    static final class StringSuperGeneric extends SingleSuperGeneric<String> {
    }

    @SuppressWarnings("unused")
    static class BiSuperGeneric<T, R> extends SingleSuperGeneric<T> {
    }

    static final class StringLongSuperGeneric extends BiSuperGeneric<String, Long> {
    }

}