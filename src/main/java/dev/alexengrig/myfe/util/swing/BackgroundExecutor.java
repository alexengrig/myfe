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

package dev.alexengrig.myfe.util.swing;

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

/**
 * {@link SwingWorker}-based background executor.
 * <ul>
 * <li>{@code after hook} - runs on the Event Dispatch Thread;
 * <li>{@code background task} - runs on a background thread;
 * <li>{@code result handler} - runs on the Event Dispatch Thread;
 * <li>{@code error handler} - runs on the Event Dispatch Thread;
 * <li>{@code before hook} - runs on the Event Dispatch Thread.
 *
 * @param <T> the type of result
 */
public class BackgroundExecutor<T> extends SwingWorker<T, T> {

    private static final LazyLogger LOGGER = LazyLoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final Supplier<String> WITHOUT_DESCRIPTION = () -> "Without description";

    private final Supplier<String> descriptionSupplier;
    private final Callable<T> backgroundTask;
    private final Consumer<T> resultHandler;
    private final Consumer<Throwable> errorHandler;
    private final Runnable finishHook;

    private BackgroundExecutor(
            Supplier<String> descriptionSupplier,
            Callable<T> backgroundTask,
            Consumer<T> resultHandler,
            Consumer<Throwable> handler, Runnable finishHook) {
        this.descriptionSupplier = descriptionSupplier;
        this.backgroundTask = backgroundTask;
        this.resultHandler = resultHandler;
        this.errorHandler = handler;
        this.finishHook = finishHook;
    }

    /**
     * Task builder.
     *
     * @param task background task
     * @param <T>  the type of result
     * @return task builder
     */
    public static <T> Builder<T> builder(Callable<T> task) {
        return new Builder<>(task);
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
        try {
            doDone();
        } finally {
            if (finishHook != null) {
                finishHook.run();
            }
        }
    }

    private void doDone() {
        T result;
        try {
            result = get();
        } catch (InterruptedException e) {
            LOGGER.warn(m -> m.log("Interrupted exception of waiting result - {}",
                    descriptionSupplier.get(), e));
            Thread.currentThread().interrupt();
            throw new InterruptedBackgroundTaskException("Interrupted exception of waiting result - " +
                    descriptionSupplier.get(),
                    e);
        } catch (ExecutionException e) {
            LOGGER.warn(m -> m.log("Execution exception of waiting result - {}",
                    descriptionSupplier.get(), e));
            if (errorHandler != null && e.getCause() != null) {
                errorHandler.accept(e.getCause());
                return;
            }
            throw new ExecutionBackgroundTaskException(e.getCause());
        } catch (CancellationException ignore) {
            LOGGER.debug(m -> m.log("Cancellation exception of waiting result - {}",
                    descriptionSupplier.get()));
            return;
        }
        LOGGER.debug(m -> m.log("Finished waiting result - {}",
                descriptionSupplier.get()));
        if (resultHandler != null) {
            resultHandler.accept(result); // don't catch handler's exceptions
        }
    }

    /**
     * Task builder.
     *
     * @param <T> the type of result
     */
    public static final class Builder<T> {

        private final Callable<T> backgroundTask;

        private Supplier<String> descriptionSupplier = WITHOUT_DESCRIPTION;
        private Consumer<T> resultHandler;
        private Consumer<Throwable> errorHandler;
        private Runnable beforeHook;
        private Runnable afterHook;

        public Builder(Callable<T> backgroundTask) {
            this.backgroundTask = backgroundTask;
        }

        /**
         * Set the task description.
         *
         * @param description the task description
         * @return this builder
         */
        public Builder<T> withDescription(String description) {
            return withDescription(() -> description);
        }

        /**
         * Set the supplier of task description.
         *
         * @param descriptionSupplier the supplier of task description
         * @return this builder
         */
        public Builder<T> withDescription(Supplier<String> descriptionSupplier) {
            this.descriptionSupplier = descriptionSupplier;
            return this;
        }

        /**
         * Set the before hook.
         *
         * @param hook the before hook
         * @return this builder
         */
        public Builder<T> withBeforeHook(Runnable hook) {
            this.beforeHook = hook;
            return this;
        }

        /**
         * Set the result handler.
         *
         * @param handler the result handler
         * @return this builder
         */
        public Builder<T> withResultHandler(Consumer<T> handler) {
            this.resultHandler = handler;
            return this;
        }

        /**
         * Set the error handler.
         *
         * @param handler the error handler
         * @return this builder
         */
        public Builder<T> withErrorHandler(Consumer<Throwable> handler) {
            this.errorHandler = handler;
            return this;
        }

        /**
         * Set the after hook.
         *
         * @param hook the after hook
         * @return this builder
         */
        public Builder<T> withAfterHook(Runnable hook) {
            this.afterHook = hook;
            return this;
        }

        /**
         * Execute and get background task.
         *
         * @return background task
         */
        public BackgroundTask execute() {
            BackgroundExecutor<T> worker = new BackgroundExecutor<>(
                    descriptionSupplier, backgroundTask, resultHandler, errorHandler, afterHook);
            if (beforeHook != null) {
                SwingUtilities.invokeLater(beforeHook);
            }
            worker.execute();
            return BackgroundTask.of(worker);
        }

    }

}
