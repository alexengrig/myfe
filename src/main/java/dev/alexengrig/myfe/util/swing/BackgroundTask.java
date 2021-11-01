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

import javax.swing.*;
import java.util.Objects;

/**
 * {@link SwingWorker}'s task.
 */
public interface BackgroundTask {

    static BackgroundTask of(SwingWorker<?, ?> worker) {
        Objects.requireNonNull(worker, "The worker must not be null");
        return new BackgroundTask() {

            @Override
            public boolean isDone() {
                return worker.isDone();
            }

            @Override
            public boolean isCancelled() {
                return worker.isCancelled();
            }

            @Override
            public boolean cancel() {
                return worker.cancel(false);
            }

            @Override
            public boolean cancelNow() {
                return worker.cancel(true);
            }

        };
    }

    /**
     * @see SwingWorker#isDone()
     */
    boolean isDone();

    /**
     * @see SwingWorker#isCancelled()
     */
    boolean isCancelled();

    /**
     * In-progress tasks are allowed to complete.
     *
     * @see SwingWorker#cancel(boolean)
     */
    boolean cancel();

    /**
     * If the thread executing this task should be interrupted.
     *
     * @see SwingWorker#cancel(boolean)
     */
    boolean cancelNow();

}
