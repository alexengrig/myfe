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

package dev.alexengrig.myfe.util.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LazyLoggerTest {

    @Mock
    Logger delegate;

    @InjectMocks
    LazyLogger logger;

    @Test
    void should_log_debug() {
        // setup
        when(delegate.isDebugEnabled()).thenReturn(true);
        AtomicInteger counter = new AtomicInteger();
        // run
        logger.debug(m -> m.log("Counter: " + counter.getAndIncrement()));
        // check
        assertEquals(1, counter.get(), "Counter");
    }

    @Test
    void shouldNot_log_debug() {
        // setup
        when(delegate.isDebugEnabled()).thenReturn(false);
        AtomicInteger counter = new AtomicInteger();
        // run
        logger.debug(m -> m.log("Counter: " + counter.getAndIncrement()));
        // check
        assertEquals(0, counter.get(), "Counter");
    }

    //TODO: Add other tests

}