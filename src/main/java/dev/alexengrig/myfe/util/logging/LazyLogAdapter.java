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

package dev.alexengrig.myfe.util.logging;

import org.apache.commons.logging.Log;

/**
 * Adapter for {@link Log} to {@link LazyLogger}.
 */
public class LazyLogAdapter implements Log {

    private final LazyLogger logger;

    public LazyLogAdapter(LazyLogger logger) {
        this.logger = logger;
    }

    protected String format() {
        return "{}";
    }

    protected Object[] arguments(Object message) {
        return new Object[]{message};
    }

    protected Object[] arguments(Object message, Throwable throwable) {
        return new Object[]{message, throwable};
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(Object message) {
        logger.debug(m -> m.log(format(), arguments(message)));
    }

    @Override
    public void debug(Object message, Throwable t) {
        logger.debug(m -> m.log(format(), arguments(message, t)));
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(Object message) {
        logger.error(m -> m.log(format(), arguments(message)));
    }

    @Override
    public void error(Object message, Throwable t) {
        logger.error(m -> m.log(format(), arguments(message, t)));
    }

    @Override
    public boolean isFatalEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void fatal(Object message) {
        logger.error(m -> m.log(format(), arguments(message)));
    }

    @Override
    public void fatal(Object message, Throwable t) {
        logger.error(m -> m.log(format(), arguments(message, t)));
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(Object message) {
        logger.info(m -> m.log(format(), arguments(message)));
    }

    @Override
    public void info(Object message, Throwable t) {
        logger.info(m -> m.log(format(), arguments(message, t)));
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(Object message) {
        logger.trace(m -> m.log(format(), arguments(message)));
    }

    @Override
    public void trace(Object message, Throwable t) {
        logger.trace(m -> m.log(format(), arguments(message, t)));
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(Object message) {
        logger.warn(m -> m.log(format(), arguments(message)));
    }

    @Override
    public void warn(Object message, Throwable t) {
        logger.warn(m -> m.log(format(), arguments(message, t)));
    }

}
