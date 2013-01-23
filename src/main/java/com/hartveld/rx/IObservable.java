package com.hartveld.rx;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Block;
import java.util.function.Function;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IObservable<T> {

	static final Logger LOG = LoggerFactory.getLogger(IObservable.class);

	AutoCloseable subscribe(Block<T> onNext, Block<Throwable> onError, Runnable onCompleted);

	default AutoCloseable subscribe(IObserver<T> observer) {
		return subscribe(observer::onNext, observer::onError, observer::onCompleted);
	}

	/**
	 * The identity function, i.e. every observation is forwarded as-is.
	 * <p>
	 * This function can for example come in handy during testing.
	 *
	 * @return The new {@link IObservable} that forwards all observations.
	 */
	default IObservable<T> id() {
		LOG.trace("id()");

		return (onNext, onError, onCompleted) -> {
			AtomicBoolean stopped = new AtomicBoolean(false);
			AutoCloseable ac = subscribe(
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
			return () -> ac.close();
		};
	}

	/**
	 * Map observations through a mapping function to new other observations.
	 *
	 * @param mapper The function used to do the mapping.
	 *
	 * @return A new {@link IObservable} that forwards the result of the application of the mapper to each observation to client subscriptions.
	 */
	default <R> IObservable<R> map(Function<T, R> mapper) {
		LOG.trace("map()");

		checkNotNull(mapper, "mapper must be non-null");

		return (onNext, onError, onCompleted) -> {
			AtomicBoolean stopped = new AtomicBoolean(false);
			AutoCloseable ac = subscribe(
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
			return () -> ac.close();
		};
	}

	/**
	 * Filter observations based on given predicate.
	 *
	 * @param predicate The {@link Predicate} against which each observation is tested. Must be non-<code>null</code>.
	 *
	 * @return A new {@link IObservable} that filters observations against the given predicate.
	 */
	default IObservable<T> filter(Predicate<T> predicate) {
		LOG.trace("filter()");

		checkNotNull(predicate, "predicate must be non-null");

		return (onNext, onError, onCompleted) -> {
			AtomicBoolean stopped = new AtomicBoolean(false);
			AutoCloseable ac = subscribe(
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
			return () -> ac.close();
		};
	}

	/**
	 * Execute observations with the given executor.
	 * <p>
	 * This operator can be used to schedule the execution of observations on another thread, for example to run them on a background thread.
	 *
	 * @param executor The {@link Executor} to execute the observations through. Must be non-<code>null</code>.
	 *
	 * @return A new {@link IObservable} that executes observations for subscribers through the given executor.
	 */
	default IObservable<T> observeOn(Executor executor) {
		LOG.trace("observeOn({})", executor);

		checkNotNull(executor, "executor must be non-null");

		return (onNext, onError, onCompleted) -> {
			AtomicBoolean stopped = new AtomicBoolean(false);
			AutoCloseable ac = subscribe(
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
			return () -> ac.close();
		};
	}
	
	/**
	 * Execute subscription and closing of the subscription with the given executor.
	 *
	 * @param executor The {@link Executor} to execute the subscription and closing of the subscription. Must be non-<code>null</code>.
	 *
	 * @return A new {@link IObservable} that executes subscription and closing of subscription through the given executor.
	 */
	default IObservable<T> subscribeOn(Executor executor) {
		LOG.trace("subscribeOn({})", executor);

		return (onNext, onError, onCompleted) -> {
			AtomicBoolean stopped = new AtomicBoolean(false);
			FutureAutoCloseable futureAC = new FutureAutoCloseable();

			executor.execute(() -> {
					LOG.trace("Executing asynchronous subscription");
					futureAC.set(subscribe(
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
						futureAC.close();
					} catch (Exception e) {
						LOG.trace("Caught exception on close: {}", e.getMessage(), e);
						// TODO: Create test case for this scenario, then implement a proper handling.
					}
				});
			};
		};
	}

}
