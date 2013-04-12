package com.hartveld.stream.reactive;

import java.util.IntSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.apache.commons.lang.NotImplementedException;

@FunctionalInterface
public interface IntObservable extends IntStream {

	AutoCloseable subscribe(IntConsumer onNext, Consumer<Exception> onError, Runnable onCompleted);

	default AutoCloseable subscribe(final IntObserver observer) {
		return subscribe(observer::onNext, observer::onError, observer::onCompleted);
	}

	@Override
	default LongObservable longs() {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable doubles() {
		throw new NotImplementedException();
	}

	@Override
	default Observable<Integer> boxed() {
		return mapToObj((IntFunction<Integer>) i -> Integer.valueOf(i));
	}

	@Override
	default IntObservable map(IntUnaryOperator mapper) {
		throw new NotImplementedException();
	}

	@Override
	default <U> Observable<U> mapToObj(IntFunction<? extends U> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleStream mapToDouble(IntToDoubleFunction mapper) {
		throw new NotImplementedException();
	}

	@Override
	default LongStream mapToLong(IntToLongFunction mapper) {
		throw new NotImplementedException();
	}

	@Override
	default IntObservable flatMap(IntFunction<? extends IntStream> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default IntObservable filter(IntPredicate predicate) {
		throw new NotImplementedException();
	}

	@Override
	default IntObservable peek(IntConsumer consumer) {
		throw new NotImplementedException();
	}

	@Override
	default IntObservable sorted() {
		throw new NotImplementedException();
	}

	@Override
	default IntObservable distinct() {
		// @@@ While functional and quick to implement this approach is not very efficient.
		//     An efficient version requires an int-specific map/set implementation.
		return boxed().distinct().mapToInt(i -> (int) i);
	}

	@Override
	default IntObservable limit(long maxSize) {
		throw new NotImplementedException();
	}

	@Override
	default IntObservable substream(long startOffset) {
		throw new NotImplementedException();
	}

	@Override
	default IntObservable substream(long startOffset, long endOffset) {
		throw new NotImplementedException();
	}

	@Override
	default IntObservable sequential() {
		throw new UnsupportedOperationException();
	}

	@Override
	default IntObservable parallel() {
		throw new UnsupportedOperationException();
	}

	@Override
	default IntObservable unordered() {
		throw new UnsupportedOperationException();
	}

	@Override
	default int reduce(int identity, IntBinaryOperator op) {
		throw new NotImplementedException();
	}

	@Override
	default OptionalInt reduce(IntBinaryOperator op) {
		throw new NotImplementedException();
	}

	@Override
	default boolean anyMatch(IntPredicate predicate) {
		throw new NotImplementedException();
	}

	@Override
	default boolean allMatch(IntPredicate predicate) {
		throw new NotImplementedException();
	}

	@Override
	default boolean noneMatch(IntPredicate predicate) {
		throw new NotImplementedException();
	}

	@Override
	default OptionalInt findFirst() {
		throw new NotImplementedException();
	}

	@Override
	default OptionalInt findAny() {
		throw new NotImplementedException();
	}

	@Override
	default void forEach(IntConsumer consumer) {
		throw new NotImplementedException();
	}

	@Override
	default void forEachOrdered(IntConsumer consumer) {
		throw new NotImplementedException();
	}

	@Override
	default int[] toArray() {
		throw new NotImplementedException();
	}

	@Override
	default long count() {
		throw new UnsupportedOperationException();
	}

	@Override
	default OptionalDouble average() {
		throw new NotImplementedException();
	}

	@Override
	default OptionalInt min() {
		throw new NotImplementedException();
	}

	@Override
	default OptionalInt max() {
		throw new NotImplementedException();
	}

	@Override
	default int sum() {
		throw new NotImplementedException();
	}

	@Override
	default PrimitiveIterator.OfInt iterator() {
		throw new NotImplementedException();
	}

	@Override
	default Spliterator.OfInt spliterator() {
		throw new NotImplementedException();
	}

	@Override
	default boolean isParallel() {
		throw new UnsupportedOperationException();
	}

	@Override
	default <R> R collect(Supplier<R> resultFactory, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
		throw new NotImplementedException();
	}

	@Override
	default IntSummaryStatistics summaryStatistics() {
		throw new NotImplementedException();
	}

}
