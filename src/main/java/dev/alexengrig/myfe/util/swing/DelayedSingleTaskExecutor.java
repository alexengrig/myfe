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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * {@link Timer}-based delayed executor.
 */
public class DelayedSingleTaskExecutor extends Timer {

    private transient TaskAction previousTaskAction;

    /**
     * Construct executor.
     *
     * @param delay milliseconds for delay
     */
    public DelayedSingleTaskExecutor(int delay) {
        super(delay, null);
        setRepeats(false);
    }

    /**
     * Cancel the previous task and execute a new task.
     *
     * @param task new task
     */
    public void execute(Runnable task) {
        removeActionListener(previousTaskAction);
        TaskAction taskAction = new TaskAction(task);
        addActionListener(taskAction);
        previousTaskAction = taskAction;
        restart();
    }

    private class TaskAction implements ActionListener {

        private final Runnable task;

        private TaskAction(Runnable task) {
            this.task = task;
        }

        @Override
        public void actionPerformed(ActionEvent ignore) {
            task.run();
            DelayedSingleTaskExecutor.this.stop();
        }

    }

}
