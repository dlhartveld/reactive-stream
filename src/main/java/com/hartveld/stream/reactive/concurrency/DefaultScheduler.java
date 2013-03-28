package com.hartveld.stream.reactive.concurrency;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class DefaultScheduler implements Scheduler<Instant, Duration> {

	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

	@Override
	public <T> AutoCloseable schedule(final Runnable action) {
		checkNotNull(action);

		final Future<?> future = executor.submit(action);

		return () -> future.cancel(false);
	}

	@Override
	public <T> AutoCloseable schedule(final Runnable action, final Duration delay) {
		checkNotNull(action);

		final Future<?> future = executor.schedule(action, delay.toMillis(), TimeUnit.MILLISECONDS);

		return () -> future.cancel(false);
	}

	@Override
	public Instant now() {
		return Instant.now();
	}

}
