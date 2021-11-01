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
import dev.alexengrig.myfe.exception.InterruptedBackgroundTaskException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BackgroundWorkerTest {

    @Test
    void should_handle_result() throws Exception {
        // Setup
        String result = "Data";
        AtomicReference<String> resultHolder = new AtomicReference<>();
        AtomicReference<TestRuntimeException> exceptionHolder = new AtomicReference<>();
        WaitingBackgroundWorker worker = new WaitingBackgroundWorker(
                () -> result,
                resultHolder::set,
                exceptionHolder::set);
        // Run and wait
        worker.executeAndWait();
        // Check
        assertNotNull(resultHolder.get(), "Result");
        assertSame(result, resultHolder.get(), "Result");
        assertNull(exceptionHolder.get(), "Exception");
    }

    @Test
    void should_handle_exception() throws Exception {
        // Setup
        TestRuntimeException exceptionFromHandler = new TestRuntimeException("expected");
        AtomicReference<String> resultHolder = new AtomicReference<>();
        AtomicReference<TestRuntimeException> exceptionHolder = new AtomicReference<>();
        WaitingBackgroundWorker worker = new WaitingBackgroundWorker(
                () -> {
                    throw exceptionFromHandler;
                },
                resultHolder::set,
                exceptionHolder::set);
        // Run and wait
        worker.executeAndWait();
        // Check
        assertNull(resultHolder.get(), "Result");
        assertNotNull(exceptionHolder.get(), "Exception");
        assertSame(exceptionFromHandler, exceptionHolder.get(), "Exception");
    }

    @Test
    void should_throw_unexpectedException() {
        // Setup
        Exception unexpectedException = new Exception("unexpected");
        AtomicReference<String> resultHolder = new AtomicReference<>();
        AtomicReference<TestRuntimeException> exceptionHolder = new AtomicReference<>();
        WaitingBackgroundWorker worker = new WaitingBackgroundWorker(
                () -> {
                    throw unexpectedException;
                },
                resultHolder::set,
                exceptionHolder::set);
        // Run and wait
        ExecutionBackgroundTaskException exception = assertThrows(
                ExecutionBackgroundTaskException.class, worker::executeAndWait);
        // Check
        assertNull(resultHolder.get(), "Result");
        assertNull(exceptionHolder.get(), "Exception");
        Throwable cause = exception.getCause();
        assertNotNull(cause, "Unexpected exception");
        assertSame(unexpectedException, cause, "Unexpected exception");
    }

    @Test
    void should_throw_exception_from_resultHandler() {
        // Setup
        String result = "Data";
        AtomicReference<String> resultHolder = new AtomicReference<>();
        IllegalArgumentException exceptionFromHandler = new IllegalArgumentException("unexpected");
        AtomicReference<TestRuntimeException> exceptionHolder = new AtomicReference<>();
        WaitingBackgroundWorker worker = new WaitingBackgroundWorker(
                () -> result,
                r -> {
                    resultHolder.set(r);
                    throw exceptionFromHandler;
                },
                exceptionHolder::set);
        // Run and wait
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, worker::executeAndWait);
        // Check
        assertNotNull(resultHolder.get(), "Result");
        assertEquals(result, resultHolder.get(), "Result");
        assertNull(exceptionHolder.get(), "Exception");
        assertSame(exceptionFromHandler, exception, "Exception from handler");
    }

    @Test
    void should_throw_exception_from_exceptionHandler() {
        // Setup
        TestRuntimeException exceptionFromTask = new TestRuntimeException("expected");
        AtomicReference<String> resultHolder = new AtomicReference<>();
        AtomicReference<TestRuntimeException> exceptionHolder = new AtomicReference<>();
        IllegalArgumentException exceptionFromHandler = new IllegalArgumentException("unexpected");
        WaitingBackgroundWorker worker = new WaitingBackgroundWorker(
                () -> {
                    throw exceptionFromTask;
                },
                resultHolder::set,
                cause -> {
                    exceptionHolder.set(cause);
                    throw exceptionFromHandler;
                });
        // Run and wait
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, worker::executeAndWait);
        // Check
        assertNull(resultHolder.get(), "Result");
        assertNotNull(exceptionHolder.get(), "Exception");
        assertSame(exceptionFromTask, exceptionHolder.get(), "Exception");
        assertSame(exceptionFromHandler, exception, "Exception from handler");
    }

    //FIXME: How do interrupt?
    @Disabled
    @Test
    void should_throw_interruptedException() {
        WaitingBackgroundWorker worker = new WaitingBackgroundWorker(() -> {
            throw new InterruptedException();
        }, Objects::hash, Objects::hash);
        assertThrows(InterruptedBackgroundTaskException.class, worker::executeAndWait);
    }

    static class TestRuntimeException
            extends RuntimeException {

        public TestRuntimeException(String message) {
            super(message);
        }

    }

    static class WaitingBackgroundWorker
            extends BackgroundWorker<String, Void, TestRuntimeException> {

        private final CountDownLatch onDone = new CountDownLatch(1);
        private final AtomicReference<Exception> exceptionHolder = new AtomicReference<>();

        WaitingBackgroundWorker(
                Callable<String> backgroundTask, Consumer<String> resultHandler,
                Consumer<TestRuntimeException> exceptionHandler) {
            super(backgroundTask, resultHandler, exceptionHandler);
        }

        void executeAndWait() throws Exception {
            execute();
            onDone.await();
            Exception exception = exceptionHolder.get();
            if (exception != null) {
                throw exception;
            }
        }

        @Override
        protected void onFailDone(Exception cause) {
            exceptionHolder.set(cause);
        }

        @Override
        protected void onAfterDone() {
            onDone.countDown();
        }

    }

    @Nested
    class InheritanceTest {

        @Test
        void should_check_invalidImplementation() {
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    new InvalidBackgroundWorker<>(
                            () -> {
                                throw new IOException("expected");
                            }, Assertions::fail, Assertions::fail));
            assertEquals("The X must be a particular type, not generic - MY_EXCEPTION; " +
                            "otherwise inherit from: dev.alexengrig.myfe.util.swing.BackgroundWorker.WithExceptionType",
                    exception.getMessage(), "Message of exception");
        }

        class InvalidBackgroundWorker<MY_EXCEPTION extends IOException>
                extends BackgroundWorker<String, Void, MY_EXCEPTION> {

            InvalidBackgroundWorker(
                    Callable<String> backgroundTask, Consumer<String> resultHandler,
                    Consumer<MY_EXCEPTION> exceptionHandler) {
                super(backgroundTask, resultHandler, exceptionHandler);
            }

        }
    }

}