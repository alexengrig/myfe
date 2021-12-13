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

import dev.alexengrig.myfe.domain.FtpConnectionConfig;
import dev.alexengrig.myfe.exception.UncheckedInterruptedException;
import dev.alexengrig.myfe.util.ThrowableUtil;
import dev.alexengrig.myfe.util.logging.LazyLogger;
import dev.alexengrig.myfe.util.logging.LazyLoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Base implementation of {@link FtpClientManager}.
 *
 * @param <T>
 */
public abstract class BaseFtpClientManager<T extends FtpClient> implements FtpClientManager<T> {

    private static final LazyLogger LOGGER = LazyLoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AtomicReference<ClientObtainingStrategy<T>> obtainingStrategyHolder;
    private final AtomicReference<ClientComebackStrategy<T>> comebackStrategyHolder;
    private final FtpClientPool<T> pool;
    private final FtpConnectionConfig config;

    protected BaseFtpClientManager(FtpClientPool<T> pool, FtpConnectionConfig config) {
        FtpClientFactory<T> factory = createClientFactory();
        this.obtainingStrategyHolder = new AtomicReference<>(new UnfilledPoolObtainingStrategy(factory));
        this.comebackStrategyHolder = new AtomicReference<>(new Return2PoolComebackStrategy());
        this.pool = pool;
        this.config = config;
    }

    protected abstract FtpClientFactory<T> createClientFactory();

    @Override
    public T getClient() throws IOException {
        LOGGER.debug("Start getting client");
        ClientObtainingStrategy<T> strategy = obtainingStrategyHolder.get();
        LOGGER.debug("Using strategy: {}", strategy);
        T client = strategy.obtain();
        LOGGER.debug("Got client: {}", client);
        prepareClient(client, config);
        LOGGER.debug("Prepared client: {}", client);
        return client;
    }

    protected abstract void prepareClient(T client, FtpConnectionConfig config) throws IOException;

    protected abstract void destroyClient(T client) throws IOException;

    protected void returnToPool(T client) {
        ClientComebackStrategy<T> strategy = comebackStrategyHolder.get();
        strategy.comeBack(client);
    }

    private void replaceWithFilledPoolObtainingStrategy(ClientObtainingStrategy<T> previousStrategy) {
        FilledPoolObtainingStrategy newStrategy = new FilledPoolObtainingStrategy();
        if (obtainingStrategyHolder.compareAndSet(previousStrategy, newStrategy)) {
            LOGGER.debug("Replaced obtaining strategy of client {} with {}", previousStrategy, newStrategy);
        } else {
            LOGGER.debug("Failed to replace obtaining strategy of client {} with {}", previousStrategy, newStrategy);
        }
    }

    private void replaceWithRejectionObtainingStrategy(ClientObtainingStrategy<T> previousStrategy) {
        RejectionObtainingStrategy newStrategy = new RejectionObtainingStrategy();
        if (obtainingStrategyHolder.compareAndSet(previousStrategy, newStrategy)) {
            LOGGER.debug("Replaced obtaining strategy of client {} with {}", previousStrategy, newStrategy);
        } else {
            LOGGER.debug("Failed to replace obtaining strategy of client {} with {}", previousStrategy, newStrategy);
        }
    }

    private void replaceWithDestroyComebackStrategy(ClientComebackStrategy<T> previousStrategy) {
        DestroyComebackStrategy newStrategy = new DestroyComebackStrategy();
        if (comebackStrategyHolder.compareAndSet(previousStrategy, newStrategy)) {
            LOGGER.debug("Replaced comeback strategy of client {} with {}", previousStrategy, newStrategy);
        } else {
            LOGGER.debug("Failed to replace comeback strategy of client {} with {}", previousStrategy, newStrategy);
        }
    }

    @Override
    public void close() throws IOException {
        replaceWithRejectionObtainingStrategy(obtainingStrategyHolder.get());
        replaceWithDestroyComebackStrategy(comebackStrategyHolder.get());
        destroyClients();
    }

    private void destroyClients() throws IOException {
        List<T> clients = pool.clear();
        LOGGER.debug("Destroy clients: {}", clients);
        List<IOException> exceptions = null;
        for (T client : clients) {
            try {
                destroyClient(client);
            } catch (IOException exception) {
                if (exceptions == null) {
                    exceptions = new LinkedList<>();
                }
                exceptions.add(exception);
            }
        }
        if (exceptions != null) {
            IOException exception = ThrowableUtil.compose(exceptions);
            LOGGER.error("Exception of destroying", exception);
            throw exception;
        }
    }

    private interface ClientObtainingStrategy<T extends FtpClient> {

        T obtain();

    }

    private interface ClientComebackStrategy<T extends FtpClient> {

        void comeBack(T client);

    }

    private class FilledPoolObtainingStrategy implements ClientObtainingStrategy<T> {

        @Override
        public T obtain() {
            try {
                return pool.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new UncheckedInterruptedException(e);
            }
        }

    }

    private class UnfilledPoolObtainingStrategy extends FilledPoolObtainingStrategy {

        private final AtomicInteger creationCounter = new AtomicInteger();
        private final Lock creationLock = new ReentrantLock();

        private final FtpClientFactory<T> factory;

        public UnfilledPoolObtainingStrategy(FtpClientFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T obtain() {
            if (needPutNewClientToPool()) {
                putNewClientToPool();
            }
            return super.obtain();
        }

        private boolean needPutNewClientToPool() {
            if (!pool.isEmpty()) {
                LOGGER.debug("Pool isn't empty");
                return false;
            }
            int creationCount = creationCounter.get();
            int poolCapacity = pool.capacity();
            boolean result = creationCount < poolCapacity;
            LOGGER.debug("Check that need new client with creationCount={}, poolCapacity={}: {}",
                    creationCount, poolCapacity, result);
            return result;
        }

        private void putNewClientToPool() {
            creationLock.lock();
            try {
                if (needPutNewClientToPool()) {
                    T client = factory.createClient();
                    LOGGER.debug("Created new client: {}", client);
                    pool.put(client);
                    LOGGER.debug("Put new client to pool: {}", client);
                    creationCounter.incrementAndGet();
                    if (isFilledPool()) {
                        replaceWithFilledPoolObtainingStrategy(this);
                    }
                }
            } finally {
                creationLock.unlock();
            }
        }

        private boolean isFilledPool() {
            int creationCount = creationCounter.get();
            int poolCapacity = pool.capacity();
            boolean result = creationCount >= poolCapacity;
            LOGGER.debug("Check that pool is filled with creationCount={}, poolCapacity={}: {}",
                    creationCount, poolCapacity, result);
            return result;
        }

        @Override
        public String toString() {
            return "UnfilledPoolStrategy{" +
                    "creationCounter=" + creationCounter +
                    ", factory=" + factory +
                    '}';
        }

    }

    private class RejectionObtainingStrategy implements ClientObtainingStrategy<T> {

        @Override
        public T obtain() {
            throw new IllegalStateException("FTP client manager is closed");
        }

    }

    private class Return2PoolComebackStrategy implements ClientComebackStrategy<T> {

        @Override
        public void comeBack(T client) {
            LOGGER.debug("Return client to pool: {}", client);
            pool.put(client);
        }

    }

    private class DestroyComebackStrategy implements ClientComebackStrategy<T> {

        @Override
        public void comeBack(T client) {
            try {
                destroyClient(client);
                LOGGER.error("Destroyed client: {}", client);
            } catch (IOException e) {
                LOGGER.warn("Exception of destroying client: {}", client, e);
            }
        }

    }

}
