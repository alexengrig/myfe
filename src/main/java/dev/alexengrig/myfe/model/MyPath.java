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

package dev.alexengrig.myfe.model;

import dev.alexengrig.myfe.util.MyPathUtil;

import java.util.Objects;

public abstract class MyPath {

    private final String path;
    private final String name;

    private transient String extension;

    protected MyPath(String path, String name) {
        this.path = Objects.requireNonNull(path, "The path must not be null");
        this.name = Objects.requireNonNull(name, "The name must not be null");
    }

    public abstract boolean isDirectory();

    public MyDirectory asDirectory() {
        return (MyDirectory) this;
    }

    public boolean isFile() {
        return !isDirectory();
    }

    public MyFile asFile() {
        return (MyFile) this;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        if (extension == null) {
            extension = MyPathUtil.getExtension(this);
        }
        return extension;
    }

    @Override
    public String toString() {
        return path;
    }

}
