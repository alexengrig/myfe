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

import dev.alexengrig.myfe.exception.UnknownEventException;
import dev.alexengrig.myfe.model.FeSelectedPathModel;
import dev.alexengrig.myfe.util.event.EventListener;

/**
 * Event listener of {@link FeSelectedPathModel}.
 */
public interface FeSelectedPathModelListener extends EventListener<FeSelectedPathModelEvent> {

    @Override
    default void notify(FeSelectedPathModelEvent event) {
        if (event.getType() == FeSelectedPathModelEvent.Type.CHANGE_PATH) {
            changePath(event);
        } else {
            throw new UnknownEventException(event);
        }
    }

    void changePath(FeSelectedPathModelEvent event);

}
