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

import dev.alexengrig.myfe.model.MyFtpDirectory;
import dev.alexengrig.myfe.model.MyFtpPath;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * FTP client.
 */
public interface MyFtpClient extends AutoCloseable {

    /**
     * Get root children.
     *
     * @return root children
     */
    Stream<MyFtpPath> list();

    /**
     * Get directory children.
     *
     * @param directoryPath directory path
     * @return directory children
     */
    Stream<MyFtpPath> list(String directoryPath);

    /**
     * Get root subdirectories.
     *
     * @return root subdirectories
     */
    Stream<MyFtpDirectory> subdirectories();

    /**
     * Get directory subdirectories.
     *
     * @param directoryPath directory path
     * @return directory subdirectories
     */
    Stream<MyFtpDirectory> subdirectories(String directoryPath);

    /**
     * Get file input stream.
     *
     * @param path file path
     * @return file input stream
     */
    InputStream inputStream(String path);

}
