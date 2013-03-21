package com.hartveld.stream.reactive;

import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.stream.FlatMapper;
import java.util.stream.LongStatistics;
import java.util.stream.LongStream;
import org.apache.commons.lang.NotImplementedException;

public interface LongObservable extends LongStream {

	AutoCloseable subscribe(LongConsumer onNext, Consumer<Throwable> onError, Runnable onCompleted);

	default AutoCloseable subscribe(final LongObserver observer) {
		return subscribe(observer::onNext, observer::onError, observer::onCompleted);
	}

	@Override
	default DoubleObservable doubles() {
		throw new NotImplementedException();
	}

	@Override
	default Observable<Long> boxed() {
		return map((LongFunction<Long>) i -> Long.valueOf(i));
	}

	@Override
	default LongObservable map(LongUnaryOperator mapper) {
		throw new NotImplementedException();
	}

	@Override
	default <U> Observable<U> map(LongFunction<U> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default IntObservable map(LongToIntFunction mapper) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable map(LongToDoubleFunction mapper) {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable flatMap(LongFunction<? extends LongStream> mapper) {
		return flatMap((long i, LongConsumer sink) -> mapper.apply(i).sequential().forEach(sink));
	}

	@Override
	default LongObservable flatMap(FlatMapper.OfLongToLong mapper) {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable filter(LongPredicate predicate) {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable peek(LongConsumer consumer) {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable sorted() {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable distinct() {
		// @@@ While functional and quick to implement this approach is not very efficient.
		//     An efficient version requires an long-specific map/set implementation.
		return boxed().distinct().map(i -> (long) i);
	}

	@Override
	default LongObservable limit(long maxSize) {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable substream(long startOffset) {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable substream(long startOffset, long endOffset) {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable sequential() {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable parallel() {
		throw new NotImplementedException();
	}

	@Override
	default long reduce(long identity, LongBinaryOperator op) {
		throw new NotImplementedException();
	}

	@Override
	default OptionalLong reduce(LongBinaryOperator op) {
		throw new NotImplementedException();
	}

	@Override
	default boolean anyMatch(LongPredicate predicate) {
		throw new NotImplementedException();
	}

	@Override
	default boolean allMatch(LongPredicate predicate) {
		throw new NotImplementedException();
	}

	@Override
	default boolean noneMatch(LongPredicate predicate) {
		throw new NotImplementedException();
	}

	@Override
	default OptionalLong findFirst() {
		throw new NotImplementedException();
	}

	@Override
	default OptionalLong findAny() {
		throw new NotImplementedException();
	}

	@Override
	default void forEach(LongConsumer consumer) {
		throw new NotImplementedException();
	}

	@Override
	default void forEachUntilCancelled(LongConsumer consumer, BooleanSupplier until) {
		throw new NotImplementedException();
	}

	@Override
	default long[] toArray() {
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
	default OptionalLong min() {
		throw new NotImplementedException();
	}

	@Override
	default OptionalLong max() {
		throw new NotImplementedException();
	}

	@Override
	default LongStatistics statistics() {
		throw new NotImplementedException();
	}

	@Override
	default long sum() {
		throw new NotImplementedException();
	}


	@Override
	default PrimitiveIterator.OfLong iterator() {
		throw new NotImplementedException();
	}

	@Override
	default Spliterator.OfLong spliterator() {
		throw new NotImplementedException();
	}

	@Override
	default boolean isParallel() {
		throw new UnsupportedOperationException();
	}

	@Override
	default int getStreamFlags() {
		throw new NotImplementedException();
	}

	@Override
	default <R> R collect(Supplier<R> resultFactory, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner) {
		throw new NotImplementedException();
	}

}
