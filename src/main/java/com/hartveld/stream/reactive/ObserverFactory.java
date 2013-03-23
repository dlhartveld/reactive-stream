package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;

/**
 * Factory that creates {@link Observer}s.
 */
public class ObserverFactory {

	/**
	 * Create a new {@link Observer} with the given lambda expressions as notification handlers.
	 *
	 * @param <T>         The type of value that the {@link Observer} expects.
	 * @param onNext      A lambda expression that executes on notification for new values. Must be
	 *                       non-<code>null</code>. See also {@link Observer#onNext(java.lang.Object)}.
	 * @param onError     A lambda expression that executes on error notification. Must be non-<code>null</code>. See
	 *                       also {@link Observer#onError(java.lang.Exception)}.
	 * @param onCompleted A lambda expression that executes on completion of the observable. Must be
	 *                       non-<code>null</code>. See also {@link Observer#onCompleted()}.
	 *
	 * @return The newly created {@link Observer}.
	 *
	 * @see Observer
	 * @see Observable
	 */
	public static <T> Observer<T> createObserver(Consumer<T> onNext, Consumer<Exception> onError, Runnable onCompleted) {
		checkNotNull(onNext, "onNext must be non-null");
		checkNotNull(onError, "onError must be non-null");
		checkNotNull(onCompleted, "onCompleted must be non-null");

		return new ForwardingObserver<>(onNext, onError, onCompleted);
	}

	private ObserverFactory() {
		throw new UnsupportedOperationException();
	}

}
