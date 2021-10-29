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

import dev.alexengrig.myfe.client.ApacheCommonsFtpClientFactory;
import dev.alexengrig.myfe.config.FTPConnectionConfig;
import dev.alexengrig.myfe.converter.Converter;
import dev.alexengrig.myfe.converter.Path2MyDirectoryConverter;
import dev.alexengrig.myfe.converter.Path2MyFileConverter;
import dev.alexengrig.myfe.converter.Path2MyPathConverter;
import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyFile;
import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.repository.FTPPathRepository;
import dev.alexengrig.myfe.repository.LocalFileSystemPathRepository;
import dev.alexengrig.myfe.repository.MyPathRepository;
import dev.alexengrig.myfe.repository.URIFileSystemPathRepository;
import dev.alexengrig.myfe.service.BackgroundExecutorService;
import dev.alexengrig.myfe.service.MyPathService;
import dev.alexengrig.myfe.service.SimplePathService;
import dev.alexengrig.myfe.util.BackgroundExecutor;
import dev.alexengrig.myfe.util.PathUtil;

import java.net.URI;
import java.nio.file.Path;

public class MyTabFactory {

    private final Converter<Path, MyDirectory> directoryConverter = new Path2MyDirectoryConverter();
    private final Converter<Path, MyFile> fileConverter = new Path2MyFileConverter();
    private final Converter<Path, MyPath> pathConverter = new Path2MyPathConverter(directoryConverter, fileConverter);

    private final BackgroundExecutorService backgroundExecutorService = BackgroundExecutor::execute;

    private MyTab createTab(String title, String tip, String name, MyPathRepository repository) {
        MyPathService service = new SimplePathService(name, repository);
        return new MyTab(service, backgroundExecutorService, title, tip);
    }

    public MyTab createDefaultTab() {
        MyPathRepository repository = new LocalFileSystemPathRepository(directoryConverter, pathConverter);
        return createTab("This computer", "Your computer", "This computer", repository);
    }

    public MyTab createArchiveTab(Path path) {
        String archiveName = PathUtil.getName(path);
        String title = "Archive: " + archiveName;
        URI uri = URI.create("jar:" + path.toUri());
        MyPathRepository repository = new URIFileSystemPathRepository(uri, directoryConverter, pathConverter);
        return createTab(title, PathUtil.getAbsolutePath(path), archiveName, repository);
    }

    public MyTab createFTPTab(FTPConnectionConfig connectionConfig) {
        String title = "FTP: " + connectionConfig.getHost();
        String tip = connectionConfig.getHost() + ":" + connectionConfig.getPort();
        ApacheCommonsFtpClientFactory clientFactory = new ApacheCommonsFtpClientFactory(connectionConfig);
        MyPathRepository repository = new FTPPathRepository(clientFactory);
        return createTab(title, tip, connectionConfig.getHost(), repository);
    }

}
