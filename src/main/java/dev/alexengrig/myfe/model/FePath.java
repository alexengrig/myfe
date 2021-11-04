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

import dev.alexengrig.myfe.util.FePathUtil;

/**
 * Path of file explorer.
 */
public abstract class FePath extends AbstractPath {

    private transient String extension;

    protected FePath(String path, String name) {
        super(path, name);
    }

    public FeDirectory asDirectory() {
        return (FeDirectory) this;
    }

    public FeFile asFile() {
        return (FeFile) this;
    }

    public String getExtension() {
        if (extension == null) {
            extension = FePathUtil.getExtension(this);
        }
        return extension;
    }

}
