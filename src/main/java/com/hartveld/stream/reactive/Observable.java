package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hartveld.stream.reactive.concurrency.Scheduler;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.FlatMapper;
import java.util.stream.Stream;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FunctionalInterface
public interface Observable<T> extends Stream<T> {

	static final Logger LOG = LoggerFactory.getLogger(Observable.class);

	/**
	 * Subscribe to this {@link Observable}.
	 *
	 * @param onNext The {@link Consumer} that is called when a new value is available. Must be non-<code>null</code>.
	 * @param onError The {@link Consumer} that is called when an error occurs. Must be non-<code>null</code>.
	 * @param onCompleted The {@link Runnable} that is called when the final value has been observed. Must be non-<code>null</code>.
	 *
	 * @return An {@link AutoCloseable} that can be used to cancel the subscription.
	 */
	AutoCloseable subscribe(Consumer<T> onNext, Consumer<Exception> onError, Runnable onCompleted);

	/**
	 * Subscribe to this {@link Observable}.
	 *
	 * @param onNext The {@link Consumer} that is called when a new value is available. Must be non-<code>null</code>.
	 *
	 * @return An {@link AutoCloseable} that can be used to cancel the subscription.
	 */
	default AutoCloseable subscribe(Consumer<T> onNext) {
		return subscribe(onNext, ex -> { }, () -> { });
	}

	/**
	 * Subscribe to this {@link Observable}.
	 *
	 * @param onNext The {@link Consumer} that is called when a new value is available. Must be non-<code>null</code>.
	 * @param onError The {@link Consumer} that is called when an error occurs. Must be non-<code>null</code>.
	 *
	 * @return An {@link AutoCloseable} that can be used to cancel the subscription.
	 */
	default AutoCloseable subscribe(Consumer<T> onNext, Consumer<Exception> onError) {
		return subscribe(onNext, onError, () -> { });
	}

	/**
	 * Subscribe to this {@link Observable}.
	 *
	 * @param onNext The {@link Consumer} that is called when a new value is available. Must be non-<code>null</code>.
	 * @param onCompleted The {@link Runnable} that is called when the final value has been observed. Must be non-<code>null</code>.
	 *
	 * @return An {@link AutoCloseable} that can be used to cancel the subscription.
	 */
	default AutoCloseable subscribe(Consumer<T> onNext, Runnable onCompleted) {
		return subscribe(onNext, ex -> { }, onCompleted);
	}

	/**
	 * Subscribe to this {@link Observable}.
	 *
	 * @param observer The observer to subscribe. Must be non-<code>null</code>.
	 *
	 * @return The {@link AutoCloseable} that can be used to cancel the subscription.
	 */
	default AutoCloseable subscribe(Observer<T> observer) {
		return subscribe(observer::onNext, observer::onError, observer::onCompleted);
	}

	/**
	 * The identity function, i.e. every observation is forwarded as-is.
	 * <p>
	 * This function can for example come in handy during testing.
	 *
	 * @return The new {@link Observable} that forwards all observations.
	 */
	default Observable<T> id() {
		LOG.trace("id()");

		return new IdOp<>(this);
	}

	/**
	 * Filter observations based on given predicate.
	 *
	 * @param predicate The {@link Predicate} against which each observation is tested. Must be non-<code>null</code>.
	 *
	 * @return A new {@link Observable} that filters observations against the given predicate.
	 */
	@Override
	default Observable<T> filter(final Predicate<? super T> predicate) {
		LOG.trace("filter()");

		checkNotNull(predicate, "predicate must be non-null");

		return new FilterOp<>(this, predicate);
	}

	/**
	 * Map observations through a mapping function to new other observations.
	 *
	 * @param mapper The function used to do the mapping.
	 *
	 * @return A new {@link Observable} that forwards the result of the application of the mapper to each observation to client subscriptions.
	 */
	@Override
	default <R> Observable<R> map(final Function<? super T, ? extends R> mapper) {
		LOG.trace("map()");

		checkNotNull(mapper, "mapper must be non-null");

		return new MapOp<>(this, mapper);
	}

