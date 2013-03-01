package com.hartveld.stream.reactive.subjects;

import com.hartveld.stream.reactive.ForwardingAutoCloseable;
import com.hartveld.stream.reactive.Observer;
import com.hartveld.stream.reactive.ObserverFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicSubject<T, Source> implements Subject<T> {

	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	private static final Logger LOG = LoggerFactory.getLogger(BasicSubject.class);

	// TODO: Change to a concurrent list.
	private final Map<AutoCloseable, Observer<T>> observers = new ConcurrentHashMap<>();

	private boolean stopped = false;

	@Override
	public AutoCloseable subscribe(final Observer<T> observer) {
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
	public final AutoCloseable subscribe(final Consumer<T> onNext, final Consumer<Throwable> onError, final Runnable onCompleted) {
		LOG.trace("Subscribing new forwarding observer ...");
		return this.subscribe(ObserverFactory.createObserver(onNext, onError, onCompleted));
	}

	@Override
	public void onNext(final T value) {
		LOG.trace("onNext(): {}", value);

		if (stopped) {
			return;
		}

		observers.values().stream().forEach(observer -> observer.onNext(value));
	}

	@Override
	public void onError(final Throwable cause) {
		LOG.trace("onError(): {}", cause.getMessage(), cause);

		if (stopped) {
			return;
		}

		stopped = true;

		observers.values().stream().forEach(observer -> observer.onError(cause));
	}

	@Override
	public void onCompleted() {
		LOG.trace("onCompleted()");

		if (stopped) {
			return;
		}

		stopped = true;

		observers.values().stream().forEach(observer -> observer.onCompleted());
	}

	protected Source onSubscribe(final Observer<T> observer) { return null; };

	protected void onClose(final Source source) { }

}
