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

package dev.alexengrig.myfe.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FtpConnectionConfigTest {

    final FtpConnectionConfig config = new FtpConnectionConfig("host", 1, "user", "pass".toCharArray());

    @Test
    void should_return_info() {
        assertEquals("host:1 by user", config.getInfo(), "Info");
    }

    @Test
    void should_return_hostAndPort() {
        assertEquals("host:1", config.getHostAndPort(), "Host and port");
    }

}