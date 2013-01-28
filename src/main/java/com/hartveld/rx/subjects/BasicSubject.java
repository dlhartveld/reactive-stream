package com.hartveld.rx.subjects;

import com.hartveld.rx.ForwardingAutoCloseable;
import com.hartveld.rx.ForwardingObserver;
import com.hartveld.rx.IObserver;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicSubject<T> implements ISubject<T> {

	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	private static final Logger LOG = LoggerFactory.getLogger(BasicSubject.class);

	// TODO: Change to a concurrent list.
	private Map<AutoCloseable, IObserver<T>> observers = new ConcurrentHashMap<>();

	private boolean stopped = false;

	@Override
	public AutoCloseable subscribe(IObserver<T> observer) {
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
	public final AutoCloseable subscribe(Block<T> onNext, Block<Throwable> onError, Runnable onCompleted) {
		LOG.trace("Subscribing new forwarding observer ...");
		return this.subscribe(new ForwardingObserver<>(onNext, onError, onCompleted));
	}

	@Override
	public void onNext(T value) {
		LOG.trace("onNext(): {}", value);

		if (stopped) {
			return;
		}

		for (IObserver<T> observer : observers.values()) {
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

		for (IObserver<T> observer : observers.values()) {
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

		for (IObserver<T> observer : observers.values()) {
			observer.onCompleted();
		}
	}

}
