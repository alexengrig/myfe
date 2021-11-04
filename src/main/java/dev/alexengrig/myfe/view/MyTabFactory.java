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

package dev.alexengrig.myfe.view;

import dev.alexengrig.myfe.config.FtpConnectionConfig;
import dev.alexengrig.myfe.converter.Converter;
import dev.alexengrig.myfe.converter.Path2FeDirectoryConverter;
import dev.alexengrig.myfe.converter.Path2FeFileConverter;
import dev.alexengrig.myfe.converter.Path2FePathConverter;
import dev.alexengrig.myfe.model.FeDirectory;
import dev.alexengrig.myfe.model.FeFile;
import dev.alexengrig.myfe.model.FePath;
import dev.alexengrig.myfe.repository.ApacheCommonsFtpFileSystemPathRepository;
import dev.alexengrig.myfe.repository.FePathRepository;
import dev.alexengrig.myfe.repository.LocalFileSystemPathRepository;
import dev.alexengrig.myfe.repository.URIFileSystemPathRepository;
import dev.alexengrig.myfe.service.BackgroundExecutorService;
import dev.alexengrig.myfe.service.MyPathService;
import dev.alexengrig.myfe.service.SimplePathService;
import dev.alexengrig.myfe.util.PathUtil;
import dev.alexengrig.myfe.util.swing.BackgroundExecutor;

import java.net.URI;
import java.nio.file.Path;

public class MyTabFactory {

    private final Converter<Path, FeDirectory> directoryConverter = new Path2FeDirectoryConverter();
    private final Converter<Path, FeFile> fileConverter = new Path2FeFileConverter();
    private final Converter<Path, FePath> pathConverter = new Path2FePathConverter(directoryConverter, fileConverter);

    private final BackgroundExecutorService backgroundExecutorService = BackgroundExecutor::execute;

    private MyTab createTab(String title, String tip, String name, FePathRepository repository) {
        MyPathService service = new SimplePathService(name, repository);
        return new MyTab(service, backgroundExecutorService, title, tip);
    }

    public MyTab createDefaultTab() {
        FePathRepository repository = new LocalFileSystemPathRepository(directoryConverter, pathConverter);
        return createTab("This computer", "Your computer", "This computer", repository);
    }

    public MyTab createArchiveTab(Path path) {
        String archiveName = PathUtil.getName(path);
        String title = "Archive: " + archiveName;
        URI uri = URI.create("jar:" + path.toUri());
        FePathRepository repository = new URIFileSystemPathRepository(uri, directoryConverter, pathConverter);
        return createTab(title, PathUtil.getAbsolutePath(path), archiveName, repository);
    }

    public MyTab createFTPTab(FtpConnectionConfig connectionConfig) {
        String title = "FTP: " + connectionConfig.getHost();
        String tip = connectionConfig.getHost() + ":" + connectionConfig.getPort();
        FePathRepository repository = new ApacheCommonsFtpFileSystemPathRepository(connectionConfig);
        return createTab(title, tip, connectionConfig.getHost(), repository);
    }

}
