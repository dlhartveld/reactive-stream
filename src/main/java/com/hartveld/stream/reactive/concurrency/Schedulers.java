package com.hartveld.stream.reactive.concurrency;

import java.time.Duration;
import java.time.Instant;

public class Schedulers {

	public static Scheduler<Instant, Duration> defaultScheduler() {
		return defaultScheduler;
	}

	public static Scheduler<Instant, Duration> eventQueueScheduler() {
		return eventQueueScheduler;
	}

	public static Scheduler<Instant, Duration> immediateScheduler() {
		return immediateScheduler;
	}

	private static final DefaultScheduler defaultScheduler = new DefaultScheduler();
	private static final EventQueueScheduler eventQueueScheduler = new EventQueueScheduler();
	private static final ImmediateScheduler immediateScheduler = new ImmediateScheduler();

	private Schedulers() { }

}