	@Override
	default IntObservable mapToInt(ToIntFunction<? super T> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable mapToLong(ToLongFunction<? super T> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable mapToDouble(ToDoubleFunction<? super T> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default <R> Observable<R> flatMap(final Function<T, Stream<? extends R>> mapper) {
		LOG.trace("flatMap()");

        // We can do better than this, by polling cancellationRequested when stream is infinite
        return flatMap((T t, Consumer<R> sink) -> mapper.apply(t).sequential().forEach(sink));
    }

	@Override
	default <R> Observable<R> flatMap(final FlatMapper<? super T, R> mapper) {
		LOG.trace("flatMap()");

		checkNotNull(mapper, "mapper");

		return new FlatMapOp<>(this, mapper);
	}

	@Override
	default IntObservable flatMapToInt(FlatMapper.ToInt<? super T> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable flatMapToLong(FlatMapper.ToLong<? super T> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable flatMapToDouble(FlatMapper.ToDouble<? super T> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default Observable<T> distinct() {
		throw new NotImplementedException();
	}

	@Override
	default Observable<T> sorted() {
		throw new NotImplementedException();
	}

	@Override
	default Observable<T> sorted(Comparator<? super T> comparator) {
		throw new NotImplementedException();
	}

	@Override
	default void forEach(Consumer<? super T> consumer) {
		throw new NotImplementedException();
	}

	@Override
	default void forEachOrdered(Consumer<? super T> consumer) {
		throw new NotImplementedException();
	}

	@Override
	default Observable<T> peek(Consumer<? super T> consumer) {
		throw new NotImplementedException();
	}

	@Override
	default Observable<T> limit(long maxSize) {
		throw new NotImplementedException();
	}

	@Override
	default Observable<T> substream(long startingOffset) {
		throw new NotImplementedException();
	}

	@Override
	default Observable<T> substream(long startingOffset, long endingOffset) {
		throw new NotImplementedException();
	}

	@Override
	default <A> A[] toArray(IntFunction<A[]> generator) {
		throw new NotImplementedException();
	}

	@Override
	default T reduce(T identity, BinaryOperator<T> reducer) {
		throw new NotImplementedException();
	}

	@Override
	default Optional<T> reduce(BinaryOperator<T> reducer) {
		throw new NotImplementedException();
	}

	@Override
	default <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> reducer) {
		throw new NotImplementedException();
	}

	@Override
	default <R> R collect(Supplier<R> resultFactory, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> reducer) {
		throw new NotImplementedException();
	}

	@Override
	default <R> R collect(Collector<? super T, R> collector) {
		throw new NotImplementedException();
	}

	@Override
	default <R> R collectUnordered(Collector<? super T, R> collector) {
		throw new NotImplementedException();
	}

	@Override
	default boolean anyMatch(Predicate<? super T> predicate) {
		throw new NotImplementedException();
	}

    @Override
	default boolean allMatch(Predicate<? super T> predicate) {
		throw new NotImplementedException();
	}

	@Override
	default boolean noneMatch(Predicate<? super T> predicate) {
		throw new NotImplementedException();
	}

	@Override
	default Optional<T> findFirst() {
		throw new NotImplementedException();
	}

	@Override
	default Optional<T> findAny() {
		throw new NotImplementedException();
	}

	@Override
	default Stream<T> sequential() {
		throw new NotImplementedException();
	}

	@Override
	default Stream<T> parallel() {
		throw new NotImplementedException();
	}

	@Override
	default long count() {
		throw new UnsupportedOperationException();
	}

	@Override
	default Optional<T> min(Comparator<? super T> comparator) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Optional<T> max(Comparator<? super T> comparator) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Execute observations with the given scheduler.
	 * <p>
	 * This operator can be used to schedule the execution of observations on another thread, for example to run them on a background thread.
	 *
	 * @param <A> TODO
	 * @param <R> TODO
	 *
	 * @param scheduler The {@link Scheduler} to execute the observations through. Must be non-<code>null</code>.
	 *
	 * @return A new {@link Observable} that executes observations for subscribers through the given scheduler.
	 */
	default <A, R> Observable<T> observeOn(final Scheduler<A, R> scheduler) {
		LOG.trace("observeOn({})", scheduler);

		checkNotNull(scheduler, "scheduler must be non-null");

		return new ObserveOnOp<>(this, scheduler);
	}

	/**
	 * Execute subscription and closing of the subscription with the given scheduler.
	 *
	 * @param <A> TODO
	 * @param <R> TODO
	 *
	 * @param scheduler The {@link Scheduler} to execute the subscription and closing of the subscription. Must be non-<code>null</code>.
	 *
	 * @return A new {@link Observable} that executes subscription and closing of subscription through the given scheduler.
	 */
	default <A,R> Observable<T> subscribeOn(final Scheduler<A, R> scheduler) {
		LOG.trace("subscribeOn({})", scheduler);

		checkNotNull(scheduler, "scheduler must be non-null");

		return new SubscribeOnOp<>(this, scheduler);
	}

	@Override
	default Iterator<T> iterator() {
		throw new NotImplementedException();
	}

	@Override
	default Spliterator<T> spliterator() {
		throw new NotImplementedException();
	}

	@Override
	default boolean isParallel() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Throttle the incoming elements during the given time frame.
	 *
	 * @param time The number of time units to wait between forwarding of elements.
	 * @param unit The size of the time unit.
	 *
	 * @return A throttled {@link Observable}.
	 */
	default Observable<T> throttle(final int time, final TimeUnit unit) {
		return new ThrottleOp<>(this, time, unit);
	}

	/**
	 * Merge two observables into one.
	 * <p>
	 * When an error occurs in either of the streams, a notification is sent to subscribers, after which no more notifications will follow from either stream.
	 *
	 * @param that The other {@link Observable}.
	 *
	 * @return The merged observable stream.
	 */
	default Observable<T> merge(final Observable<T> that) {
		return new MergeOp<>(this, that);
	}

}
