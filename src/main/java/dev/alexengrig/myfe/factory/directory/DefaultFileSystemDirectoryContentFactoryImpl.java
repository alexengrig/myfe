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

package dev.alexengrig.myfe.factory.directory;

import dev.alexengrig.myfe.controller.directory.DirectoryContentController;
import dev.alexengrig.myfe.model.directory.ContentModel;
import dev.alexengrig.myfe.model.directory.DirectoryContentModel;
import dev.alexengrig.myfe.service.directory.DefaultFileSystemDirectoryService;
import dev.alexengrig.myfe.service.directory.DirectoryService;
import dev.alexengrig.myfe.view.directory.DirectoryContentView;

import java.util.List;

public class DefaultFileSystemDirectoryContentFactoryImpl implements DirectoryContentFactory {

    //TODO: Get from context
    private final DirectoryService directoryService = new DefaultFileSystemDirectoryService();

    @Override
    public DirectoryContentModel createModel() {
        //FIXME: Get root directory
        List<ContentModel> content = directoryService.getContent(null);
        return new DirectoryContentModel(content);
    }

    @Override
    public DirectoryContentView createView(DirectoryContentModel model) {
        return new DirectoryContentView(model);
    }

    @Override
    public DirectoryContentController createController(DirectoryContentView view) {
        return new DirectoryContentController(view);
    }

}
