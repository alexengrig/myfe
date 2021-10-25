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

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = "-ea")
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
public class FileReading_Benchmarks {

    @Param({"2", "4", "8"})
    int megabytes;

    @Param({"128", "256", "512", "1024", "2048", "4096", "8192", "16384", "32768", "65536"})
    int batchSize;

    Path file;

    @Setup(Level.Trial)
    public void beforeAll() throws Exception {
        Path file = Files.createTempFile("myfe-file-for-reader-", ".txt");
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        char[] buffer = new char[1048576]; // 1Mb
        for (int i = 0, symbol = 'a'; i < megabytes; i++, symbol++) {
            Arrays.fill(buffer, (char) symbol);
            joiner.add(new String(buffer));
        }
        Files.writeString(file, joiner.toString());
        this.file = file;
    }

    @TearDown(Level.Trial)
    public void afterAll() throws Exception {
        Files.delete(file);
    }

    @Benchmark
    public void fileChannel(Blackhole blackhole) throws Exception {
        try (RandomAccessFile reader = new RandomAccessFile(file.toFile(), "r")) {
            FileChannel channel = reader.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(batchSize);
            while (channel.read(buffer) != -1) {
                String batch = buffer.flip().toString();
                blackhole.consume(batch);
            }
        }
    }

    //@Benchmark //#2
    public void fileReader_with_charArray(Blackhole blackhole) throws Exception {
        try (FileReader reader = new FileReader(file.toFile())) {
            char[] buffer = new char[batchSize];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                String batch = new String(buffer, 0, count);
                blackhole.consume(batch);
            }
        }
    }

    //@Benchmark //#3
    public void fileReader_with_charBuffer(Blackhole blackhole) throws Exception {
        try (FileReader reader = new FileReader(file.toFile())) {
            CharBuffer buffer = CharBuffer.allocate(batchSize);
            while (reader.read(buffer) != -1) {
                String batch = buffer.flip().toString();
                blackhole.consume(batch);
            }
        }
    }

    //@Benchmark //#4
    public void fileInputStream_with_byteArray(Blackhole blackhole) throws Exception {
        try (FileInputStream reader = new FileInputStream(file.toFile())) {
            byte[] buffer = new byte[batchSize];
            int count;
            while ((count = reader.read()) != -1) {
                String batch = new String(buffer, 0, count);
                blackhole.consume(batch);
            }
        }
    }

}
