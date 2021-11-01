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

package dev.alexengrig.myfe.util.swing;

import dev.alexengrig.myfe.exception.ExecutionBackgroundTaskException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class BackgroundStreamerTest {

    @Test
    void should_execute_streaming() throws Exception {
        AtomicBoolean closeHolder = new AtomicBoolean();
        AtomicInteger valueHolder = new AtomicInteger();
        WaitingBackgroundStreamer<Integer> streamer = new WaitingBackgroundStreamer<>(
                () -> IntStream.rangeClosed(1, 1_000).boxed().onClose(() -> closeHolder.set(true)),
                integers -> integers.forEach(valueHolder::set));
        streamer.executeAndWait();
        assertTrue(closeHolder.get(), "Stream isn't closed");
        assertEquals(1_000, valueHolder.get(), "Max value");
    }

    @Test
    void should_throw_uncheckedException() {
        WaitingBackgroundStreamer<Integer> streamer = new WaitingBackgroundStreamer<>(
                () -> {
                    throw new RuntimeException();
                },
                numbers -> fail());
        assertThrows(ExecutionBackgroundTaskException.class, streamer::executeAndWait);
    }

    @Test
    void should_throw_checkedException() {
        WaitingBackgroundStreamer<Integer> streamer = new WaitingBackgroundStreamer<>(
                () -> {
                    throw new Exception();
                },
                numbers -> fail());
        assertThrows(ExecutionBackgroundTaskException.class, streamer::executeAndWait);
    }

    @Test
    @Timeout(2)
    void should_cancel_streaming() throws Exception {
        AtomicBoolean closeHolder = new AtomicBoolean();
        AtomicInteger valueHolder = new AtomicInteger();
        WaitingBackgroundStreamer<Integer> streamer = new WaitingBackgroundStreamer<>(
                () -> IntStream.range(1, Integer.MAX_VALUE).boxed().onClose(() -> closeHolder.set(true)),
                integers -> integers.forEach(valueHolder::set));
        new Thread(() -> {
            try {
                Thread.sleep(1_000L);
            } catch (InterruptedException ignore) {
            }
            streamer.cancelStreaming();
        }).start();
        streamer.executeAndWait();
        assertTrue(closeHolder.get(), "Stream isn't closed");
        assertTrue(valueHolder.get() > 0, "No value");
    }

    static class WaitingBackgroundStreamer<T> extends BackgroundStreamer<T> {

        private final CountDownLatch onDone = new CountDownLatch(1);
        private final AtomicReference<Exception> exceptionHolder = new AtomicReference<>();

        protected WaitingBackgroundStreamer(Callable<Stream<T>> backgroundTask, Consumer<Stream<T>> chunksHandler) {
            super(backgroundTask, chunksHandler);
        }

        @Override
        protected void done() {
            try {
                super.done();
            } catch (Exception e) {
                exceptionHolder.set(e);
            } finally {
                onDone.countDown();
            }
        }

        void executeAndWait() throws Exception {
            execute();
            onDone.await();
            Exception exception = exceptionHolder.get();
            if (exception != null) {
                throw exception;
            }
        }

    }

}