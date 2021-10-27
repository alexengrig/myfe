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

package dev.alexengrig.myfe.model;

import org.apache.commons.net.ftp.FTPFile;

import java.util.Calendar;
import java.util.Objects;

/**
 * Immutable {@link FTPFile} with context.
 */
public class ContextFTPFile extends FTPFile {

    private final String parentPath;
    private final String separator;
    private final FTPFile delegate;

    private transient String path;

    public ContextFTPFile(String parentPath, String separator, FTPFile delegate) {
        this.parentPath = Objects.requireNonNull(parentPath, "The parent path must not be null");
        this.separator = Objects.requireNonNull(separator, "The separator must not be null");
        this.delegate = Objects.requireNonNull(delegate, "The delegate must not be null");
    }

    public String getPath() {
        if (path == null) {
            //TODO: Normalize path
            path = parentPath + separator + delegate.getName();
        }
        return path;
    }

    // Delegate methods

    @Override
    public String getGroup() {
        return delegate.getGroup();
    }

    @Override
    public void setGroup(String group) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getHardLinkCount() {
        return delegate.getHardLinkCount();
    }

    @Override
    public void setHardLinkCount(int links) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLink() {
        return delegate.getLink();
    }

    @Override
    public void setLink(String link) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRawListing() {
        return delegate.getRawListing();
    }

    @Override
    public void setRawListing(String rawListing) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getSize() {
        return delegate.getSize();
    }

    @Override
    public void setSize(long size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Calendar getTimestamp() {
        return delegate.getTimestamp();
    }

    @Override
    public void setTimestamp(Calendar date) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getType() {
        return delegate.getType();
    }

    @Override
    public void setType(int type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUser() {
        return delegate.getUser();
    }

    @Override
    public void setUser(String user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasPermission(int access, int permission) {
        return delegate.hasPermission(access, permission);
    }

    @Override
    public void setPermission(int access, int permission, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDirectory() {
        return delegate.isDirectory();
    }

    @Override
    public boolean isFile() {
        return delegate.isFile();
    }

    @Override
    public boolean isSymbolicLink() {
        return delegate.isSymbolicLink();
    }

    @Override
    public boolean isUnknown() {
        return delegate.isUnknown();
    }

    @Override
    public boolean isValid() {
        return delegate.isValid();
    }

    @Override
    public String toFormattedString() {
        return delegate.toFormattedString();
    }

    @Override
    public String toFormattedString(String timezone) {
        return delegate.toFormattedString(timezone);
    }

    @Override
    public String toString() {
        return getPath();
    }

}
