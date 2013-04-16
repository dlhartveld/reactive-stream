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
	public AutoCloseable subscribe(final Consumer<? super R> onNext, final Consumer<Exception> onError, final Runnable onCompleted) {
		this.sourceSubscription = source.subscribe(
			el -> {
				if (stopped.get()) {
					return;
				}

				try {
					onNext(el, onNext);
				} catch (Exception t) {
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

		return this.sourceSubscription;
	}

	protected synchronized final void unsubscribe() {
		if (this.sourceSubscription != null) {
			closeSourceSubscriptionQuietly();
			this.sourceSubscription = null;
		}
	}

	protected abstract void onNext(final T element, final Consumer<? super R> onNext);

	protected void onError(final Exception exception, final Consumer<Exception> onError) {
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
