package com.hartveld.stream.reactive.subjects;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hartveld.stream.reactive.ForwardingAutoCloseable;
import com.hartveld.stream.reactive.Observer;
import com.hartveld.stream.reactive.ObserverFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSubject<T, R, Source> implements Subject<T, R> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractSubject.class);

	private final Map<AutoCloseable, Observer<R>> observers = new ConcurrentHashMap<>();

	private boolean stopped = false;

	protected final boolean isStopped() {
		return this.stopped;
	}

	@Override
	public AutoCloseable subscribe(final Observer<R> observer) {
		LOG.trace("Subscribing new observer: {}", observer);

		final ForwardingAutoCloseable<Source> fac = new ForwardingAutoCloseable<>();
		fac.set(() -> {
			if (observers.containsKey(fac)) {
				observers.remove(fac);

				final Source source = fac.getSource();
				if (source != null) {
					onClose(source);
				}
			}
		});

		observers.put(fac, observer);

		final Source source = onSubscribe(observer);
		if (source != null) {
			fac.setSource(source);
		}

		return fac;
	}

	@Override
	public final AutoCloseable subscribe(final Consumer<R> onNext, final Consumer<Exception> onError, final Runnable onCompleted) {
		LOG.trace("Subscribing new forwarding observer ...");

		checkNotNull(onNext, "onNext");
		checkNotNull(onError, "onError");
		checkNotNull(onCompleted, "onCompleted");

		return this.subscribe(ObserverFactory.createObserver(onNext, onError, onCompleted));
	}

	/**
	 * Broadcast the given value to all subscribed observers.
	 *
	 * @param value The value to broadcast. Must be non-<code>null</code>.
	 */
	protected final void broadcastNext(final R value) {
		LOG.trace("broadcastNext(): {}", value);

		checkNotNull(value, "value");

		if (isStopped()) {
			return;
		}

		// TODO: Check for errors.
		observers.values().stream().forEach(observer -> observer.onNext(value));
	}

	@Override
	public void onError(final Exception exception) {
		LOG.trace("onError(): {}", exception.getMessage(), exception);

		checkNotNull(exception, "exception");

		if (stopped) {
			return;
		}

		stopped = true;

		// TODO: Check for errors.
		observers.values().stream().forEach(observer -> observer.onError(exception));
	}

	@Override
	public void onCompleted() {
		LOG.trace("onCompleted()");

		if (stopped) {
			return;
		}

		stopped = true;

		// TODO: Check for errors.
		observers.values().stream().forEach(observer -> observer.onCompleted());
	}

	protected Source onSubscribe(final Observer<R> observer) { return null; }

	protected void onClose(final Source source) { }

}
