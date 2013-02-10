package com.hartveld.rx;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;

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
	 *                       non-<code>null</code>. See also {@link IObserver#onCompleted()}.
	 *
	 * @return The newly created {@link IObserver}.
	 *
	 * @see IObserver
	 * @see IObservable
	 */
	public static <T> IObserver<T> createObserver(Consumer<T> onNext, Consumer<Throwable> onError, Runnable onCompleted) {
		checkNotNull(onNext, "onNext must be non-null");
		checkNotNull(onError, "onError must be non-null");
		checkNotNull(onCompleted, "onCompleted must be non-null");

		return new ForwardingObserver<>(onNext, onError, onCompleted);
	}

	private ObserverFactory() {
		throw new UnsupportedOperationException();
	}

}
