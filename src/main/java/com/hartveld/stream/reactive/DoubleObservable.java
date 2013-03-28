package com.hartveld.stream.reactive;

import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.FlatMapper;
import org.apache.commons.lang.NotImplementedException;

@FunctionalInterface
public interface DoubleObservable extends DoubleStream {

	AutoCloseable subscribe(DoubleConsumer onNext, Consumer<Exception> onError, Runnable onCompleted);

	default AutoCloseable subscribe(DoubleObserver observer) {
		return subscribe(observer::onNext, observer::onError, observer::onCompleted);
	}

	@Override
	default PrimitiveIterator.OfDouble iterator() {
		throw new NotImplementedException();
	}

	@Override
	default Spliterator.OfDouble spliterator() {
		throw new NotImplementedException();
	}

	@Override
	default Observable<Double> boxed() {
		return mapToObj((DoubleFunction<Double>) i -> Double.valueOf(i));
	}

	@Override
	default DoubleObservable map(DoubleUnaryOperator mapper) {
		throw new NotImplementedException();
	}

	@Override
	default <U> Observable<U> mapToObj(DoubleFunction<U> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default IntObservable mapToInt(DoubleToIntFunction mapper) {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable mapToLong(DoubleToLongFunction mapper) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable flatMap(DoubleFunction<? extends DoubleStream> mapper) {
		return flatMap((double i, DoubleConsumer sink) -> mapper.apply(i).sequential().forEach(sink));
	}

	@Override
	default DoubleObservable flatMap(FlatMapper.OfDoubleToDouble mapper) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable filter(DoublePredicate predicate) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable peek(DoubleConsumer consumer) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable sorted() {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable distinct() {
		// @@@ While functional and quick to implement this approach is not very efficient.
		//     An efficient version requires an double-specific map/set implementation.
		return boxed().distinct().mapToDouble(i -> (double) i);
	}

	@Override
	default DoubleObservable limit(long maxSize) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable substream(long startOffset) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable substream(long startOffset, long endOffset) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable sequential() {
		throw new UnsupportedOperationException();
	}

	@Override
	default DoubleObservable parallel() {
		throw new UnsupportedOperationException();
	}

	@Override
	default double reduce(double identity, DoubleBinaryOperator op) {
		throw new NotImplementedException();
	}

	@Override
	default OptionalDouble reduce(DoubleBinaryOperator op) {
		throw new NotImplementedException();
	}

	@Override
	default boolean anyMatch(DoublePredicate predicate) {
		throw new NotImplementedException();
	}

	@Override
	default boolean allMatch(DoublePredicate predicate) {
		throw new NotImplementedException();
	}

	@Override
	default boolean noneMatch(DoublePredicate predicate) {
		throw new NotImplementedException();
	}

	@Override
	default OptionalDouble findFirst() {
		throw new NotImplementedException();
	}

	@Override
	default OptionalDouble findAny() {
		throw new NotImplementedException();
	}

	@Override
	default void forEach(DoubleConsumer consumer) {
		throw new NotImplementedException();
	}

	@Override
	default void forEachOrdered(DoubleConsumer consumer) {
		throw new NotImplementedException();
	}

	@Override
	default void forEachUntilCancelled(DoubleConsumer consumer, BooleanSupplier until) {
		throw new NotImplementedException();
	}

	@Override
	default double[] toArray() {
		throw new NotImplementedException();
	}


	@Override
	default long count() {
		throw new NotImplementedException();
	}

	@Override
	default OptionalDouble average() {
		throw new NotImplementedException();
	}
	@Override
	default OptionalDouble min() {
		throw new NotImplementedException();
	}

	@Override
	default OptionalDouble max() {
		throw new NotImplementedException();
	}

	@Override
	default double sum() {
		throw new NotImplementedException();
	}

	@Override
	default boolean isParallel() {
		throw new UnsupportedOperationException();
	}

	@Override
	default <R> R collect(Supplier<R> resultFactory, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleSummaryStatistics summaryStatistics() {
		throw new NotImplementedException();
	}

}
