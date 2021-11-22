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
import dev.alexengrig.myfe.repository.ArchiveFileSystemPathRepository;
import dev.alexengrig.myfe.repository.FePathRepository;
import dev.alexengrig.myfe.repository.FtpFileSystemPathRepository;
import dev.alexengrig.myfe.repository.LocalFileSystemPathRepository;
import dev.alexengrig.myfe.service.FePathService;
import dev.alexengrig.myfe.service.SimplePathService;
import dev.alexengrig.myfe.util.FePathUtil;

import java.nio.file.Path;

public class FeTabFactory {

    private final Converter<Path, FeDirectory> directoryConverter = new Path2FeDirectoryConverter();
    private final Converter<Path, FeFile> fileConverter = new Path2FeFileConverter();
    private final Converter<Path, FePath> pathConverter = new Path2FePathConverter(directoryConverter, fileConverter);

    private FeTab createTab(String title, String tip, String name, FePathRepository repository) {
        FePathService service = new SimplePathService(name, repository);
        return new FeTab(title, tip, service);
    }

    public FeTab createDefaultTab() {
        FePathRepository repository = new LocalFileSystemPathRepository(directoryConverter, pathConverter);
        return createTab("This computer", "Your computer", "This computer", repository);
    }

    public FeTab createArchiveTab(String path) {
        String title = getArchiveTabTitle(path);
        String name = FePathUtil.getNameByPath(path);
        FePathRepository repository = new ArchiveFileSystemPathRepository(path, directoryConverter, pathConverter);
        return createTab(title, path, name, repository);
    }

    public String getArchiveTabTitle(String path) {
        String archiveName = FePathUtil.getNameByPath(path);
        return "Archive: " + archiveName;
    }

    public FeTab createFtpTab(FtpConnectionConfig config) {
        String title = getFtpTabTitle(config);
        FePathRepository repository = new FtpFileSystemPathRepository(config, directoryConverter, pathConverter);
        return createTab(title, config.getInfo(), config.getHostAndPort(), repository);
    }

    private String getFtpTabTitle(FtpConnectionConfig config) {
        return "FTP: " + config.getHostAndPort();
    }

}
