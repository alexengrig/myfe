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

import java.io.IOException;

/**
 * Manager of {@link CommonsFtpClient}.
 */
public class CommonsFtpClientManager extends BaseFtpClientManager<CommonsFtpClient> {

    private static final int DEFAULT_CAPACITY = 4;

    public CommonsFtpClientManager(FtpConnectionConfig config) {
        this(DEFAULT_CAPACITY, config);
    }

    public CommonsFtpClientManager(int capacity, FtpConnectionConfig config) {
        this(//TODO: Get from context
                new BlockingFtpClientPool<>(capacity),
                config);
    }

    public CommonsFtpClientManager(FtpClientPool<CommonsFtpClient> pool, FtpConnectionConfig config) {
        super(pool, config);
    }

    @Override
    protected FtpClientFactory<CommonsFtpClient> createClientFactory() {
        return new ManagedClientFactory();
    }

    @Override
    protected void prepareClient(CommonsFtpClient client, FtpConnectionConfig config) throws IOException {
        if (!client.isConnected()) {
            client.connect(config.getHost(), config.getPort());
            client.login(config.getUsername(), config.getPassword());
        }
    }

    @Override
    protected void destroyClient(CommonsFtpClient client) throws IOException {
        ((ManagedClient) client).doClose();
    }

    private class ManagedClient extends CommonsFtpClient {

        public void doClose() throws IOException {
            super.close();
        }

        @Override
        public void close() throws IOException {
            returnToPool(this);
        }

    }

    private class ManagedClientFactory implements FtpClientFactory<CommonsFtpClient> {

        @Override
        public ManagedClient createClient() {
            return new ManagedClient();
        }

    }

}
