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

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DelayedSingleTaskExecutorTest {

    @Test
    void should_not_repeat() throws InterruptedException {
        int delay = 50;
        DelayedSingleTaskExecutor executor = new DelayedSingleTaskExecutor(delay);
        AtomicInteger counter = new AtomicInteger();
        executor.execute(counter::incrementAndGet);
        Thread.sleep(delay * 3);
        assertEquals(1, counter.get(), "Counter");
    }

    @Test
    void should_execute_severalTimes() throws InterruptedException {
        int delay = 1000;
        DelayedSingleTaskExecutor executor = new DelayedSingleTaskExecutor(delay);
        AtomicInteger counter = new AtomicInteger();
        executor.execute(counter::incrementAndGet);
        Thread.sleep(delay + delay / 2);
        executor.execute(counter::incrementAndGet);
        Thread.sleep(delay + delay / 2);
        executor.execute(counter::incrementAndGet);
        Thread.sleep(delay + 200);
        assertEquals(3, counter.get(), "Counter");
    }

    @Test
    void should_execute_onlyTime() throws InterruptedException {
        int delay = 1000;
        DelayedSingleTaskExecutor executor = new DelayedSingleTaskExecutor(delay);
        AtomicInteger counter = new AtomicInteger();
        executor.execute(counter::incrementAndGet);
        Thread.sleep(delay / 4);
        executor.execute(counter::incrementAndGet);
        Thread.sleep(delay / 4);
        executor.execute(counter::incrementAndGet);
        Thread.sleep(delay + 200);
        assertEquals(1, counter.get(), "Counter");
    }

}