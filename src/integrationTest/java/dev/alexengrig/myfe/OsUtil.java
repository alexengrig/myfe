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

package dev.alexengrig.myfe;

import java.util.Objects;

public final class OsUtil {

    private static final String OS = Objects.requireNonNull(System.getProperty("os.name"), "No property \"os.name\"").toLowerCase();
    private static final boolean IS_WINDOWS = OS.startsWith("windows");

    private OsUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility class");
    }

    public static boolean isWindows() {
        return IS_WINDOWS;
    }

}
