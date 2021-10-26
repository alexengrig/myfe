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

import dev.alexengrig.myfe.converter.Converter;
import dev.alexengrig.myfe.model.MyDirectory;
import dev.alexengrig.myfe.model.MyPath;
import dev.alexengrig.myfe.util.CloseOnTerminalOperationStreams;
import dev.alexengrig.myfe.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterator.ORDERED;

/**
 * {@link FileSystem}-based implementation.
 */
public class FileSystemPathRepository implements MyPathRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FileSystem fileSystem;
    private final Converter<Path, MyDirectory> directoryConverter;
    private final Converter<Path, MyPath> pathConverter;

    public FileSystemPathRepository(
            FileSystem fileSystem,
            Converter<Path, MyDirectory> directoryConverter,
            Converter<Path, MyPath> pathConverter) {
        this.fileSystem = fileSystem;
        this.directoryConverter = directoryConverter;
        this.pathConverter = pathConverter;
    }

    @Override
    public void close() throws Exception {
        fileSystem.close();
        LOGGER.debug("Closed file system");
    }

    @Override
    public List<MyDirectory> getRootDirectories() {
        LOGGER.debug("Getting root directories");
        Iterable<Path> directories = fileSystem.getRootDirectories();
        return StreamSupport.stream(directories.spliterator(), false)
                .map(directoryConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<MyPath> getChildren(String directoryPath) {
        LOGGER.debug("Getting children: {}", directoryPath);
        Path directory = fileSystem.getPath(requireNonNullPath(directoryPath));
        List<Path> children = PathUtil.getChildren(directory);
        return children.stream()
                .map(pathConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<MyDirectory> getSubdirectories(String directoryPath) {
        LOGGER.debug("Getting subdirectories: {}", directoryPath);
        Path directory = fileSystem.getPath(requireNonNullPath(directoryPath));
        final List<Path> subdirectories = PathUtil.getSubdirectories(directory);
        return subdirectories.stream()
                .map(directoryConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public String readBatch(String filePath, int batchSize) {
        LOGGER.debug("Start reading a batch: {} - {} bytes", filePath, batchSize);
        Path path = fileSystem.getPath(requireNonNullPath(filePath));
        try (SeekableByteChannel channel = Files.newByteChannel(path)) {
            if (batchSize > channel.size()) {
                batchSize = (int) channel.size();
            }
            ByteBuffer buffer = ByteBuffer.allocate(batchSize);
            int count = channel.read(buffer);
            if (count != -1) {
                return StandardCharsets.UTF_8.decode(buffer.flip()).toString();
            }
            return "";
        } catch (IOException e) {
            LOGGER.error("Exception of reading a batch: {} - {} bytes", filePath, batchSize, e);
            throw new UncheckedIOException("Exception of reading a batch: " +
                                           filePath + " - " + batchSize + " bytes", e);
        }
    }

    @Override
    public Stream<String> readInBatches(String filePath, int batchSize, int numberOfBatches) {
        try {
            LOGGER.debug("Start reading in batches: {}", filePath);
            Path path = fileSystem.getPath(requireNonNullPath(filePath));
            SeekableByteChannel channel = Files.newByteChannel(path);
            if (batchSize > channel.size()) {
                batchSize = (int) channel.size();
            }
            ByteBuffer buffer = ByteBuffer.allocate(batchSize);
            Iterator<String> iterator = new ChannelIterator(channel, buffer, numberOfBatches);
            Spliterator<String> spliterator = Spliterators.spliteratorUnknownSize(iterator, ORDERED | NONNULL);
            Stream<String> batchStream = StreamSupport.stream(spliterator, false).onClose(() -> {
                try {
                    channel.close();
                    LOGGER.debug("Closed channel for: {}", filePath);
                } catch (IOException e) {
                    LOGGER.error("Exception of closing channel of: {}", filePath, e);
                    throw new UncheckedIOException("Exception of closing channel of: " + filePath, e);
                }
            });
            LOGGER.debug("Return batch stream of: {}", filePath);
            return CloseOnTerminalOperationStreams.wrap(batchStream);
        } catch (IOException e) {
            LOGGER.error("Exception of reading in batches: {}", filePath, e);
            throw new UncheckedIOException("Exception of reading in batches: " + filePath, e);
        }
    }

    private String requireNonNullPath(String path) {
        return requireNonNull(path, "The path must not be null");
    }

    /**
     * {@link ReadableByteChannel} as {@link Iterator}.
     */
    //FIXME: Move class
    private static class ChannelIterator implements Iterator<String> {

        private final ReadableByteChannel channel;
        private final ByteBuffer buffer;
        private final int maxNumberOfBatches;

        int numberOfBatches = 0;
        String nextBatch = null;

        public ChannelIterator(ReadableByteChannel channel, ByteBuffer buffer, int maxNumberOfBatches) {
            this.channel = channel;
            this.buffer = buffer;
            this.maxNumberOfBatches = maxNumberOfBatches;
        }

        private String readBatch() {
            try {
                int count = channel.read(buffer);
                if (count != -1 && numberOfBatches++ < maxNumberOfBatches) {
                    return StandardCharsets.UTF_8.decode(buffer.flip()).toString();
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
