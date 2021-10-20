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

import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.repository.MyPathRepository;

import java.util.List;
import java.util.Objects;

public class SimplePathService implements MyPathService {

    private final String name;
    private final MyPathRepository repository;

    public SimplePathService(String name, MyPathRepository repository) {
        this.name = name;
        this.repository = repository;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<MyDirectory> getRootDirectories() {
        return repository.getRootDirectories(); //TODO: Sort
    }

    @Override
    public List<MyDirectory> getSubdirectories(MyDirectory directory) {
        return repository.getSubdirectories(requireNonNullDirectory(directory).getPath()); //TODO: Sort
    }

    @Override
    public List<MyPath> getContent(MyDirectory directory) {
        return repository.getChildren(requireNonNullDirectory(directory).getPath()); //TODO: Sort
    }

    private MyDirectory requireNonNullDirectory(MyDirectory directory) {
        return Objects.requireNonNull(directory, "The directory must not be null");
    }

}
