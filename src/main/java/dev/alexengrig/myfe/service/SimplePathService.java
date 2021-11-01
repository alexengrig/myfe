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

package dev.alexengrig.myfe.service;

import dev.alexengrig.myfe.model.FeDirectory;
import dev.alexengrig.myfe.model.FeFile;
import dev.alexengrig.myfe.model.FePath;
import dev.alexengrig.myfe.repository.MyPathRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SimplePathService implements MyPathService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Batch size in bytes.
     */
    private static final int FILE_PREVIEW_CONTENT_SIZE = 1_048_576; // 1Mb

    private final String rootName;
    private final MyPathRepository repository;

    public SimplePathService(String rootName, MyPathRepository repository) {
        this.rootName = rootName;
        this.repository = repository;
    }

    @Override
    public void destroy() {
        try {
            repository.close();
        } catch (Exception e) {
            throw new RuntimeException("Exception of destroying", e);
        }
    }

    @Override
    public String getRootName() {
        return rootName;
    }

    @Override
    public List<FeDirectory> getRootDirectories() {
        return repository.getRootDirectories();
    }

    @Override
    public List<FeDirectory> getSubdirectories(FeDirectory directory) {
        return repository.getSubdirectories(requireNonNullDirectory(directory).getPath());
    }

    @Override
    public List<FePath> getDirectoryContent(FeDirectory directory) {
        return repository.getChildren(requireNonNullDirectory(directory).getPath());
    }

    @Override
    public String getFileContentPreview(FeFile file) {
        return repository.readBatch(requireNonNullFile(file).getPath(), FILE_PREVIEW_CONTENT_SIZE);
    }

    @Override
    public Stream<String> readFileContent(FeFile file) {
        return repository.readInBatches(requireNonNullFile(file).getPath(), FILE_PREVIEW_CONTENT_SIZE, 1);
    }

    private FeDirectory requireNonNullDirectory(FeDirectory directory) {
        return Objects.requireNonNull(directory, "The directory must not be null");
    }

    private FeFile requireNonNullFile(FeFile file) {
        return Objects.requireNonNull(file, "The file must not be null");
    }

}
