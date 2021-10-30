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

package dev.alexengrig.myfe.repository;

import dev.alexengrig.myfe.client.MyFtpClient;
import dev.alexengrig.myfe.config.FTPConnectionConfig;
import dev.alexengrig.myfe.converter.Converter;
import dev.alexengrig.myfe.converter.MyFtpDirectory2MyDirectoryConverter;
import dev.alexengrig.myfe.converter.MyFtpPath2MyPathConverter;
import dev.alexengrig.myfe.exception.MyPathRepositoryException;
import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyFtpDirectory;
import dev.alexengrig.myfe.model.MyFtpPath;
import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.util.CloseOnTerminalOperationStreams;
import dev.alexengrig.myfe.util.LazyLogger;
import dev.alexengrig.myfe.util.LazyLoggerFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystem;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterator.ORDERED;

/**
 * {@link MyFtpClient}-based implementation.
 */
//TODO: Create FileSystem of FTP server for FileSystemPathRepository
public class FtpPathRepository implements MyPathRepository {

    private static final LazyLogger LOGGER = LazyLoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static {
        FileSystemOptions options = new FileSystemOptions();
        FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
    }

    private final Converter<MyFtpPath, MyPath> pathConverter;
    private final Converter<MyFtpDirectory, MyDirectory> directoryConverter;
    private final FTPConnectionConfig config;
    private final FtpFileSystem fs;

    public FtpPathRepository(FTPConnectionConfig connectionConfig) {
        this(//TODO: Get from context
                new MyFtpPath2MyPathConverter(),
                new MyFtpDirectory2MyDirectoryConverter(),
                connectionConfig);
    }

    public FtpPathRepository(
            Converter<MyFtpPath, MyPath> pathConverter,
            Converter<MyFtpDirectory, MyDirectory> directoryConverter,
            FTPConnectionConfig connectionConfig) {
        this.pathConverter = pathConverter;
        this.directoryConverter = directoryConverter;
        this.config = connectionConfig;
        this.fs = createFtpFileSystem(connectionConfig);
    }

    private FtpFileSystem createFtpFileSystem(FTPConnectionConfig cfg) {
        try {
            String username = cfg.getUsername();
            String password = new String(cfg.getPassword());
            String host = cfg.getHost();
            int port = cfg.getPort();
            FileSystemManager fsManager = VFS.getManager();
            FileObject file = fsManager.resolveFile("ftp://" + username + ":" + password + "@" + host + ":" + port + "/");
            return (FtpFileSystem) file.getFileSystem();
        } catch (FileSystemException e) {
            throw new MyPathRepositoryException(e);
        }
    }

    @Override
    public void close() throws Exception {
        fs.close();
    }

