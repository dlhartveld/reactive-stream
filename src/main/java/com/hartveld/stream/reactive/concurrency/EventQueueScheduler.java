package com.hartveld.stream.reactive.concurrency;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.EventQueue;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;
import org.apache.commons.lang.NotImplementedException;

class EventQueueScheduler implements Scheduler<Instant, Duration> {

	private final Executor executor = EventQueue::invokeLater;

	@Override
	public <T> AutoCloseable schedule(final Runnable action) {
		checkNotNull(action, "action");

		executor.execute(action);

		return () ->  { };
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
