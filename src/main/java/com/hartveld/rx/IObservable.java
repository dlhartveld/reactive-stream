package com.hartveld.rx;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IObservable<T> {

	static final Logger LOG = LoggerFactory.getLogger(IObservable.class);

	AutoCloseable subscribe(Procedure1<T> onNext, Procedure1<Throwable> onError, Procedure onCompleted);

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
		checkNotNull(executor, "executor must be non-null");

		return (onNext, onError, onCompleted) -> {
			AutoCloseable ac = IObservable.this.subscribe(
				e -> executor.execute(() -> onNext.procedure(e)),
				e -> executor.execute(() -> onError.procedure(e)),
				() -> executor.execute(() -> onCompleted.procedure())
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
		return (onNext, onError, onCompleted) -> {
			FutureAutoCloseable futureAC = new FutureAutoCloseable();

			executor.execute(() -> {
					futureAC.set(IObservable.this.subscribe(
						e -> onNext.procedure(e),
						e -> onError.procedure(e),
						() -> onCompleted.procedure()
					));
				}
			);

			return () -> executor.execute(() -> {
				try {
					futureAC.close();
				} catch (Exception e) { 
					// TODO: Create test case for this scenario, then implement a proper handling.
				}
			});
		};
	}

}
