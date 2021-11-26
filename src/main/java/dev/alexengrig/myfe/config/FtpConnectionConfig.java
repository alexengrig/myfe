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

import java.util.Objects;

/**
 * Configuration of FTP connection.
 */
public class FtpConnectionConfig {

    public static final int DEFAULT_PORT = 21;
    public static final String ANONYMOUS_USERNAME = "anonymous";

    private static final char[] DEFAULT_PASSWORD = new char[]{};

    private final String host;
    private final int port;
    private final String username;
    private final char[] password;

    private transient String info;

    public FtpConnectionConfig(String host, int port, String username, char[] password) {
        this.host = Objects.requireNonNull(host, "The host must not be null");
        this.port = requirePositivePort(port);
        this.username = Objects.requireNonNull(username, "The username must not be null");
        this.password = Objects.requireNonNull(password, "The password must not be null");
    }

    private static int requirePositivePort(int port) {
        if (port < 0) {
            throw new IllegalArgumentException("The port must be positive number");
        }
        return port;
    }

    public static FtpConnectionConfig anonymous(String host) {
        return anonymous(host, DEFAULT_PORT);
    }

    public static FtpConnectionConfig anonymous(String host, int port) {
        return new FtpConnectionConfig(host, port, ANONYMOUS_USERNAME, DEFAULT_PASSWORD);
    }

    public static FtpConnectionConfig user(String host, String username, char[] password) {
        return user(host, DEFAULT_PORT, username, password);
    }

    public static FtpConnectionConfig user(String host, int port, String username, char[] password) {
        return new FtpConnectionConfig(host, port, username, password);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }

    /**
     * Get info: {@code host:port by username}.
     *
     * @return {@code host:port by username}
     */
    public String getInfo() {
        if (info == null) {
            info = host + ":" + port + " by " + username;
        }
        return info;
    }

    /**
     * Get host and port: {@code host:port}.
     *
     * @return {@code host:port}
     */
    public String getHostAndPort() {
        return host + ":" + port;
    }

    @Override
    public String toString() {
        return "FtpConnectionConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + (password.length == 0 ? "absent" : "present") + '\'' +
                '}';
    }

}
