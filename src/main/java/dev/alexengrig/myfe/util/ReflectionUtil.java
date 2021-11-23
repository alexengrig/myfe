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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A utility class for {@link java.lang.reflect}.
 */
public final class ReflectionUtil {

    private ReflectionUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility class");
    }

    /**
     * Returns the first generic class from the superclass of the given class.
     *
     * @param type child class
     * @param <T>  the type of the generic class
     * @return the generic class
     * @see ReflectionUtil#getGenericTypeFromSuperclass(java.lang.Class, int)
     */
    public static <T> Class<T> getGenericTypeFromSuperclass(Class<?> type) {
        return getGenericTypeFromSuperclass(type, 0);
    }

    /**
     * Returns the generic class from the superclass of the given class by index.
     *
     * <pre>{@code
     * // 0 - String
     * // 1 - Long
     * class Child extends Parent<String, Long> {}
     * }</pre>
     *
     * @param type  child class
     * @param index index of the generic class
     * @param <T>   the type of the generic class
     * @return the generic class
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getGenericTypeFromSuperclass(Class<?> type, int index) {
        ParameterizedType genericSupertype = (ParameterizedType) type.getGenericSuperclass();
        Type[] genericTypes = genericSupertype.getActualTypeArguments();
        return (Class<T>) genericTypes[index];
    }

}
