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

import dev.alexengrig.myfe.config.FtpConnectionConfig;
import dev.alexengrig.myfe.domain.FtpDirectory;
import dev.alexengrig.myfe.domain.FtpPath;
import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Description;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.III_Result;
import org.openjdk.jcstress.infra.results.II_Result;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaseFtpClientManager_JCStressTest {

    @JCStressTest
    @Description("2 clients for 2 actors")
    @Outcome(id = {"1, 1", "1, 2", "2, 1", "2, 2"}, expect = Expect.ACCEPTABLE, desc = "Expected variants")
    @Outcome(expect = Expect.ACCEPTABLE_INTERESTING, desc = "Other")
    @State
    public static class TwoClientsForTwoActors {

        private static final int NUMBER_OF_CLIENTS = 2;
        private static final int NUMBER_OF_CALLS = 10;

        private final BaseFtpClientManager<FtpClient> manager = new DoNothingFtpClientManager(NUMBER_OF_CLIENTS);

        private void fillClients(Set<FtpClient> clients) throws IOException {
            for (int i = 0; i < NUMBER_OF_CALLS; i++) {
                FtpClient client = manager.getClient();
                clients.add(client);
                client.close();
            }
        }

        @Actor
        public void actor1(II_Result r) {
            Set<FtpClient> clients = new HashSet<>();
            try {
                fillClients(clients);
                r.r1 = clients.size();
            } catch (IOException e) {
                r.r1 = -1;
            }
        }

        @Actor
        public void actor2(II_Result r) {
            Set<FtpClient> clients = new HashSet<>();
            try {
                fillClients(clients);
                r.r2 = clients.size();
            } catch (IOException e) {
                r.r2 = -1;
            }
        }

    }

    @JCStressTest
    @Description("2 clients for 3 actors")
    @Outcome(id = {
            "1, 1, 1",
            "2, 1, 1",
            "2, 2, 1",
            "1, 2, 1",
            "2, 1, 2",
            "1, 1, 2",
            "1, 2, 2",
            "2, 2, 2"
    }, expect = Expect.ACCEPTABLE, desc = "Expected variants")
    @Outcome(expect = Expect.ACCEPTABLE_INTERESTING, desc = "Other")
    @State
    public static class TwoClientsForThreeActors {

        private static final int NUMBER_OF_CLIENTS = 2;
        private static final int NUMBER_OF_CALLS = 10;

        private final BaseFtpClientManager<FtpClient> manager = new DoNothingFtpClientManager(NUMBER_OF_CLIENTS);

        private void fillClients(Set<FtpClient> clients) throws IOException {
            for (int i = 0; i < NUMBER_OF_CALLS; i++) {
                FtpClient client = manager.getClient();
                clients.add(client);
                client.close();
            }
        }

        @Actor
        public void actor1(III_Result r) {
            Set<FtpClient> clients = new HashSet<>();
            try {
                fillClients(clients);
                r.r1 = clients.size();
            } catch (IOException e) {
                r.r1 = -1;
            }
        }

        @Actor
        public void actor2(III_Result r) {
            Set<FtpClient> clients = new HashSet<>();
            try {
                fillClients(clients);
                r.r2 = clients.size();
            } catch (IOException e) {
                r.r2 = -1;
            }
        }

        @Actor
        public void actor3(III_Result r) {
            Set<FtpClient> clients = new HashSet<>();
            try {
                fillClients(clients);
                r.r3 = clients.size();
            } catch (IOException e) {
                r.r3 = -1;
            }
        }

    }

    @JCStressTest
    @Description("3 clients for 2 actors")
    @Outcome(id = {"1, 1", "1, 2", "2, 1", "2, 2"}, expect = Expect.ACCEPTABLE, desc = "Expected variants")
    @Outcome(expect = Expect.ACCEPTABLE_INTERESTING, desc = "Other")
    @State
    public static class ThreeClientsForTwoActors {

        private static final int NUMBER_OF_CLIENTS = 3;
        private static final int NUMBER_OF_CALLS = 10;

        private final BaseFtpClientManager<FtpClient> manager = new DoNothingFtpClientManager(NUMBER_OF_CLIENTS);

        private void fillClients(Set<FtpClient> clients) throws IOException {
            for (int i = 0; i < NUMBER_OF_CALLS; i++) {
                FtpClient client = manager.getClient();
                clients.add(client);
                client.close();
            }
        }

        @Actor
        public void actor1(II_Result r) {
            Set<FtpClient> clients = new HashSet<>();
            try {
                fillClients(clients);
                r.r1 = clients.size();
            } catch (IOException e) {
                r.r1 = -1;
            }
        }

        @Actor
        public void actor2(II_Result r) {
            Set<FtpClient> clients = new HashSet<>();
            try {
                fillClients(clients);
                r.r2 = clients.size();
            } catch (IOException e) {
                r.r2 = -1;
            }
        }

    }

    static class DoNothingFtpClientManager extends BaseFtpClientManager<FtpClient> {

        public DoNothingFtpClientManager(int capacity) {
            super(new BlockingFtpClientPool<>(capacity), FtpConnectionConfig.anonymous("fake-host"));
        }

        @Override
        protected FtpClientFactory<FtpClient> createClientFactory() {
            return () -> new DoNothingFtpClient() {
                @Override
                public void close() {
                    returnToPool(this);
                }
            };
        }

        @Override
        protected void prepareClient(FtpClient client, FtpConnectionConfig config) {
            // do nothing
        }
    }

    static class DoNothingFtpClient implements FtpClient {

        @Override
        public void connect(String host, int port) {
        }

        @Override
        public boolean isConnected() {
            return true;
        }

        @Override
        public void login(String username, char[] password) {
        }

        @Override
        public void login(String username, String password) {
        }

        @Override
        public void disconnect() {
        }

        @Override
        public List<FtpDirectory> listRootDirectories() {
            return null;
        }

        @Override
        public List<FtpDirectory> listSubdirectories(String path) {
            return null;
        }

        @Override
        public List<FtpPath> listChildren(String path) {
            return null;
        }

        @Override
        public InputStream retrieveFileStream(String path) {
            return null;
        }

        @Override
        public void close() {
        }

    }

}
