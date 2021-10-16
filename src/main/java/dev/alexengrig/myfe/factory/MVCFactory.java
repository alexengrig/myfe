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

package dev.alexengrig.myfe.factory;

/**
 * Mode-View-Controller factory.
 *
 * @param <M> the type of model
 * @param <V> the type of view
 * @param <C> the type of controller
 */
public interface MVCFactory<M, V, C> {

    M createModel();

    V createView(M model);

    C createController(V view);

}
