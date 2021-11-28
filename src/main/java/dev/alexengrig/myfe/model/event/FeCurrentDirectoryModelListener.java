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

package dev.alexengrig.myfe.model.event;


import dev.alexengrig.myfe.util.event.EventListener;
import dev.alexengrig.myfe.util.event.UnknownEventException;

/**
 * Event listener of {@link FeCurrentDirectoryModelEvent}.
 */
public interface FeCurrentDirectoryModelListener extends EventListener<FeCurrentDirectoryModelEvent> {

    @Override
    default void notify(FeCurrentDirectoryModelEvent event) {
        switch (event.getType()) {
            case GO_TO_ROOT:
                goToRoot(event);
                break;
            case GO_TO_DIRECTORY:
                goToDirectory(event);
                break;
            case REFRESH:
                refresh(event);
                break;
            default:
                throw new UnknownEventException(event);
        }
    }

    void goToRoot(FeCurrentDirectoryModelEvent event);

    void goToDirectory(FeCurrentDirectoryModelEvent event);

    void refresh(FeCurrentDirectoryModelEvent event);

}
