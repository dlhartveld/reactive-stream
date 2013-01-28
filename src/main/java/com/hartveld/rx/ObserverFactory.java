package com.hartveld.rx;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Block;

/**
 * Factory that creates {@link IObserver}s.
 */
public class ObserverFactory {

	/**
	 * Create a new {@link IObserver} with the given lambda expressions as notification handlers.
	 *
	 * @param <T>         The type of value that the {@link IObserver} expects.
	 * @param onNext      A lambda expression that executes on notification for new values. Must be
	 *                       non-<code>null</code>. See also {@link IObserver#onNext(java.lang.Object)}.
	 * @param onError     A lambda expression that executes on error notification. Must be non-<code>null</code>. See
	 *                       also {@link IObserver#onError(java.lang.Throwable)}.
	 * @param onCompleted A lambda expression that executes on completion of the observable. Must be
	 *                    non-<code>null</code>. See also {@link IObserver#onCompleted()}.
	 *
	 * @return The newly created {@link IObserver}.
	 *
	 * @see {@link IObserver}
	 * @see {@link IObservable}
	 */
	public static <T> IObserver<T> createObserver(Block<T> onNext, Block<Throwable> onError, Runnable onCompleted) {
		checkNotNull(onNext, "onNext must be non-null");
		checkNotNull(onError, "onError must be non-null");
		checkNotNull(onCompleted, "onCompleted must be non-null");

		return new IObserver<T>() {
			@Override
			public void onNext(T value) {
				onNext.accept(value);
			}

			@Override
			public void onError(Throwable e) {
				onError.accept(e);
			}

			@Override
			public void onCompleted() {
				onCompleted.run();
			}
		};
	}

	private ObserverFactory() {
		throw new UnsupportedOperationException();
	}

}
