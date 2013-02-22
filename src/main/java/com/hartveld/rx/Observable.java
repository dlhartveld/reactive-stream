package com.hartveld.rx;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.BooleanSupplier;
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
	AutoCloseable subscribe(Consumer<T> onNext, Consumer<Throwable> onError, Runnable onCompleted);

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
	default AutoCloseable subscribe(Consumer<T> onNext, Consumer<Throwable> onError) {
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

		return (onNext, onError, onCompleted) -> {
			AtomicBoolean stopped = new AtomicBoolean(false);
			return subscribe(
				el -> {
					if (stopped.get()) return;
					onNext.accept(el);
				},
				ex -> {
					if (stopped.get()) return;
					stopped.set(true);
					onError.accept(ex);
				},
				() -> {
					if (stopped.get()) return;
					stopped.set(true);
					onCompleted.run();
				}
			);
		};
	}

	/**
	 * Filter observations based on given predicate.
	 *
	 * @param predicate The {@link Predicate} against which each observation is tested. Must be non-<code>null</code>.
	 *
	 * @return A new {@link Observable} that filters observations against the given predicate.
	 */
	@Override
	default Observable<T> filter(Predicate<? super T> predicate) {
		LOG.trace("filter()");

		checkNotNull(predicate, "predicate must be non-null");

		return (onNext, onError, onCompleted) -> {
			AtomicBoolean stopped = new AtomicBoolean(false);
			return subscribe(
				el -> {
					if (stopped.get()) return;
					if (predicate.test(el)) {
						onNext.accept(el);
					}
				},
				ex -> {
					if (stopped.get()) return;
					stopped.set(true);
					onError.accept(ex);
				},
				() -> {
					if (stopped.get()) return;
					stopped.set(true);
					onCompleted.run();
				}
			);
		};
	}

	/**
	 * Map observations through a mapping function to new other observations.
	 *
	 * @param mapper The function used to do the mapping.
	 *
	 * @return A new {@link Observable} that forwards the result of the application of the mapper to each observation to client subscriptions.
	 */
	@Override
	default <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
		LOG.trace("map()");

		checkNotNull(mapper, "mapper must be non-null");

		return (onNext, onError, onCompleted) -> {
			AtomicBoolean stopped = new AtomicBoolean(false);
			return subscribe(
				e -> {
					if (stopped.get()) return;
					try {
						R inner = mapper.apply(e);
						onNext.accept(inner);
					} catch (RuntimeException ex) {
						LOG.trace("Caught exception: {}", ex.getMessage(), ex);
						stopped.set(true);
						onError.accept(ex);
					}
				},
				e -> {
					if (stopped.get()) return;
					stopped.set(true);
					onError.accept(e);
				},
				() -> {
					if (stopped.get()) return;
					stopped.set(true);
					onCompleted.run();
				}
			);
		};
	}

	@Override
	default IntObservable map(ToIntFunction<? super T> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable map(ToLongFunction<? super T> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable map(ToDoubleFunction<? super T> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default <R> Observable<R> flatMap(Function<T, Stream<? extends R>> mapper) {
        // We can do better than this, by polling cancellationRequested when stream is infinite
        return flatMap((T t, Consumer<R> sink) -> mapper.apply(t).sequential().forEach(sink));
    }

	@Override
	default <R> Observable<R> flatMap(FlatMapper<? super T, R> mapper) {
		return (onNext, onError, onCompleted) -> {
			final AtomicBoolean completed = new AtomicBoolean(false);
			return subscribe(
				el -> {
					if (completed.get()) return;

					try {
						mapper.explodeInto(el, elem -> onNext.accept(elem));
					} catch (Throwable t) {
						completed.set(true);
						onError.accept(t);
					}
				},
				ex -> {
					if (completed.get()) return;

					completed.set(true);

					onError.accept(ex);
				},
				() -> {
					if (completed.get()) return;

					completed.set(true);

					onCompleted.run();
				}
			);
		};
	}

	@Override
	default IntObservable flatMap(FlatMapper.ToInt<? super T> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default LongObservable flatMap(FlatMapper.ToLong<? super T> mapper) {
		throw new NotImplementedException();
	}

	@Override
	default DoubleObservable flatMap(FlatMapper.ToDouble<? super T> mapper) {
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
	default void forEachUntil(Consumer<? super T> consumer, BooleanSupplier until) {
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

	/**
	 * Execute observations with the given executor.
	 * <p>
	 * This operator can be used to schedule the execution of observations on another thread, for example to run them on a background thread.
	 *
	 * @param executor The {@link Executor} to execute the observations through. Must be non-<code>null</code>.
	 *
	 * @return A new {@link Observable} that executes observations for subscribers through the given executor.
	 */
	default Observable<T> observeOn(Executor executor) {
		LOG.trace("observeOn({})", executor);

		checkNotNull(executor, "executor must be non-null");

		return (onNext, onError, onCompleted) -> {
			AtomicBoolean stopped = new AtomicBoolean(false);
			return subscribe(
				e -> {
					if(stopped.get()) return;
					LOG.trace("Executing onNext asynchronously for: {}", e);
					executor.execute(() -> {
						LOG.trace("onNext({}) (asynchronously called)", e);
						onNext.accept(e);
					});
				},
				e -> {
					if(stopped.get()) return;
					LOG.trace("Executing onError asynchronously for: {}", e);
					executor.execute(() -> {
						LOG.trace("onNext({}) (asynchronously called)", e);
						stopped.set(true);
						onError.accept(e);
					});
				},
				() -> {
					if(stopped.get()) return;
					LOG.trace("Executing onCompleted asynchronously...");
					executor.execute(() -> {
						LOG.trace("onCompleted() (asynchronously called)");
						stopped.set(true);
						onCompleted.run();
					});
				}
			);
		};
	}

	/**
	 * Execute subscription and closing of the subscription with the given executor.
	 *
	 * @param executor The {@link Executor} to execute the subscription and closing of the subscription. Must be non-<code>null</code>.
	 *
	 * @return A new {@link Observable} that executes subscription and closing of subscription through the given executor.
	 */
	default Observable<T> subscribeOn(Executor executor) {
		LOG.trace("subscribeOn({})", executor);

		checkNotNull(executor, "executor must be non-null");

		return (onNext, onError, onCompleted) -> {
			AtomicBoolean stopped = new AtomicBoolean(false);
			ForwardingAutoCloseable fac = new ForwardingAutoCloseable();

			executor.execute(() -> {
					LOG.trace("Executing asynchronous subscription");
					fac.set(subscribe(
						e -> {
							if(stopped.get()) return;
							onNext.accept(e);
						},
						e -> {
							if(stopped.get()) return;
							onError.accept(e);
							stopped.set(true);
						},
						() -> {
							if(stopped.get()) return;
							onCompleted.run();
							stopped.set(true);
						}
					));
				}
			);

			return () -> {
				LOG.trace("Executing asynchronous close...");
				executor.execute(() -> {
					LOG.trace("Executing close...");
					try {
						fac.close();
					} catch (Exception e) {
						LOG.trace("Caught exception on close: {}", e.getMessage(), e);
						// TODO: Create test case for this scenario, then implement a proper handling.
					}
				});
			};
		};
	}

	@Override
	default Spliterator<T> spliterator() {
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
