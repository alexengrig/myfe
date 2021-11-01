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

import dev.alexengrig.myfe.config.FTPConnectionConfig;
import dev.alexengrig.myfe.converter.Converter;
import dev.alexengrig.myfe.converter.FileObject2MyDirectoryConverter;
import dev.alexengrig.myfe.converter.FileObject2MyPathConverter;
import dev.alexengrig.myfe.exception.MyPathRepositoryException;
import dev.alexengrig.myfe.model.FeDirectory;
import dev.alexengrig.myfe.model.FePath;
import dev.alexengrig.myfe.util.CloseOnTerminalOperationStreams;
import dev.alexengrig.myfe.util.function.ThrowablePredicate;
import dev.alexengrig.myfe.util.logging.LazyLogAdapter;
import dev.alexengrig.myfe.util.logging.LazyLogger;
import dev.alexengrig.myfe.util.logging.LazyLoggerFactory;
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
 * {@link FtpFileSystem}-based implementation.
 */
public class ApacheCommonsFtpFileSystemPathRepository implements MyPathRepository {

    private static final LazyLogger LOGGER = LazyLoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static {
        FileSystemOptions options = new FileSystemOptions();
        FtpFileSystemConfigBuilder config = FtpFileSystemConfigBuilder.getInstance();
        config.setPassiveMode(options, true);
    }

    private final Converter<FileObject, FePath> pathConverter;
    private final Converter<FileObject, FeDirectory> directoryConverter;
    private final FTPConnectionConfig config;
    private final FtpFileSystem fileSystem;

    public ApacheCommonsFtpFileSystemPathRepository(FTPConnectionConfig connectionConfig) {
        this(//TODO: Get from context
                new FileObject2MyPathConverter(),
                new FileObject2MyDirectoryConverter(),
                connectionConfig);
    }

    public ApacheCommonsFtpFileSystemPathRepository(
            Converter<FileObject, FePath> pathConverter,
            Converter<FileObject, FeDirectory> directoryConverter,
            FTPConnectionConfig connectionConfig) {
        this.pathConverter = pathConverter;
        this.directoryConverter = directoryConverter;
        this.config = connectionConfig;
        this.fileSystem = createFtpFileSystem(connectionConfig);
        fileSystem.setLogger(new LogAdapter(connectionConfig));
    }

    private FtpFileSystem createFtpFileSystem(FTPConnectionConfig config) {
        try {
            String username = config.getUsername();
            String password = new String(config.getPassword());
            String host = config.getHost();
            int port = config.getPort();
            FileSystemManager fsManager = VFS.getManager();
            String path = "ftp://" + username + ":" + password + "@" + host + ":" + port + "/";
            FileObject file = fsManager.resolveFile(path);
            return (FtpFileSystem) file.getFileSystem();
        } catch (FileSystemException e) {
            LOGGER.error(m -> m.log("Exception of creating FTP file system: {}",
                    config.getInfo(), e));
            throw new MyPathRepositoryException("Exception of creating FTP file system: " +
                    config.getInfo(),
                    e);
        }
    }

    @Override
    public void close() throws Exception {
        fileSystem.close();
    }

    @Override
    public List<FeDirectory> getRootDirectories() {
        LOGGER.debug(m -> m.log("Start getting root directories \"{}\"",
                config.getInfo()));
        try {
            FileObject[] directories = fileSystem.getRoot().findFiles(Selector.of(FileObject::isFolder));
            List<FeDirectory> result = Arrays.stream(directories)
                    .map(directoryConverter::convert)
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
    public List<FePath> getChildren(String directoryPath) {
        LOGGER.debug(m -> m.log("Start getting children \"{}\" for: {}",
                config.getInfo(), directoryPath));
        try {
            FileObject directory = fileSystem.resolveFile(directoryPath);
            //FIXME: Check on folder
            FileObject[] children = directory.getChildren();
            List<FePath> result = Arrays.stream(children)
                    .map(pathConverter::convert)
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
    public List<FeDirectory> getSubdirectories(String directoryPath) {
        LOGGER.debug(m -> m.log("Start getting subdirectories \"{}\" for: {}",
                config.getInfo(), directoryPath));
        try {
            FileObject directory = fileSystem.resolveFile(directoryPath);
            FileObject[] directories = directory.findFiles(Selector.of(FileObject::isFolder));
            List<FeDirectory> result = Arrays.stream(directories)
                    .map(directoryConverter::convert)
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
            FileObject file = fileSystem.resolveFile(filePath);
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
            FileObject file = fileSystem.resolveFile(filePath);
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

    private static class LogAdapter extends LazyLogAdapter {

        private final FTPConnectionConfig config;

        public LogAdapter(FTPConnectionConfig config) {
            super(LOGGER);
            this.config = config;
        }

        @Override
        protected String format() {
            return "\"{}\": {}";
        }

        @Override
        protected Object[] arguments(Object message) {
            return new Object[]{config.getInfo(), message};
        }

        @Override
        protected Object[] arguments(Object message, Throwable throwable) {
            return new Object[]{config.getInfo(), message, throwable};
        }

    }

    private static class Selector implements FileSelector {

        private final ThrowablePredicate<FileObject> predicate;

        private Selector(ThrowablePredicate<FileObject> predicate) {
            this.predicate = predicate;
        }

        private static Selector of(ThrowablePredicate<FileObject> predicate) {
            return new Selector(predicate);
        }

        @Override
        public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
            return predicate.test(fileInfo.getFile());
        }

        @Override
        public boolean traverseDescendents(FileSelectInfo fileInfo) {
            return false;
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
