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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("ResultOfMethodCallIgnored")
class CloseOnTerminalOperationStreamsTest {

    AtomicBoolean closeHolder = new AtomicBoolean(false);

    Stream<String> stream() {
        return Stream.of("1", "2", "3").onClose(() -> closeHolder.set(true));
    }

    Stream<String> wrappedStream() {
        return CloseOnTerminalOperationStreams.wrap(stream());
    }

    @Test
    void testWithoutTryWithResources() {
        Stream<String> stream = stream();
        assertFalse(closeHolder.get(), "It's closed");
        stream.count();
        assertFalse(closeHolder.get(), "It's closed");
        stream.close();
        assertTrue(closeHolder.get(), "It isn't closed");
    }

    @Test
    void testWithTryWithResources() {
        try (Stream<String> ignore = stream()) {
            assertFalse(closeHolder.get(), "It's closed");
        }
        assertTrue(closeHolder.get(), "It isn't closed");
    }

    @Test
    void testWrap() {
        Stream<String> stream = wrappedStream();
        assertFalse(closeHolder.get(), "It's closed");
        stream.count();
        assertTrue(closeHolder.get(), "It isn't closed");
    }

    //TODO: Add other tests

}