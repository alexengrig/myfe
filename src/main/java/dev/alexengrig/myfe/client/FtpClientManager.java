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

import java.io.Closeable;
import java.io.IOException;

/**
 * Manager of {@link FtpClient}.
 *
 * @param <T> the type of FTP client
 */
public interface FtpClientManager<T extends FtpClient> extends Closeable {

    /**
     * Get FTP client.
     *
     * <pre>{@code
     * try (FtpClient client = ftpClientManager.getClient()) {
     *     // work with client
     * } catch (IOException exception) {
     *     // process exception
     * }
     * }</pre>
     *
     * @return FTP client
     * @throws IOException while preparing FTP client
     */
    T getClient() throws IOException;

}
