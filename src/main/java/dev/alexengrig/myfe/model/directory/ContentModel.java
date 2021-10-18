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

public class ContentModel
        implements Model {

    private final String name;
    private final String extension;

    public ContentModel(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    public static ContentModel from(Path content) {
        String name = content.getNameCount() == 0 ? content.toString() : content.getFileName().toString();
        String extension;
        if (Files.isDirectory(content)) {
            extension = "<dir>";
        } else {
            int indexOfDot = name.lastIndexOf('.');
            if (indexOfDot >= 0) {
                extension = name.substring(indexOfDot + 1);
            } else {
                extension = "<unknown>";
            }
        }
        return new ContentModel(name, extension);
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return name;
    }

}
