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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * {@link SwingWorker}-based background worker.
 *
 * <p>When inheriting, specify a particular type of exception - {@code X}, not generic,
 * or inherit from {@link WithExceptionType}.
 *
 * @param <X> the type of expected exception
 */
public class BackgroundWorker<T, V, X extends Throwable>
        extends SwingWorker<T, V> {

    /**
     * Index of {@code X}.
     */
    private static final int INDEX_OF_EXCEPTION_GENERIC_TYPE = 2;

    private final Callable<T> backgroundTask;
    private final Consumer<T> resultHandler;
    private final Consumer<X> exceptionHandler;

    protected BackgroundWorker(Callable<T> backgroundTask, Consumer<T> resultHandler, Consumer<X> exceptionHandler) {
        this(backgroundTask, resultHandler, exceptionHandler, true);
    }

    private BackgroundWorker(
            Callable<T> backgroundTask, Consumer<T> resultHandler, Consumer<X> exceptionHandler, boolean needCheck) {
        this.backgroundTask = backgroundTask;
        this.resultHandler = resultHandler;
        this.exceptionHandler = exceptionHandler;
        if (needCheck) {
            requireValidGenericTypeOfException();
        }
    }

    public static <T, V, X extends Throwable> BackgroundWorker<T, V, X> from(
            Callable<T> backgroundTask, Consumer<T> resultHandler,
            Class<X> exceptionType, Consumer<X> exceptionHandler) {
        return new WithExceptionType<>(backgroundTask, resultHandler, exceptionType, exceptionHandler);
    }

    private void requireValidGenericTypeOfException() {
        ParameterizedType genericSupertype = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] genericTypes = genericSupertype.getActualTypeArguments();
        Type genericType = genericTypes[INDEX_OF_EXCEPTION_GENERIC_TYPE];
        if (!(genericType instanceof Class)) {
            throw new IllegalStateException(
                    "The X must be a particular type, not generic - " + genericType + "; " +
                    "otherwise inherit from: " +
                    BackgroundWorker.class.getName() + "." + WithExceptionType.class.getSimpleName());
        }
    }

    @Override
    protected final T doInBackground() throws Exception {
        return backgroundTask.call();
    }

    @Override
    protected final void done() {
        onBeforeDone();
        try {
            onDone();
        } catch (Exception exception) {
            onFailDone(exception);
            throw exception;
        } finally {
            onAfterDone();
        }
    }

    protected void onBeforeDone() {
        // do nothing
    }

    private void onDone() {
        T result;
        try {
            result = get();
        } catch (ExecutionException executionException) {
            Throwable exception = executionException.getCause();
            if (isExpectedException(exception)) {
                @SuppressWarnings("unchecked")
                X expected = (X) exception;
                exceptionHandler.accept(expected); // don't catch handler's unchecked exceptions
                return;
            } else {
                throw new ExecutionBackgroundTaskException(exception);
            }
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
            throw new InterruptedBackgroundTaskException();
        }
        resultHandler.accept(result); // don't catch handler's unchecked exceptions
    }

    protected void onFailDone(Exception cause) {
        // do nothing
    }

    protected void onAfterDone() {
        // do nothing
    }

    private boolean isExpectedException(Throwable exception) {
        Class<X> exceptionType = getExpectedExceptionType();
        return exceptionType.isInstance(exception);
    }

    /**
     * @implNote It works only for {@code extends BackgroundWorker<T, V, TypeOfException>},
     * where {@code TypeOfException} is a particular type.
     * @see BackgroundWorker#requireValidGenericTypeOfException()
     */
    protected Class<X> getExpectedExceptionType() {
        return ReflectionUtil.getGenericType(getClass(), INDEX_OF_EXCEPTION_GENERIC_TYPE);
    }

    protected static class WithExceptionType<T, V, X extends Throwable>
            extends BackgroundWorker<T, V, X> {

        private final Class<X> exceptionType;

        protected WithExceptionType(
                Callable<T> backgroundTask, Consumer<T> resultHandler,
                Class<X> exceptionType, Consumer<X> exceptionHandler) {
            super(backgroundTask, resultHandler, exceptionHandler, false);
            this.exceptionType = exceptionType;
        }

        @Override
        protected Class<X> getExpectedExceptionType() {
            return exceptionType;
        }

    }

}
