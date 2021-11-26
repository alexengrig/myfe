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

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.LongSummaryStatistics;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * {@link Stream}-wrapper where {@link Stream} closes itself on terminal operations.
 *
 * <p>Before:
 *
 * <pre>{@code
 * try (Stream<?> stream = getStream()) {
 *     return stream.count();
 * }
 * }</pre>
 *
 * <p>After:
 *
 * <pre>{@code
 * Stream<?> stream = CloseOnTerminalOperationStreams.wrap(getStream());
 * return stream.count();
 * }</pre>
 */
public final class CloseOnTerminalOperationStreams {

    private CloseOnTerminalOperationStreams() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility class");
    }

    public static <T> Stream<T> wrap(Stream<T> stream) {
        return new DelegatedStream<>(stream);
    }

    public static IntStream wrapInt(IntStream intStream) {
        return new DelegatedIntStream(intStream);
    }

    public static LongStream wrapLong(LongStream intStream) {
        return new DelegatedLongStream(intStream);
    }

    public static DoubleStream wrapDouble(DoubleStream intStream) {
        return new DelegatedDoubleStream(intStream);
    }

    private static class DelegatedStream<T> implements Stream<T> {

        private final Stream<T> delegate;

        public DelegatedStream(Stream<T> delegate) {
            this.delegate = delegate;
        }

        // Terminal operations

        @Override
        public void forEach(Consumer<? super T> action) {
            try (delegate) {
                delegate.forEach(action);
            }
        }

        @Override
        public void forEachOrdered(Consumer<? super T> action) {
            try (delegate) {
                delegate.forEachOrdered(action);
            }
        }

        @Override
        public Object[] toArray() {
            try (delegate) {
                return delegate.toArray();
            }
        }

        @Override
        public <A> A[] toArray(IntFunction<A[]> generator) {
            try (delegate) {
                return delegate.toArray(generator);
            }
        }

        @Override
        public T reduce(T identity, BinaryOperator<T> accumulator) {
            try (delegate) {
                return delegate.reduce(identity, accumulator);
            }
        }

        @Override
        public Optional<T> reduce(BinaryOperator<T> accumulator) {
            try (delegate) {
                return delegate.reduce(accumulator);
            }
        }

        @Override
        public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
            try (delegate) {
                return delegate.reduce(identity, accumulator, combiner);
            }
        }

        @Override
        public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
            try (delegate) {
                return delegate.collect(supplier, accumulator, combiner);
            }
        }

        @Override
        public <R, A> R collect(Collector<? super T, A, R> collector) {
            try (delegate) {
                return delegate.collect(collector);
            }
        }

        @Override
        public Optional<T> min(Comparator<? super T> comparator) {
            try (delegate) {
                return delegate.min(comparator);
            }
        }

        @Override
        public Optional<T> max(Comparator<? super T> comparator) {
            try (delegate) {
                return delegate.max(comparator);
            }
        }

        @Override
        public long count() {
            try (delegate) {
                return delegate.count();
            }
        }

        @Override
        public boolean anyMatch(Predicate<? super T> predicate) {
            try (delegate) {
                return delegate.anyMatch(predicate);
            }
        }

        @Override
        public boolean allMatch(Predicate<? super T> predicate) {
            try (delegate) {
                return delegate.allMatch(predicate);
            }
        }

        @Override
        public boolean noneMatch(Predicate<? super T> predicate) {
            try (delegate) {
                return delegate.noneMatch(predicate);
            }
        }

        @Override
        public Optional<T> findFirst() {
            try (delegate) {
                return delegate.findFirst();
            }
        }

        @Override
        public Optional<T> findAny() {
            try (delegate) {
                return delegate.findAny();
            }
        }

        @Override
        public Iterator<T> iterator() {
            try (delegate) {
                return delegate.iterator();
            }
        }

        @Override
        public Spliterator<T> spliterator() {
            try (delegate) {
                return delegate.spliterator();
            }
        }

        // To other types

        @Override
        public IntStream mapToInt(ToIntFunction<? super T> mapper) {
            return new DelegatedIntStream(delegate.mapToInt(mapper));
        }

        @Override
        public LongStream mapToLong(ToLongFunction<? super T> mapper) {
            return new DelegatedLongStream(delegate.mapToLong(mapper));
        }

        @Override
        public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
            return wrapDouble(delegate.mapToDouble(mapper));
        }

        @Override
        public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
            return new DelegatedIntStream(delegate.flatMapToInt(mapper));
        }

        @Override
        public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
            return new DelegatedLongStream(delegate.flatMapToLong(mapper));
        }

        @Override
        public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
            return wrapDouble(delegate.flatMapToDouble(mapper));
        }

        // Others

        @Override
        public Stream<T> filter(Predicate<? super T> predicate) {
            return wrap(delegate.filter(predicate));
        }

        @Override
        public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
            return wrap(delegate.map(mapper));
        }

        @Override
        public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
            return wrap(delegate.flatMap(mapper));
        }

        @Override
        public Stream<T> distinct() {
            return wrap(delegate.distinct());
        }

        @Override
        public Stream<T> sorted() {
            return wrap(delegate.sorted());
        }

        @Override
        public Stream<T> sorted(Comparator<? super T> comparator) {
            return wrap(delegate.sorted(comparator));
        }

        @Override
        public Stream<T> peek(Consumer<? super T> action) {
            return wrap(delegate.peek(action));
        }

        @Override
        public Stream<T> limit(long maxSize) {
            return wrap(delegate.limit(maxSize));
        }

        @Override
        public Stream<T> skip(long n) {
            return wrap(delegate.skip(n));
        }

        @Override
        public Stream<T> takeWhile(Predicate<? super T> predicate) {
            return wrap(delegate.takeWhile(predicate));
        }

        @Override
        public Stream<T> dropWhile(Predicate<? super T> predicate) {
            return wrap(delegate.dropWhile(predicate));
        }

        @Override
        public Stream<T> sequential() {
            return wrap(delegate.sequential());
        }

        @Override
        public Stream<T> parallel() {
            return wrap(delegate.parallel());
        }

        @Override
        public Stream<T> unordered() {
            return wrap(delegate.unordered());
        }

        @Override
        public Stream<T> onClose(Runnable closeHandler) {
            return wrap(delegate.onClose(closeHandler));
        }

        @Override
        public boolean isParallel() {
            return delegate.isParallel();
        }

        @Override
        public void close() {
            delegate.close();
        }

    }

    private static class DelegatedIntStream implements IntStream {

        private final IntStream delegate;

        public DelegatedIntStream(IntStream delegate) {
            this.delegate = delegate;
        }

        // Terminal operations

        @Override
        public void forEach(IntConsumer action) {
            try (delegate) {
                delegate.forEach(action);
            }
        }

        @Override
        public void forEachOrdered(IntConsumer action) {
            try (delegate) {
                delegate.forEachOrdered(action);
            }
        }

        @Override
        public PrimitiveIterator.OfInt iterator() {
            try (delegate) {
                return delegate.iterator();
            }
        }

        @Override
        public Spliterator.OfInt spliterator() {
            try (delegate) {
                return delegate.spliterator();
            }
        }

        @Override
        public int[] toArray() {
            try (delegate) {
                return delegate.toArray();
            }
        }

        @Override
        public int reduce(int identity, IntBinaryOperator op) {
            try (delegate) {
                return delegate.reduce(identity, op);
            }
        }

        @Override
        public OptionalInt reduce(IntBinaryOperator op) {
            try (delegate) {
                return delegate.reduce(op);
            }
        }

        @Override
        public <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
            try (delegate) {
                return delegate.collect(supplier, accumulator, combiner);
            }
        }

        @Override
        public int sum() {
            try (delegate) {
                return delegate.sum();
            }
        }

        @Override
        public OptionalInt min() {
            try (delegate) {
                return delegate.min();
            }
        }

        @Override
        public OptionalInt max() {
            try (delegate) {
                return delegate.max();
            }
        }

        @Override
        public long count() {
            try (delegate) {
                return delegate.count();
            }
        }

        @Override
        public OptionalDouble average() {
            try (delegate) {
                return delegate.average();
            }
        }

        @Override
        public IntSummaryStatistics summaryStatistics() {
            try (delegate) {
                return delegate.summaryStatistics();
            }
        }

        @Override
        public boolean anyMatch(IntPredicate predicate) {
            try (delegate) {
                return delegate.anyMatch(predicate);
            }
        }

        @Override
        public boolean allMatch(IntPredicate predicate) {
            try (delegate) {
                return delegate.allMatch(predicate);
            }
        }

        @Override
        public boolean noneMatch(IntPredicate predicate) {
            try (delegate) {
                return delegate.noneMatch(predicate);
            }
        }

        @Override
        public OptionalInt findFirst() {
            try (delegate) {
                return delegate.findFirst();
            }
        }

        @Override
        public OptionalInt findAny() {
            try (delegate) {
                return delegate.findAny();
            }
        }

        // To other types

        @Override
        public Stream<Integer> boxed() {
            return wrap(delegate.boxed());
        }

        @Override
        public <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
            return wrap(delegate.mapToObj(mapper));
        }

        @Override
        public LongStream mapToLong(IntToLongFunction mapper) {
            return wrapLong(delegate.mapToLong(mapper));
        }

        @Override
        public DoubleStream mapToDouble(IntToDoubleFunction mapper) {
            return delegate.mapToDouble(mapper);
        }

        @Override
        public LongStream asLongStream() {
            return delegate.asLongStream();
        }

        @Override
        public DoubleStream asDoubleStream() {
            try (delegate) {
                return delegate.asDoubleStream();
            }
        }

        // Others

        @Override
        public IntStream filter(IntPredicate predicate) {
            return wrapInt(delegate.filter(predicate));
        }

        @Override
        public IntStream map(IntUnaryOperator mapper) {
            return wrapInt(delegate.map(mapper));
        }

        @Override
        public IntStream flatMap(IntFunction<? extends IntStream> mapper) {
            return wrapInt(delegate.flatMap(mapper));
        }

        @Override
        public IntStream distinct() {
            return wrapInt(delegate.distinct());
        }

        @Override
        public IntStream sorted() {
            return wrapInt(delegate.sorted());
        }

        @Override
        public IntStream peek(IntConsumer action) {
            return wrapInt(delegate.peek(action));
        }

        @Override
        public IntStream limit(long maxSize) {
            return wrapInt(delegate.limit(maxSize));
        }

        @Override
        public IntStream skip(long n) {
            return wrapInt(delegate.skip(n));
        }

        @Override
        public IntStream takeWhile(IntPredicate predicate) {
            return wrapInt(delegate.takeWhile(predicate));
        }

        @Override
        public IntStream dropWhile(IntPredicate predicate) {
            return wrapInt(delegate.dropWhile(predicate));
        }

        @Override
        public IntStream sequential() {
            return wrapInt(delegate.sequential());
        }

        @Override
        public IntStream parallel() {
            return wrapInt(delegate.parallel());
        }

        @Override
        public IntStream unordered() {
            return wrapInt(delegate.unordered());
        }

        @Override
        public IntStream onClose(Runnable closeHandler) {
            return wrapInt(delegate.onClose(closeHandler));
        }

        @Override
        public boolean isParallel() {
            return delegate.isParallel();
        }

        @Override
        public void close() {
            delegate.close();
        }

    }

    private static class DelegatedLongStream implements LongStream {

        private final LongStream delegate;

        public DelegatedLongStream(LongStream delegate) {
            this.delegate = delegate;
        }

        // Terminal operations

        @Override
        public void forEach(LongConsumer action) {
            try (delegate) {
                delegate.forEach(action);
            }
        }

        @Override
        public void forEachOrdered(LongConsumer action) {
            try (delegate) {
                delegate.forEachOrdered(action);
            }
        }

        @Override
        public long[] toArray() {
            try (delegate) {
                return delegate.toArray();
            }
        }

        @Override
        public long reduce(long identity, LongBinaryOperator op) {
            try (delegate) {
                return delegate.reduce(identity, op);
            }
        }

        @Override
        public OptionalLong reduce(LongBinaryOperator op) {
            try (delegate) {
                return delegate.reduce(op);
            }
        }

        @Override
        public <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner) {
            try (delegate) {
                return delegate.collect(supplier, accumulator, combiner);
            }
        }

        @Override
        public long sum() {
            try (delegate) {
                return delegate.sum();
            }
        }

        @Override
        public OptionalLong min() {
            try (delegate) {
                return delegate.min();
            }
        }

        @Override
        public OptionalLong max() {
            try (delegate) {
                return delegate.max();
            }
        }

        @Override
        public long count() {
            try (delegate) {
                return delegate.count();
            }
        }

        @Override
        public OptionalDouble average() {
            try (delegate) {
                return delegate.average();
            }
        }

        @Override
        public LongSummaryStatistics summaryStatistics() {
            try (delegate) {
                return delegate.summaryStatistics();
            }
        }

        @Override
        public boolean anyMatch(LongPredicate predicate) {
            try (delegate) {
                return delegate.anyMatch(predicate);
            }
        }

        @Override
        public boolean allMatch(LongPredicate predicate) {
            try (delegate) {
                return delegate.allMatch(predicate);
            }
        }

        @Override
        public boolean noneMatch(LongPredicate predicate) {
            try (delegate) {
                return delegate.noneMatch(predicate);
            }
        }

        @Override
        public OptionalLong findFirst() {
            try (delegate) {
                return delegate.findFirst();
            }
        }

        @Override
        public OptionalLong findAny() {
            try (delegate) {
                return delegate.findAny();
            }
        }

        @Override
        public PrimitiveIterator.OfLong iterator() {
            try (delegate) {
                return delegate.iterator();
            }
        }

        @Override
        public Spliterator.OfLong spliterator() {
            try (delegate) {
                return delegate.spliterator();
            }
        }

        // To other types

        @Override
        public <U> Stream<U> mapToObj(LongFunction<? extends U> mapper) {
            return wrap(delegate.mapToObj(mapper));
        }

        @Override
        public Stream<Long> boxed() {
            return wrap(delegate.boxed());
        }

        @Override
        public IntStream mapToInt(LongToIntFunction mapper) {
            return new DelegatedIntStream(delegate.mapToInt(mapper));
        }

        @Override
        public DoubleStream mapToDouble(LongToDoubleFunction mapper) {
            return wrapDouble(delegate.mapToDouble(mapper));
        }

        @Override
        public DoubleStream asDoubleStream() {
            return wrapDouble(delegate.asDoubleStream());
        }

        // Others

        @Override
        public LongStream filter(LongPredicate predicate) {
            return wrapLong(delegate.filter(predicate));
        }

        @Override
        public LongStream map(LongUnaryOperator mapper) {
            return wrapLong(delegate.map(mapper));
        }

        @Override
        public LongStream flatMap(LongFunction<? extends LongStream> mapper) {
            return wrapLong(delegate.flatMap(mapper));
        }

        @Override
        public LongStream distinct() {
            return wrapLong(delegate.distinct());
        }

        @Override
        public LongStream sorted() {
            return wrapLong(delegate.sorted());
        }

        @Override
        public LongStream peek(LongConsumer action) {
            return wrapLong(delegate.peek(action));
        }

        @Override
        public LongStream limit(long maxSize) {
            return wrapLong(delegate.limit(maxSize));
        }

        @Override
        public LongStream skip(long n) {
            return wrapLong(delegate.skip(n));
        }

        @Override
        public LongStream takeWhile(LongPredicate predicate) {
            return wrapLong(delegate.takeWhile(predicate));
        }

        @Override
        public LongStream dropWhile(LongPredicate predicate) {
            return wrapLong(delegate.dropWhile(predicate));
        }

        @Override
        public LongStream sequential() {
            return wrapLong(delegate.sequential());
        }

        @Override
        public LongStream parallel() {
            return wrapLong(delegate.parallel());
        }

        @Override
        public LongStream unordered() {
            return wrapLong(delegate.unordered());
        }

        @Override
        public LongStream onClose(Runnable closeHandler) {
            return wrapLong(delegate.onClose(closeHandler));
        }

        @Override
        public boolean isParallel() {
            return delegate.isParallel();
        }

        @Override
        public void close() {
            delegate.close();
        }

    }

    private static class DelegatedDoubleStream implements DoubleStream {

        private final DoubleStream delegate;

        public DelegatedDoubleStream(DoubleStream delegate) {
            this.delegate = delegate;
        }

        // Terminal operations

        @Override
        public void forEach(DoubleConsumer action) {
            try (delegate) {
                delegate.forEach(action);
            }
        }

        @Override
        public void forEachOrdered(DoubleConsumer action) {
            try (delegate) {
                delegate.forEachOrdered(action);
            }
        }

        @Override
        public PrimitiveIterator.OfDouble iterator() {
            try (delegate) {
                return delegate.iterator();
            }
        }

        @Override
        public Spliterator.OfDouble spliterator() {
            try (delegate) {
                return delegate.spliterator();
            }
        }

        @Override
        public double[] toArray() {
            try (delegate) {
                return delegate.toArray();
            }
        }

        @Override
        public double reduce(double identity, DoubleBinaryOperator op) {
            try (delegate) {
                return delegate.reduce(identity, op);
            }
        }

        @Override
        public OptionalDouble reduce(DoubleBinaryOperator op) {
            try (delegate) {
                return delegate.reduce(op);
            }
        }

        @Override
        public <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner) {
            try (delegate) {
                return delegate.collect(supplier, accumulator, combiner);
            }
        }

        @Override
        public double sum() {
            try (delegate) {
                return delegate.sum();
            }
        }

        @Override
        public OptionalDouble min() {
            try (delegate) {
                return delegate.min();
            }
        }

        @Override
        public OptionalDouble max() {
            try (delegate) {
                return delegate.max();
            }
        }

        @Override
        public long count() {
            try (delegate) {
                return delegate.count();
            }
        }

        @Override
        public OptionalDouble average() {
            try (delegate) {
                return delegate.average();
            }
        }

        @Override
        public DoubleSummaryStatistics summaryStatistics() {
            try (delegate) {
                return delegate.summaryStatistics();
            }
        }

        @Override
        public boolean anyMatch(DoublePredicate predicate) {
            try (delegate) {
                return delegate.anyMatch(predicate);
            }
        }

        @Override
        public boolean allMatch(DoublePredicate predicate) {
            try (delegate) {
                return delegate.allMatch(predicate);
            }
        }

        @Override
        public boolean noneMatch(DoublePredicate predicate) {
            try (delegate) {
                return delegate.noneMatch(predicate);
            }
        }

        @Override
        public OptionalDouble findFirst() {
            try (delegate) {
                return delegate.findFirst();
            }
        }

        @Override
        public OptionalDouble findAny() {
            try (delegate) {
                return delegate.findAny();
            }
        }

        // To other types

        @Override
        public Stream<Double> boxed() {
            return new DelegatedStream<>(delegate.boxed());
        }

        @Override
        public <U> Stream<U> mapToObj(DoubleFunction<? extends U> mapper) {
            return new DelegatedStream<>(delegate.mapToObj(mapper));
        }

        @Override
        public IntStream mapToInt(DoubleToIntFunction mapper) {
            return new DelegatedIntStream(delegate.mapToInt(mapper));
        }

        @Override
        public LongStream mapToLong(DoubleToLongFunction mapper) {
            return new DelegatedLongStream(delegate.mapToLong(mapper));
        }

        // Others

        @Override
        public DoubleStream filter(DoublePredicate predicate) {
            return wrapDouble(delegate.filter(predicate));
        }

        @Override
        public DoubleStream map(DoubleUnaryOperator mapper) {
            return wrapDouble(delegate.map(mapper));
        }

        @Override
        public DoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper) {
            return wrapDouble(delegate.flatMap(mapper));
        }

        @Override
        public DoubleStream distinct() {
            return wrapDouble(delegate.distinct());
        }

        @Override
        public DoubleStream sorted() {
            return wrapDouble(delegate.sorted());
        }

        @Override
        public DoubleStream peek(DoubleConsumer action) {
            return wrapDouble(delegate.peek(action));
        }

        @Override
        public DoubleStream limit(long maxSize) {
            return wrapDouble(delegate.limit(maxSize));
        }

        @Override
        public DoubleStream skip(long n) {
            return wrapDouble(delegate.skip(n));
        }

        @Override
        public DoubleStream takeWhile(DoublePredicate predicate) {
            return wrapDouble(delegate.takeWhile(predicate));
        }

        @Override
        public DoubleStream dropWhile(DoublePredicate predicate) {
            return wrapDouble(delegate.dropWhile(predicate));
        }

        @Override
        public DoubleStream sequential() {
            return wrapDouble(delegate.sequential());
        }

        @Override
        public DoubleStream parallel() {
            return wrapDouble(delegate.parallel());
        }

        @Override
        public DoubleStream unordered() {
            return wrapDouble(delegate.unordered());
        }

        @Override
        public DoubleStream onClose(Runnable closeHandler) {
            return wrapDouble(delegate.onClose(closeHandler));
        }

        @Override
        public boolean isParallel() {
            return delegate.isParallel();
        }

        @Override
        public void close() {
            delegate.close();
        }

    }

}
