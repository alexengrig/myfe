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

import dev.alexengrig.myfe.converter.Converter;
import dev.alexengrig.myfe.converter.FtpDirectory2FeDirectoryConverter;
import dev.alexengrig.myfe.converter.FtpFile2FeFileConverter;
import dev.alexengrig.myfe.converter.FtpPath2FePathConverter;
import dev.alexengrig.myfe.converter.Path2FeDirectoryConverter;
import dev.alexengrig.myfe.converter.Path2FeFileConverter;
import dev.alexengrig.myfe.converter.Path2FePathConverter;
import dev.alexengrig.myfe.domain.FeDirectory;
import dev.alexengrig.myfe.domain.FeFile;
import dev.alexengrig.myfe.domain.FePath;
import dev.alexengrig.myfe.domain.FtpConnectionConfig;
import dev.alexengrig.myfe.domain.FtpDirectory;
import dev.alexengrig.myfe.domain.FtpFile;
import dev.alexengrig.myfe.domain.FtpPath;
import dev.alexengrig.myfe.repository.ArchiveFileSystemPathRepository;
import dev.alexengrig.myfe.repository.FePathRepository;
import dev.alexengrig.myfe.repository.FtpClientPathRepository;
import dev.alexengrig.myfe.repository.LocalFileSystemPathRepository;
import dev.alexengrig.myfe.service.LocalPathService;
import dev.alexengrig.myfe.service.RemotePathService;
import dev.alexengrig.myfe.util.FePathUtil;

import java.nio.file.Path;

public class FeTabFactory {

    private final Converter<Path, FeDirectory> directoryConverter = new Path2FeDirectoryConverter();
    private final Converter<Path, FeFile> fileConverter = new Path2FeFileConverter();
    private final Converter<Path, FePath> pathConverter = new Path2FePathConverter(directoryConverter, fileConverter);

    private final Converter<FtpDirectory, FeDirectory> ftpDirectoryConverter = new FtpDirectory2FeDirectoryConverter();
    private final Converter<FtpFile, FeFile> ftpFileConverter = new FtpFile2FeFileConverter();
    private final Converter<FtpPath, FePath> ftpPathConverter = new FtpPath2FePathConverter(ftpDirectoryConverter, ftpFileConverter);

    public FeTab createDefaultTab() {
        String title = "This computer";
        String tip = "Your computer";
        FePathRepository repository = new LocalFileSystemPathRepository(directoryConverter, pathConverter);
        LocalPathService service = new LocalPathService(title, repository);
        return new FeTab(title, tip, service);
    }

    public FeTab createArchiveTab(String path) {
        String title = getArchiveTabTitle(path);
        String name = FePathUtil.getNameByPath(path);
        FePathRepository repository = new ArchiveFileSystemPathRepository(path, directoryConverter, pathConverter);
        LocalPathService service = new LocalPathService(name, repository);
        return new FeTab(title, path, service);
    }

    public String getArchiveTabTitle(String path) {
        String archiveName = FePathUtil.getNameByPath(path);
        return "Archive: " + archiveName;
    }

    public FeTab createFtpTab(FtpConnectionConfig config) {
        String title = getFtpTabTitle(config);
        FePathRepository repository = new FtpClientPathRepository(config, ftpDirectoryConverter, ftpPathConverter);
        RemotePathService service = new RemotePathService(config.getHostAndPort(), repository);
        return new FeTab(title, config.getInfo(), service);
    }

    public String getFtpTabTitle(FtpConnectionConfig config) {
        return "FTP: " + config.getHostAndPort();
    }

}
