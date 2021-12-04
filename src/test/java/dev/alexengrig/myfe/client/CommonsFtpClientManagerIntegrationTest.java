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

import dev.alexengrig.myfe.WithUnixFtpServer;
import dev.alexengrig.myfe.config.FtpConnectionConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommonsFtpClientManagerIntegrationTest extends WithUnixFtpServer {

    static final int CAPACITY = 4;

    @Spy
    FtpClientPool<CommonsFtpClient> pool = new BlockingFtpClientPool<>(CAPACITY);
    FtpConnectionConfig config;

    CommonsFtpClientManager manager;

    static Stream<Arguments> provide_numberOfCalls_numberOfThreads_expectedNumberOfCreation() {
        return Stream.of(
                Arguments.of(10, CAPACITY, CAPACITY),
                Arguments.of(10, CAPACITY - 1, CAPACITY - 1)
        );
    }

    @BeforeEach
    void beforeEach() {
        setup();
        config = FtpConnectionConfig.user(host, port, username, password.toCharArray());
        manager = new CommonsFtpClientManager(pool, config);
    }

    @AfterEach
    void afterEach() throws IOException {
        manager.close();
        tearDown();
    }

    @Test
    void should_takeOne_and_putOne() throws Exception {
        assertEquals(0, pool.size(), "Pool size");
        // get first client
        CommonsFtpClient client = manager.getClient();
        verify(pool).put(same(client));
        verify(pool).take();
        client.close();
        verify(pool, times(2)).put(same(client));
        // get same client
        CommonsFtpClient sameClient = manager.getClient();
        assertSame(client, sameClient, "Client");
        verify(pool, times(2)).put(same(client));
        verify(pool, times(2)).take();
        client.close();
        verify(pool, times(3)).put(same(client));
        verify(pool, never()).put(not(same(client)));
        assertEquals(1, pool.size(), "Pool size");
    }

    @Test
    void should_takeAll_and_putAll() throws Exception {
        assertEquals(0, pool.size(), "Pool size");
        // get all clients
        ArrayList<CommonsFtpClient> clients = new ArrayList<>(CAPACITY);
        for (int i = 0; i < CAPACITY; i++) {
            CommonsFtpClient client = manager.getClient();
            clients.add(client);
        }
        verify(pool, times(CAPACITY)).put(any(CommonsFtpClient.class));
        verify(pool, times(CAPACITY)).take();
        // give all clients
        for (CommonsFtpClient client : clients) {
            client.close();
        }
        verify(pool, times(CAPACITY * 2)).put(any(CommonsFtpClient.class));
        assertEquals(CAPACITY, pool.size(), "Pool size");
    }

    @ParameterizedTest
    @MethodSource("provide_numberOfCalls_numberOfThreads_expectedNumberOfCreation")
    void should_get_clients_in_severalThreads(int numberOfCalls, int numberOfThreads, int expectedNumberOfCreation) throws Exception {
        // setup
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch onStart = new CountDownLatch(1);
        Callable<?> task = () -> {
            onStart.await();
            for (int i = 0; i < numberOfCalls; i++) {
                CommonsFtpClient client = manager.getClient();
                client.close();
            }
            return null;
        };
        ArrayList<Future<?>> futures = new ArrayList<>(numberOfThreads);
        // run
        for (int i = 0; i < numberOfThreads; i++) {
            Future<?> future = executorService.submit(task);
            futures.add(future);
        }
        onStart.countDown();
        // wait
        executorService.shutdown();
        for (Future<?> future : futures) {
            future.get();
        }
        // check
        int totalNumberOfGetting = numberOfCalls * numberOfThreads;
        verify(pool, times(totalNumberOfGetting + expectedNumberOfCreation)).put(any(CommonsFtpClient.class));
        verify(pool, times(totalNumberOfGetting)).take();
    }

}