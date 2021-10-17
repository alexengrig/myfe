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

package dev.alexengrig.myfe.exception;

/**
 * When {@link dev.alexengrig.myfe.util.BackgroundWorker} doesn't expect an exception.
 * <p>Use {@link Throwable#getCause()} to get an unexpected message.
 */
public class BackgroundWorkerUnexpectedException extends RuntimeException {

    public BackgroundWorkerUnexpectedException(Throwable cause) {
        super(cause);
    }

    public BackgroundWorkerUnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

}
