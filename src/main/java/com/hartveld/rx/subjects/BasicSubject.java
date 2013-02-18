package com.hartveld.rx.subjects;

import com.hartveld.rx.ForwardingAutoCloseable;
import com.hartveld.rx.Observer;
import com.hartveld.rx.ObserverFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicSubject<T> implements Subject<T> {

	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	private static final Logger LOG = LoggerFactory.getLogger(BasicSubject.class);
	// TODO: Change to a concurrent list.
	private Map<AutoCloseable, Observer<T>> observers = new ConcurrentHashMap<>();
	private boolean stopped = false;

	@Override
	public AutoCloseable subscribe(Observer<T> observer) {
		LOG.trace("Subscribing new observer: {}", observer);

		ForwardingAutoCloseable fac = new ForwardingAutoCloseable();
		fac.set(() -> {
			if (observers.containsKey(fac)) {
				observers.remove(fac);
			}
		});

		observers.put(fac, observer);

		return fac;
	}

	@Override
	public final AutoCloseable subscribe(Consumer<T> onNext, Consumer<Throwable> onError, Runnable onCompleted) {
		LOG.trace("Subscribing new forwarding observer ...");
		return this.subscribe(ObserverFactory.createObserver(onNext, onError, onCompleted));
	}

	@Override
	public void onNext(T value) {
		LOG.trace("onNext(): {}", value);

		if (stopped) {
			return;
		}

		for (Observer<T> observer : observers.values()) {
			observer.onNext(value);
		}
	}

	@Override
	public void onError(Throwable cause) {
		LOG.trace("onError(): {}", cause.getMessage(), cause);

		if (stopped) {
			return;
		}

		stopped = true;

		for (Observer<T> observer : observers.values()) {
			observer.onError(cause);
		}
	}

	@Override
	public void onCompleted() {
		LOG.trace("onCompleted()");

		if (stopped) {
			return;
		}

		stopped = true;

		for (Observer<T> observer : observers.values()) {
			observer.onCompleted();
		}
	}

}
