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

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A utility class for {@link java.lang.reflect}.
 */
public final class ReflectionUtil {

    private ReflectionUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility class");
    }

    public static <T> Class<T> getGenericTypeFromSuperclass(Class<?> type) {
        return getGenericTypeFromSuperclass(type, 0);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getGenericTypeFromSuperclass(Class<?> type, int index) {
        ParameterizedType genericSupertype = (ParameterizedType) type.getGenericSuperclass();
        Type[] genericTypes = genericSupertype.getActualTypeArguments();
        return (Class<T>) genericTypes[index];
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<T> type, int length) {
        return (T[]) Array.newInstance(type, length);
    }

}