    @Override
    public List<MyDirectory> getRootDirectories() {
        LOGGER.debug(m -> m.log("Start getting root directories \"{}\"",
                config.getInfo()));
        try {
            FileObject[] directories = fs.getRoot().findFiles(new FileSelector() {
                @Override
                public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
                    return fileInfo.getFile().isFolder();
                }

                @Override
                public boolean traverseDescendents(FileSelectInfo fileInfo) {
                    return false;
                }
            });
            List<MyDirectory> result = Arrays.stream(directories)
                    .map(f -> new MyDirectory(f.getName().getPath(), f.getName().getBaseName())) //FIXME: Converter
                    .collect(Collectors.toList());
            LOGGER.debug(m -> m.log("Finished getting root directories \"{}\", number of directories: {}",
                    config.getInfo(), result.size()));
            return result;
        } catch (Exception e) {
            LOGGER.error(m -> m.log("Exception of getting root directories \"{}\"",
                    config.getInfo(), e));
            throw new MyPathRepositoryException("Exception of getting root directories \"" +
                    config.getInfo() + "\"",
                    e);
        }
    }

    @Override
    public List<MyPath> getChildren(String directoryPath) {
        LOGGER.debug(m -> m.log("Start getting children \"{}\" for: {}",
                config.getInfo(), directoryPath));
        try {
            FileObject directory = fs.resolveFile(directoryPath);
            //FIXME: Check on folder
            FileObject[] children = directory.getChildren();
            List<MyPath> result = Arrays.stream(children)
                    .map(f -> {
                        try {
                            return MyPath.of(f.getName().getPath(), f.getName().getBaseName(), f.isFolder());
                        } catch (FileSystemException e) {
                            throw new MyPathRepositoryException(e);
                        }
                    })
                    .collect(Collectors.toList());
            LOGGER.debug(m -> m.log("Finished getting children \"{}\" in \"{}\", number of elements: {}",
                    config.getInfo(), directoryPath, result.size()));
            return result;
        } catch (Exception e) {
            LOGGER.error(m -> m.log("Exception of getting children \"{}\" for: {}",
                    config.getInfo(), directoryPath, e));
            throw new MyPathRepositoryException("Exception of getting children \"" +
                    config.getInfo() + "\" for: " +
                    directoryPath,
                    e);
        }
    }

    @Override
    public List<MyDirectory> getSubdirectories(String directoryPath) {
        LOGGER.debug(m -> m.log("Start getting subdirectories \"{}\" for: {}",
                config.getInfo(), directoryPath));
        try {
            FileObject directory = fs.resolveFile(directoryPath);
            FileObject[] directories = directory.findFiles(new FileSelector() {
                @Override
                public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
                    return fileInfo.getFile().isFolder();
                }

                @Override
                public boolean traverseDescendents(FileSelectInfo fileInfo) {
                    return false;
                }
            });
            List<MyDirectory> result = Arrays.stream(directories)
                    .map(f -> new MyDirectory(f.getName().getPath(), f.getName().getBaseName())) //FIXME: Converter
                    .collect(Collectors.toList());
            LOGGER.debug(m -> m.log("Finished getting subdirectories \"{}\" in \"{}\", number of directories: {}",
                    config.getInfo(), directoryPath, result.size()));
            return result;
        } catch (Exception e) {
            LOGGER.error(m -> m.log("Exception of getting subdirectories \"{}\" for: {}",
                    config.getInfo(), directoryPath, e));
            throw new MyPathRepositoryException("Exception of getting subdirectories \"" +
                    config.getInfo() + "\" for: " +
                    directoryPath,
                    e);
        }
    }

    @Override
    public String readBatch(String filePath, int batchSize) {
        LOGGER.debug(m -> m.log("Start reading a batch of {} byte(s) \"{}\" for: {}",
                batchSize, config.getInfo(), filePath));
        try {
            FileObject file = fs.resolveFile(filePath);
            InputStream inputStream = file.getContent().getInputStream();
            byte[] buffer = new byte[batchSize];
            int count = inputStream.read(buffer);
            String result;
            if (count != -1) {
                result = new String(buffer, 0, count, UTF_8);
            } else {
                result = "";
            }
            LOGGER.debug(m -> m.log("Finished reading a batch of {} byte(s) \"{}\" in \"{}\", number of characters: {}",
                    batchSize, config.getInfo(), filePath, result.length()));
            return result;
        } catch (Exception e) {
            LOGGER.error(m -> m.log("Exception of reading a batch of {} byte(s) \"{}\" for: {}",
                    batchSize, config.getInfo(), filePath, e));
            throw new MyPathRepositoryException("Exception of reading a batch of " +
                    batchSize + " byte(s) \"" +
                    config.getInfo() + "\" for: " +
                    filePath,
                    e);
        }
    }

    @Override
    public Stream<String> readInBatches(String filePath, int batchSize, int numberOfBatches) {
        LOGGER.debug(m -> m.log("Start reading {} batch(es) of {} byte(s) \"{}\" for: {}",
                numberOfBatches, batchSize, config.getInfo(), filePath));
        try {
            FileObject file = fs.resolveFile(filePath);
            InputStream inputStream = file.getContent().getInputStream();
            Iterator<String> iterator = new InputStreamIterator(inputStream, new byte[batchSize], numberOfBatches);
            Spliterator<String> spliterator = Spliterators.spliteratorUnknownSize(iterator, NONNULL | ORDERED);
            Stream<String> result = StreamSupport.stream(spliterator, false)
                    .onClose(() -> {
                        try {
                            inputStream.close();
                            LOGGER.debug(m -> m.log("Closed InputStream of reading {} batch(es) of {} byte(s) \"{}\" for: {}",
                                    numberOfBatches, batchSize, config.getInfo(), filePath));
                        } catch (IOException e) {
                            LOGGER.error(m -> m.log("Exception of closing InputStream of reading {} batch(es) of {} byte(s) \"{}\" for: {}",
                                    numberOfBatches, batchSize, config.getInfo(), filePath, e));
                            throw new UncheckedIOException("Exception of closing InputStream of reading " +
                                    numberOfBatches + " batch(es) of " +
                                    batchSize + " byte(s) \"" +
                                    config.getInfo() + "\" for: " +
                                    filePath,
                                    e);
                        }
                    });
            LOGGER.debug(m -> m.log("Return Stream of reading {} batch(es) of {} byte(s) \"{}\" for: {}",
                    numberOfBatches, batchSize, config.getInfo(), filePath));
            return CloseOnTerminalOperationStreams.wrap(result);
        } catch (Exception e) {
            LOGGER.error(m -> m.log("Exception of reading {} batch(es) of {} byte(s) \"{}\" for: {}",
                    numberOfBatches, batchSize, config.getInfo(), filePath, e));
            throw new MyPathRepositoryException("Exception of reading " +
                    numberOfBatches + " batch(es) of " +
                    batchSize + " byte(s) \"" +
                    config.getInfo() + "\" for: " +
                    filePath,
                    e);
        }
    }

    private static class InputStreamIterator implements Iterator<String> {

        private final InputStream inputStream;
        private final byte[] buffer;
        private final int maxNumberOfBatches;

        int numberOfBatches = 0;
        String nextBatch = null;

        public InputStreamIterator(InputStream inputStream, byte[] buffer, int maxNumberOfBatches) {
            this.inputStream = inputStream;
            this.buffer = buffer;
            this.maxNumberOfBatches = maxNumberOfBatches;
        }

        private String readBatch() {
            try {
                int count = inputStream.read(buffer);
                if (count != -1 && numberOfBatches++ < maxNumberOfBatches) {
                    return new String(buffer, 0, count, UTF_8);
                }
                return null;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public boolean hasNext() {
            if (nextBatch != null) {
                return true;
            } else {
                nextBatch = readBatch();
                return nextBatch != null;
            }
        }

        @Override
        public String next() {
            if (nextBatch != null || hasNext()) {
                String result = nextBatch;
                nextBatch = null;
                return result;
            } else {
                throw new NoSuchElementException();
            }
        }

    }

}
