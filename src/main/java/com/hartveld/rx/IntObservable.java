package com.hartveld.rx;

import java.util.OptionalInt;
import java.util.Spliterator;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collector;
import java.util.stream.FlatMapper;
import java.util.stream.IntStream;
import org.apache.commons.lang.NotImplementedException;

public interface IntObservable extends IntStream {

	AutoCloseable subscribe(IntConsumer onNext, Consumer<Throwable> onError, Runnable onCompleted);

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
        return map((IntFunction<Integer>) i -> Integer.valueOf(i));
    }

	@Override
	default IntObservable map(IntUnaryOperator mapper) {
		throw new NotImplementedException();
	}

	@Override
	default <U> Observable<U> map(IntFunction<U> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default IntObservable flatMap(IntFunction<? extends IntStream> mapper) {
        return flatMap((int i, IntConsumer sink) -> mapper.apply(i).sequential().forEach(sink));
    }

	@Override
	default IntObservable flatMap(FlatMapper.OfIntToInt mapper) {
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
        return boxed().distinct().map(i -> (int) i);
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
		throw new NotImplementedException();
	}

    @Override
	default IntObservable parallel() {
		throw new NotImplementedException();
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
	default <R> R collect(Collector.OfInt<R> collector) {
		throw new NotImplementedException();
	}

	@Override
	default <R> R collectUnordered(Collector.OfInt<R> collector) {
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
	default void forEachUntilCancelled(IntConsumer consumer, BooleanSupplier until) {
		throw new NotImplementedException();
	}

    @Override
	default int[] toArray() {
		throw new NotImplementedException();
	}

	@Override
	default Spliterator.OfInt spliterator() {
		throw new NotImplementedException();
	}

	@Override
	default boolean isParallel() {
		throw new NotImplementedException();
	}

	@Override
	default int getStreamFlags() {
		throw new NotImplementedException();
	}

}
