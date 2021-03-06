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

package dev.alexengrig.myfe.service;

import dev.alexengrig.myfe.util.swing.BackgroundTask;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Service of background executor.
 */
public interface BackgroundExecutorService {

    <T> BackgroundTask execute(Supplier<String> descriptionSupplier, Callable<T> backgroundTask, Consumer<T> resultHandler);

    default <T> BackgroundTask execute(String description, Callable<T> backgroundTask, Consumer<T> resultHandler) {
        return execute(() -> description, backgroundTask, resultHandler);
    }

}
