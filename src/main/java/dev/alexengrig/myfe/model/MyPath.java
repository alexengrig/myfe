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

/**
 * Path of file explorer.
 */
public abstract class MyPath extends AbstractPath {

    private transient String extension;

    protected MyPath(String path, String name) {
        super(path, name);
    }

    @Deprecated(forRemoval = true)
    public static MyPath of(String path, String name, boolean isDirectory) {
        return isDirectory ? directory(path, name) : file(path, name);
    }

    @Deprecated(forRemoval = true)
    public static MyFile file(String path, String name) {
        return new MyFile(path, name);
    }

    @Deprecated(forRemoval = true)
    public static MyDirectory directory(String path, String name) {
        return new MyDirectory(path, name);
    }

    public MyDirectory asDirectory() {
        return (MyDirectory) this;
    }

    public MyFile asFile() {
        return (MyFile) this;
    }

    public String getExtension() {
        if (extension == null) {
            extension = MyPathUtil.getExtension(this);
        }
        return extension;
    }

}
