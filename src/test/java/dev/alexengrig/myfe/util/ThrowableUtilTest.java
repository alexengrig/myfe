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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ThrowableUtilTest {

    @Test
    void should_compose_exceptions() {
        Throwable main = new Throwable();
        Throwable firstSuppressed = new Throwable();
        Throwable secondSuppressed = new Throwable();
        List<Throwable> exceptions = List.of(main, firstSuppressed, secondSuppressed);
        Throwable exception = ThrowableUtil.compose(exceptions);
        assertSame(main, exception, "Main exception");
        assertArrayEquals(new Throwable[]{firstSuppressed, secondSuppressed}, exception.getSuppressed(), "Suppressed exceptions");
    }

}