package com.hartveld.stream.reactive.concurrency;

import java.time.Duration;
import java.time.Instant;
import org.apache.commons.lang.NotImplementedException;

class ImmediateScheduler implements Scheduler<Instant, Duration> {

	@Override
	public <T> AutoCloseable schedule(final Runnable action) {
		action.run();

		return () -> { };
	}

	@Override
	public <T> AutoCloseable schedule(final Runnable action, final Duration delay) {
		throw new NotImplementedException();
	}

	@Override
	public Instant now() {
		return Instant.now();
	}

}
