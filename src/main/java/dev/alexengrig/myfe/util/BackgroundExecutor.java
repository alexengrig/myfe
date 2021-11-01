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

package dev.alexengrig.myfe.util;

import dev.alexengrig.myfe.exception.ExecutionBackgroundTaskException;
import dev.alexengrig.myfe.exception.InterruptedBackgroundTaskException;
import dev.alexengrig.myfe.util.logging.LazyLogger;
import dev.alexengrig.myfe.util.logging.LazyLoggerFactory;

import javax.swing.*;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BackgroundExecutor<T> extends SwingWorker<T, T> {

    private static final LazyLogger LOGGER = LazyLoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Supplier<String> descriptionSupplier;
    private final Callable<T> backgroundTask;
    private final Consumer<T> resultHandler;

    protected BackgroundExecutor(
            Supplier<String> descriptionSupplier,
            Callable<T> backgroundTask,
            Consumer<T> resultHandler) {
        this.descriptionSupplier = descriptionSupplier;
        this.backgroundTask = backgroundTask;
        this.resultHandler = resultHandler;
    }

    public static <T> BackgroundTask execute(
            Supplier<String> description,
            Callable<T> task,
            Consumer<T> handler) {
        BackgroundExecutor<T> worker = new BackgroundExecutor<>(description, task, handler);
        worker.execute();
        return BackgroundTask.of(worker);
    }

    @Override
    protected final T doInBackground() throws Exception {
        LOGGER.debug(m -> m.log("Start doing in background - {}",
                descriptionSupplier.get()));
        T result = backgroundTask.call();
        LOGGER.debug(m -> m.log("Finished doing in background - {}",
                descriptionSupplier.get()));
        return result;
    }

    @Override
    protected final void done() {
        LOGGER.debug(m -> m.log("Start waiting result - {}",
                descriptionSupplier.get()));
        T result;
        try {
            result = get();
        } catch (InterruptedException e) {
            LOGGER.warn(m -> m.log("Interrupted exception of waiting result",
                    descriptionSupplier.get(), e));
            throw new InterruptedBackgroundTaskException("Interrupted exception of waiting result" +
                    descriptionSupplier.get(),
                    e);
        } catch (ExecutionException e) {
            LOGGER.warn(m -> m.log("Execution exception of waiting result",
                    descriptionSupplier.get(), e));
            throw new ExecutionBackgroundTaskException(e.getCause());
        } catch (CancellationException ignore) {
            LOGGER.warn(m -> m.log("Cancellation exception of waiting result",
                    descriptionSupplier.get()));
            return;
        }
        LOGGER.debug(m -> m.log("Finished waiting result - {}",
                descriptionSupplier.get()));
        resultHandler.accept(result); // don't catch handler's exceptions
    }

}
