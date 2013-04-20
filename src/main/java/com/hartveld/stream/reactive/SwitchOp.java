package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SwitchOp<T> implements Observable<T> {

	private static final Logger LOG = LoggerFactory.getLogger(SwitchOp.class);

	private final AtomicBoolean stopped = new AtomicBoolean();

	private final Observable<Observable<T>> source;
	private ForwardingAutoCloseable subscription = null;

	private AutoCloseable currentSubscription = null;

	SwitchOp(final Observable<T> source) throws ClassCastException {
		checkNotNull(source, "source");

		/* The cast is purely cosmetical - the generic component of the type is ignored.
		 * Only when evaluating the observable in onNext will a ClassCastException be thrown. */
		this.source = (Observable<Observable<T>>) source;
	}

	@Override
	public final AutoCloseable subscribe(final Consumer<? super T> onNext, final Consumer<Exception> onError, final Runnable onCompleted) {
		LOG.trace("Subscribe()");

		checkNotNull(onNext, "onNext");
		checkNotNull(onError, "onError");
		checkNotNull(onCompleted, "onCompleted");

		this.subscription = new ForwardingAutoCloseable();

		this.subscription.set(source.subscribe(
				src -> onNext(src, onNext, onError, onCompleted),
				ex -> onError(ex, onError),
				() -> onCompleted(onCompleted)
		));

		return () -> stop();
	}

	private synchronized void onNext(final Observable<T> newSource, final Consumer<? super T> onNext, final Consumer<Exception> onError, final Runnable onCompleted) {
		LOG.trace("onNext()");

		checkNotNull(newSource, "newSource");
		checkNotNull(onNext, "onNext");
		checkNotNull(onError, "onError");
		checkNotNull(onCompleted, "onCompleted");

		if (stopped.get()) {
			return;
		}

		if (!(newSource instanceof Observable)) {
			throw new RuntimeException("Element is not an Observable");
		}

		try {
			if (this.currentSubscription != null) {
				this.currentSubscription.close();
			}

			this.currentSubscription = newSource.subscribe(onNext, onError, onCompleted);
		} catch (final Exception exception) {
			LOG.trace("Something went wrong: {}", exception.getMessage(), exception);

			try {
				stop();
			} catch (final Exception e) {
				exception.addSuppressed(e);
			}

			onError.accept(exception);
		}
	}

	private synchronized void onError(final Exception exception, final Consumer<Exception> onError) {
		LOG.trace("onError(): {}", exception.getMessage());

		checkNotNull(exception, "exception");
		checkNotNull(onError, "onError");

		if (stopped.get()) {
			return;
		}

		try {
			stop();
		} catch (final Exception e) {
			exception.addSuppressed(e);
		}

		onError.accept(exception);
	}

	private synchronized void onCompleted(final Runnable onCompleted) {
		LOG.trace("onCompleted()");

		checkNotNull(onCompleted, "onCompleted");

		if (stopped.get()) {
			return;
		}

		try {
			stop();
		} catch (final Exception e) {
			LOG.trace("Ignoring exception during stop() on completion of source: {}", e.getMessage(), e);
		}

		try {
			onCompleted.run();
		} catch (final Exception e) {
			LOG.trace("Ignoring exception during onCompleted(): {}", e.getMessage(), e);
		}
	}

	private synchronized void stop() throws Exception {
		LOG.trace("stop()");

		if (this.stopped.get()) {
			return;
		}

		stopped.set(true);

		unsubscribe();
	}

	private void unsubscribe() throws Exception {
		LOG.trace("Unsubscribing from sources ...");

		AutoCloseables.closeAllIfNotNull(this.subscription, this.currentSubscription);
	}

}
