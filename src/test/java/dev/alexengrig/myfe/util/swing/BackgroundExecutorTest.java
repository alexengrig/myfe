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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BackgroundExecutorTest {

    @Test
    @Timeout(4)
    void should_execute_resultHandler() throws InterruptedException {
        // setup
        String expectedResult = "Just do it";
        AtomicBoolean beforeHookCalled = new AtomicBoolean();
        AtomicReference<String> resultHolder = new AtomicReference<>();
        AtomicReference<Throwable> errorHolder = new AtomicReference<>();
        AtomicBoolean afterHookCalled = new AtomicBoolean();
        CountDownLatch latch = new CountDownLatch(1);
        // run
        BackgroundExecutor.builder(() -> expectedResult)
                .withBeforeHook(() -> beforeHookCalled.set(true))
                .withResultHandler(resultHolder::set)
                .withErrorHandler(errorHolder::set)
                .withAfterHook(() -> {
                    afterHookCalled.set(true);
                    latch.countDown();
                })
                .execute();
        // wait
        latch.await();
        // check
        assertTrue(beforeHookCalled.get(), "Before hook didn't call");
        assertSame(expectedResult, resultHolder.get(), "Result");
        assertNull(errorHolder.get(), "Error");
        assertTrue(afterHookCalled.get(), "After hook didn't call");
    }

    @Test
    @Timeout(4)
    void should_execute_errorHandler() throws InterruptedException {
        // setup
        Exception expectedError = new Exception("I will not do it");
        AtomicBoolean beforeHookCalled = new AtomicBoolean();
        AtomicReference<String> resultHolder = new AtomicReference<>();
        AtomicReference<Throwable> errorHolder = new AtomicReference<>();
        AtomicBoolean afterHookCalled = new AtomicBoolean();
        CountDownLatch latch = new CountDownLatch(1);
        // run
        BackgroundExecutor.<String>builder(() -> {
                    throw expectedError;
                })
                .withBeforeHook(() -> beforeHookCalled.set(true))
                .withResultHandler(resultHolder::set)
                .withErrorHandler(errorHolder::set)
                .withAfterHook(() -> {
                    afterHookCalled.set(true);
                    latch.countDown();
                })
                .execute();
        // wait
        latch.await();
        // check
        assertTrue(beforeHookCalled.get(), "Before hook didn't call");
        assertNull(resultHolder.get(), "Result");
        assertSame(expectedError, errorHolder.get(), "Error");
        assertTrue(afterHookCalled.get(), "After hook didn't call");
    }

    @Test
    @Timeout(10)
    void should_cancel_backgroundTask() throws InterruptedException {
        // setup
        String expectedResult = "Just do it";
        AtomicBoolean beforeHookCalled = new AtomicBoolean();
        AtomicReference<String> resultHolder = new AtomicReference<>();
        AtomicReference<Throwable> errorHolder = new AtomicReference<>();
        AtomicBoolean afterHookCalled = new AtomicBoolean();
        CountDownLatch onStart = new CountDownLatch(1);
        CountDownLatch onEnd = new CountDownLatch(1);
        // run
        BackgroundTask task = BackgroundExecutor.builder(() -> {
                    onStart.countDown();
                    Thread.sleep(2_000L);
                    onEnd.countDown();
                    return expectedResult;
                })
                .withBeforeHook(() -> beforeHookCalled.set(true))
                .withResultHandler(resultHolder::set)
                .withErrorHandler(errorHolder::set)
                .withAfterHook(() -> afterHookCalled.set(true))
                .execute();
        onStart.await();
        assertTrue(task.cancel(), "Didn't canceled");
        assertTrue(onEnd.await(4_000L, TimeUnit.MILLISECONDS), "Task didn't end");
        // check
        assertTrue(beforeHookCalled.get(), "Before hook didn't call");
        assertNull(resultHolder.get(), "Result");
        assertNull(errorHolder.get(), "Error");
        assertTrue(afterHookCalled.get(), "After hook didn't call");
    }

    @Test
    @Timeout(8)
    void should_cancelNow_backgroundTask() throws InterruptedException {
        // setup
        AtomicBoolean beforeHookCalled = new AtomicBoolean();
        AtomicReference<String> resultHolder = new AtomicReference<>();
        AtomicReference<Throwable> errorHolder = new AtomicReference<>();
        AtomicBoolean afterHookCalled = new AtomicBoolean();
        CountDownLatch onStart = new CountDownLatch(1);
        CountDownLatch onEnd = new CountDownLatch(1);
        // run
        BackgroundTask task = BackgroundExecutor.builder(() -> {
                    onStart.countDown();
                    Thread.sleep(2_000L);
                    onEnd.countDown();
                    return "Unreachable result";
                })
                .withBeforeHook(() -> beforeHookCalled.set(true))
                .withResultHandler(resultHolder::set)
                .withErrorHandler(errorHolder::set)
                .withAfterHook(() -> afterHookCalled.set(true))
                .execute();
        onStart.await();
        assertTrue(task.cancelNow(), "Didn't canceled");
        assertFalse(onEnd.await(4_000L, TimeUnit.MILLISECONDS), "Task ended");
        // check
        assertTrue(beforeHookCalled.get(), "Before hook didn't call");
        assertNull(resultHolder.get(), "Result");
        assertNull(errorHolder.get(), "Error");
        assertTrue(afterHookCalled.get(), "After hook didn't call");
    }

}