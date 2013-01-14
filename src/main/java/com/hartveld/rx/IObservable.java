package com.hartveld.rx;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IObservable<T> {

	static final Logger LOG = LoggerFactory.getLogger(IObservable.class);

	AutoCloseable subscribe(Procedure1<T> onNext, Procedure1<Throwable> onError, Procedure onCompleted);

	/**
	 * Map observations through a mapping function to new other observations.
	 *
	 * @param mapper The function used to do the mapping.
	 *
	 * @return A new {@link IObservable} that forwards the result of the application of the mapper to each observation to client subscriptions.
	 */
	default <R> IObservable<R> map(Function1<R, T> mapper) {
		LOG.trace("map()");

		checkNotNull(mapper, "mapper must be non-null");

		return (onNext, onError, onCompleted) -> {
			AtomicBoolean stopped = new AtomicBoolean(false);
			AutoCloseable ac = subscribe(
				e -> {
					if (stopped.get()) return;
					try {
						R inner = mapper.function(e);
						onNext.procedure(inner);
					} catch (RuntimeException ex) {
						LOG.trace("Caught exception: {}", ex.getMessage(), ex);
						stopped.set(true);
						onError.procedure(ex);
					}
				},
				e -> {
					if (stopped.get()) return;
					onError.procedure(e);
				},
				() -> {
					if (stopped.get()) return;
					stopped.set(true);
					onCompleted.procedure();
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
			AutoCloseable ac = subscribe(
				e -> {
					LOG.trace("Executing onNext asynchronously for: {}", e);
					executor.execute(() -> {
						LOG.trace("onNext({}) (asynchronously called)", e);
						onNext.procedure(e);
					});
				},
				e -> {
					LOG.trace("Executing onError asynchronously for: {}", e);
					executor.execute(() -> {
						LOG.trace("onNext({}) (asynchronously called)", e);
						onError.procedure(e);
					});
				},
				() -> {
					LOG.trace("Executing onCompleted asynchronously...");
					executor.execute(() -> {
						LOG.trace("onCompleted() (asynchronously called)");
						onCompleted.procedure();
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
			FutureAutoCloseable futureAC = new FutureAutoCloseable();

			executor.execute(() -> {
					LOG.trace("Executing asynchronous subscription");
					futureAC.set(subscribe(
						e -> onNext.procedure(e),
						e -> onError.procedure(e),
						() -> onCompleted.procedure()
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
