package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MergeOp<T> implements Observable<T> {

	private static final Logger LOG = LoggerFactory.getLogger(MergeOp.class);

	private boolean aStopped = false;
	private boolean bStopped = false;
	private boolean stopped = false;

	private final Observable<T> sourceA;
	private AutoCloseable subscriptionA;

	private final Observable<T> sourceB;
	private AutoCloseable subscriptionB;

	MergeOp(final Observable<T> sourceA, final Observable<T> sourceB) {
		checkNotNull(sourceA, "sourceA");
		checkNotNull(sourceB, "sourceB");

		this.sourceA = sourceA;
		this.sourceB = sourceB;
	}

	@Override
	public AutoCloseable subscribe(final Consumer<? super T> onNext, final Consumer<Exception> onError, final Runnable onCompleted) {
		LOG.trace("subscribe()");

		this.subscriptionA = this.sourceA.subscribe(
				el -> onNextA(el, onNext, onError),
				ex -> onErrorA(ex, onError),
				() -> onCompletedA(onCompleted)
		);

		this.subscriptionB = this.sourceB.subscribe(
				el -> onNextB(el, onNext, onError),
				ex -> onErrorB(ex, onError),
				() -> onCompletedB(onCompleted)
		);

		return this::onClose;
	}

	protected void onClose() throws Exception {
		LOG.trace("onClose()");

		unsubscribeFromSources();
	}

	protected void onNext(final T element, final Consumer<? super T> onNext, final Consumer<Exception> onError) {
		LOG.trace("onNext: {}", element);

		if (stopped) {
			return;
		}

		try {
			onNext.accept(element);
		} catch (Exception ex) {
			stopped = true;
			onError(ex, onError);
		}
	}

	protected void onError(final Exception exception, final Consumer<Exception> onError) {
		LOG.trace("onError: {}", exception.getMessage(), exception);

		if (stopped) {
			return;
		}

		stopped = true;

		try {
			unsubscribeFromSources();
		} catch (Exception e) {
			exception.addSuppressed(e);
		}

		onError.accept(exception);
	}

	protected void onCompleted(final Runnable onCompleted) {
		LOG.trace("onCompleted()");

		if (stopped) {
			return;
		}

		if (oneSourceCompleted()) {
			this.stopped = true;

			try {
				unsubscribeFromSources();
			} catch (Exception ex) {
				LOG.warn("Suppressed exception thrown while unsubscribing from sources after completion.", ex.getMessage(), ex);
			}

			onCompleted.run();
		}
	}

	private void onNextA(final T element, final Consumer<? super T> onNext, final Consumer<Exception> onError) {
		if (aStopped) {
			return;
		}

		onNext(element, onNext, onError);
	}

	private void onErrorA(final Exception exception, final Consumer<Exception> onError) {
		if (aStopped) {
			return;
		}

		aStopped = true;

		onError(exception, onError);
	}

	private void onCompletedA(final Runnable onCompleted) {
		if (aStopped) {
			return;
		}

		onCompleted(onCompleted);

		aStopped = true;
	}

	private void onNextB(final T element, final Consumer<? super T> onNext, final Consumer<Exception> onError) {
		if (bStopped) {
			return;
		}

		onNext(element, onNext, onError);
	}

	private void onErrorB(final Exception exception, final Consumer<Exception> onError) {
		if (bStopped) {
			return;
		}

		bStopped = true;

		onError(exception, onError);
	}

	private void onCompletedB(final Runnable onCompleted) {
		if (bStopped) {
			return;
		}

		onCompleted(onCompleted);

		bStopped = true;
	}

	private void unsubscribeFromSources() throws Exception {
		Exception outer = null;

		try {
			this.subscriptionA.close();
		} catch (Exception a) {
			outer = new Exception("Closing of one or more subscriptions failed", a);
		}

		try {
			this.subscriptionB.close();
		} catch (Exception b) {
			if (outer == null) {
				outer = new Exception("Closing of one or more subscriptions failed", b);
			} else {
				outer.addSuppressed(b);
			}
		}

		if (outer != null) {
			throw outer;
		}
	}

	private boolean oneSourceCompleted() {
		return this.aStopped || this.bStopped;
	}

}
