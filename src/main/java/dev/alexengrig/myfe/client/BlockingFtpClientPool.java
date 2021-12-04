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

package dev.alexengrig.myfe.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingFtpClientPool<T extends FtpClient> implements FtpClientPool<T> {

    private final int capacity;
    private final BlockingQueue<T> pool;

    public BlockingFtpClientPool(int capacity) {
        this(capacity, new ArrayBlockingQueue<>(capacity));
    }

    protected BlockingFtpClientPool(int capacity, BlockingQueue<T> pool) {
        this.capacity = requirePositiveAndNonZeroCapacity(capacity);
        this.pool = Objects.requireNonNull(pool, "The pool must not be null");
    }

    private static int requirePositiveAndNonZeroCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("The capacity must be greater than 0: " + capacity);
        }
        return capacity;
    }

    @Override
    public T take() throws InterruptedException {
        return pool.take();
    }

    @Override
    public void put(T client) {
        pool.add(client);
    }

    @Override
    public boolean isEmpty() {
        return pool.isEmpty();
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int size() {
        return pool.size();
    }

    @Override
    public List<T> clear() {
        LinkedList<T> drained = new LinkedList<>();
        pool.drainTo(drained);
        return drained;
    }

}
