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

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * A utility class for {@link java.lang.Throwable}.
 */
public final class ThrowableUtil {

    private ThrowableUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility class");
    }

    /**
     * Get the first exception from the list of exceptions and add others exceptions as suppressed exceptions.
     *
     * @param exceptions the collection of exceptions
     * @param <T>        the type of exception
     * @return the first exception with other exceptions as suppressed exceptions
     */
    public static <T extends Throwable> T compose(Collection<? extends T> exceptions) {
        Iterator<? extends T> iterator = Objects.requireNonNull(exceptions, "The exceptions must not be null").iterator();
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("No exceptions");
        }
        T exception = iterator.next();
        while (iterator.hasNext()) {
            T suppressed = iterator.next();
            exception.addSuppressed(suppressed);
        }
        return exception;
    }

}
