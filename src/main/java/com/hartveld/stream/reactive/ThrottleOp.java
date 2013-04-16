package com.hartveld.stream.reactive;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

class ThrottleOp<T> extends OperatorBase<T, T> {

	private long previousTime;

	private final long delay;

	ThrottleOp(final Observable<T> source, final long delay, final TimeUnit timeUnit) {
		super(source);

		this.previousTime = 0;

		this.delay = timeUnit.toMillis(delay);
	}

	@Override
	protected void onNext(final T element, final Consumer<? super T> onNext) {
		LOG.trace("onNext: {}", element);

		final long currentTime = System.currentTimeMillis();
		final long difference = currentTime - previousTime;

		LOG.trace("Current time: {} - previous time: {} - difference: {} - configured delay: {}",
				currentTime, previousTime, difference, delay);

		if (difference >= delay) {
			LOG.trace("Forwarding element ...");
			onNext.accept(element);
			previousTime = currentTime;
		}
	}

}
