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

package dev.alexengrig.myfe.domain;

import java.util.Objects;

/**
 * An abstraction of file/directory in a data store (e.g. file system/ftp server).
 */
abstract class AbstractPath {

    private final String path;
    private final String name;

    protected AbstractPath(String path, String name) {
        this.path = Objects.requireNonNull(path, "The path must not be null");
        this.name = Objects.requireNonNull(name, "The name must not be null");
        assert path.endsWith(name) : "Invalid name=" + name + " for path=" + path;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public abstract boolean isDirectory();

    public boolean isFile() {
        return !isDirectory();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractPath that = (AbstractPath) o;
        return path.equals(that.path) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name);
    }

    @Override
    public String toString() {
        return path;
    }

}
