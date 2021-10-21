package dev.alexengrig.myfe.util;

import javax.swing.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class BackgroundStreamer<T> extends SwingWorker<T, T> {

    private final Callable<Stream<T>> backgroundTask;
    private final Consumer<Stream<T>> chunksHandler;

    protected BackgroundStreamer(Callable<Stream<T>> backgroundTask, Consumer<Stream<T>> chunksHandler) {
        this.backgroundTask = Objects.requireNonNull(backgroundTask, "The background task must not be null");
        this.chunksHandler = Objects.requireNonNull(chunksHandler, "The chunks handler must not be null");
    }

    public static <T> void stream(Callable<Stream<T>> backgroundTask, Consumer<Stream<T>> chunksHandler) {
        final BackgroundStreamer<T> worker = new BackgroundStreamer<>(backgroundTask, chunksHandler);
        worker.execute();
    }

    @Override
    protected final T doInBackground() throws Exception {
        Stream<T> stream = backgroundTask.call();
        stream.forEachOrdered(this::publish);
        return null;
    }

    @Override
    protected final void process(List<T> chunks) {
        chunksHandler.accept(chunks.stream());
    }

}
