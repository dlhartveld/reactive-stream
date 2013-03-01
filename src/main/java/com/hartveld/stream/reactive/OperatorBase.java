package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

abstract class OperatorBase<T, R> implements Observable<R> {

	private final AtomicBoolean stopped = new AtomicBoolean(false);

	private final Observable<T> source;
	private AutoCloseable sourceSubscription;

	protected OperatorBase(final Observable<T> source) {
		checkNotNull(source, "source");

		this.source = source;
	}

	@Override
	public AutoCloseable subscribe(final Consumer<R> onNext, final Consumer<Throwable> onError, final Runnable onCompleted) {
		return source.subscribe(
			el -> {
				if (stopped.get()) {
					return;
				}

				try {
					onNext(el, onNext);
				} catch (Throwable t) {
					stopped.set(true);
					unsubscribe();
					onError(t, onError);
				}
			},
			ex -> {
				if (stopped.get()) {
					return;
				}

				stopped.set(true);
				onError(ex, onError);
			},
			() -> {
				if (stopped.get()) {
					return;
				}

				stopped.set(true);
				onCompleted(onCompleted);
			}
		);
	}

	protected synchronized final void unsubscribe() {
		if (this.sourceSubscription != null) {
			closeSourceSubscriptionQuietly();
			this.sourceSubscription = null;
		}
	}

	protected abstract void onNext(final T element, final Consumer<R> onNext);

	protected void onError(final Throwable exception, final Consumer<Throwable> onError) {
		onError.accept(exception);
	}

	protected void onCompleted(final Runnable onCompleted) {
		onCompleted.run();
	}

	private void closeSourceSubscriptionQuietly() {
		try {
			this.sourceSubscription.close();
		} catch (Exception e) { }
	}

}
