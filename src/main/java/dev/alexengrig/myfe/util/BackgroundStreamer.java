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

import javax.swing.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * {@link SwingWorker}-based implementation,
 * passes values from a background task to a chunks' handler,
 * from a background thread to the Event Dispatch Thread;
 * uses {@link Stream} that closes at the end.
 *
 * @param <T> the type of result
 */
public class BackgroundStreamer<T> extends SwingWorker<T, T> {

    /**
     * Run in a background thread.
     */
    private final Callable<Stream<T>> backgroundTask;
    /**
     * Run in the Event Dispatch Thread.
     */
    private final Consumer<Stream<T>> chunksHandler;

    /**
     * @implNote See FutureTaskHappensBefore_JCStressTest.
     */
    private Stream<T> stream;

    protected BackgroundStreamer(Callable<Stream<T>> backgroundTask, Consumer<Stream<T>> chunksHandler) {
        this.backgroundTask = Objects.requireNonNull(backgroundTask, "The background task must not be null");
        this.chunksHandler = Objects.requireNonNull(chunksHandler, "The chunks handler must not be null");
    }

    /**
     * Create a background task of streaming.
     *
     * @param backgroundTask result stream supplier
     * @param chunkHandler   result handler
     * @return the background task
     */
    public static <T> Task stream(Callable<Stream<T>> backgroundTask, Consumer<Stream<T>> chunkHandler) {
        final BackgroundStreamer<T> worker = new BackgroundStreamer<>(backgroundTask, chunkHandler);
        worker.execute();
        return new Task(worker);
    }

    protected boolean cancelStreaming() {
        return cancel(true);
    }

    @Override
    protected final T doInBackground() throws Exception {
        try {
            stream = backgroundTask.call();
            stream.forEachOrdered(this::publish);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return null;
    }

    @Override
    protected final void process(List<T> chunks) {
        chunksHandler.accept(chunks.stream());
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException e) {
            throw new InterruptedBackgroundTaskException();
        } catch (ExecutionException e) {
            throw new ExecutionBackgroundTaskException(e.getCause());
        } catch (CancellationException ignore) {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * Background task.
     */
    public static class Task {

        private final BackgroundStreamer<?> streamer;

        private Task(BackgroundStreamer<?> streamer) {
            this.streamer = streamer;
        }

        public boolean isDone() {
            return streamer.isDone();
        }

        public boolean isCanceled() {
            return streamer.isCancelled();
        }

        public boolean cancel() {
            return streamer.cancelStreaming();
        }

    }

}
