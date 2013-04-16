package com.hartveld.stream.reactive.subjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicSubject<T, Source> extends AbstractSubject<T, T, Source> implements Subject<T, T> {

	private static final Logger LOG = LoggerFactory.getLogger(BasicSubject.class);

	@Override
	public void onNext(final T value) {
		LOG.trace("onNext(): {}", value);

		if (isStopped()) {
			return;
		}

		broadcastNext(value);
	}

}
