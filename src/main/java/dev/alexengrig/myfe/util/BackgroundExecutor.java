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

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class BackgroundExecutor<T> extends SwingWorker<T, T> {

    private final Callable<T> task;
    private final Consumer<T> doneHandler;

    protected BackgroundExecutor(Callable<T> backgroundTask, Consumer<T> doneHandler) {
        this.task = backgroundTask;
        this.doneHandler = doneHandler;
    }

    public static <T> void execute(Callable<T> task, Consumer<T> handler) {
        BackgroundExecutor<T> worker = new BackgroundExecutor<>(task, handler);
        worker.execute();
    }

    @Override
    protected final T doInBackground() throws Exception {
        return task.call();
    }

    @Override
    protected final void done() {
        T result;
        try {
            result = get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        doneHandler.accept(result);
    }

}
