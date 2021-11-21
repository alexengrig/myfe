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

package dev.alexengrig.myfe.repository;

import com.github.robtimus.filesystems.ftp.ConnectionMode;
import com.github.robtimus.filesystems.ftp.FTPEnvironment;
import dev.alexengrig.myfe.config.FtpConnectionConfig;
import dev.alexengrig.myfe.converter.Converter;
import dev.alexengrig.myfe.model.FeDirectory;
import dev.alexengrig.myfe.model.FePath;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

/**
 * {@link com.github.robtimus.filesystems.ftp.FTPFileSystemProvider}-based implementation.
 */
public class FtpFileSystemPathRepository extends URIFileSystemPathRepository {

    private static final FTPEnvironment BASE_ENV = new FTPEnvironment()
            .withConnectionMode(ConnectionMode.PASSIVE)
            .withConnectTimeout(10_000);

    public FtpFileSystemPathRepository(
            FtpConnectionConfig config,
            Converter<Path, FeDirectory> directoryConverter,
            Converter<Path, FePath> pathConverter) {
        super(createUri(config), createEnvironment(config), directoryConverter, pathConverter);
    }

    private static URI createUri(FtpConnectionConfig config) {
        return URI.create("ftp://" + config.getHostAndPort());
    }

    private static Map<String, Object> createEnvironment(FtpConnectionConfig config) {
        return new FTPEnvironment(BASE_ENV)
                .withCredentials(config.getUsername(), config.getPassword());
    }

}
