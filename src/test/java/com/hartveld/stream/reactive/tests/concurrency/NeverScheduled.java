package com.hartveld.stream.reactive.tests.concurrency;

import com.hartveld.stream.reactive.concurrency.Scheduler;
import static com.hartveld.stream.reactive.AutoCloseables.noop;

import java.time.Duration;
import java.time.Instant;

class NeverScheduled implements Scheduler<Instant, Duration> {

	@Override
	public <T> AutoCloseable schedule(final Runnable action) {
		return noop();
	}

	@Override
	public <T> AutoCloseable schedule(final Runnable action, final Duration delay) {
		return noop();
	}

	@Override
	public Instant now() {
		return Instant.now();
	}

}
