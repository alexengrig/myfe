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
import dev.alexengrig.myfe.converter.Path2MyDirectoryConverter;
import dev.alexengrig.myfe.converter.Path2MyFileConverter;
import dev.alexengrig.myfe.converter.Path2MyPathConverter;
import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyFile;
import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.repository.LocalFileSystemPathRepository;
import dev.alexengrig.myfe.repository.MyPathRepository;
import dev.alexengrig.myfe.repository.URIFileSystemPathRepository;
import dev.alexengrig.myfe.service.MyPathService;
import dev.alexengrig.myfe.service.SimplePathService;
import dev.alexengrig.myfe.util.PathUtil;

import java.net.URI;
import java.nio.file.Path;

public class MyTabFactory {

    private final Converter<Path, MyDirectory> directoryConverter = new Path2MyDirectoryConverter();
    private final Converter<Path, MyFile> fileConverter = new Path2MyFileConverter();
    private final Converter<Path, MyPath> pathConverter = new Path2MyPathConverter(directoryConverter, fileConverter);

    public MyTab createDefaultTab() {
        MyPathRepository repository = new LocalFileSystemPathRepository(directoryConverter, pathConverter);
        //TODO: Move name
        MyPathService service = new SimplePathService("This computer", repository);
        MyTabComponent component = new MyTabComponent(service);
        //TODO: Move title and tip
        return new MyTab("This computer", "Your computer", component);
    }

    public MyTab createArchiveTab(Path path) {
        URI uri = path.toUri();
        URI jarUri = URI.create("jar:" + uri);
        String archiveName = PathUtil.getName(path);
        URIFileSystemPathRepository repository = new URIFileSystemPathRepository(jarUri, directoryConverter, pathConverter);
        MyPathService service = new SimplePathService(archiveName, repository);
        return new MyTab("Archive: " + archiveName, PathUtil.getAbsolutePath(path), new MyTabComponent(service));
    }

}
