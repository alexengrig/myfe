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

package dev.alexengrig.myfe.model.directory;

import dev.alexengrig.myfe.model.Model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class DirectoryModel
        implements Model {

    private final String path;
    private final String name;

    private boolean loaded;

    public DirectoryModel(String path, String name) {
        this.path = Objects.requireNonNull(path, "The path must not be null");
        this.name = Objects.requireNonNull(name, "The name must not be null");
    }

    protected DirectoryModel(String name) {
        this.path = null; //TODO: "/" ?
        this.name = Objects.requireNonNull(name, "The name must not be null");
    }

    public static DirectoryModel from(Path directory) {
        assert Files.isDirectory(directory) : "The directory must be directory: " + directory;
        String path = directory.toAbsolutePath().toString();
        String name = directory.getNameCount() == 0 ? directory.toString() : directory.getFileName().toString();
        return new DirectoryModel(path, name);
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Override
    public String toString() {
        return name;
    }

}
