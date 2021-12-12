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

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

/**
 * {@link org.apache.commons.net.ftp.FTPClient} exception.
 */
public class FTPClientIOException extends IOException {

    public FTPClientIOException(int code, String message) {
        super(createMessage(code, message));
    }

    public FTPClientIOException(FTPClient client) {
        this(client.getReplyCode(), client.getReplyString());
    }

    public FTPClientIOException(FTPClient client, Throwable suppressed) {
        this(client.getReplyCode(), client.getReplyString());
        addSuppressed(suppressed);
    }

    private static String createMessage(int code, String message) {
        return "FTP server return code - " + (message != null ? message : code);
    }

}
