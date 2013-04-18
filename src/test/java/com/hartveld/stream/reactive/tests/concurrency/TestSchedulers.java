package com.hartveld.stream.reactive.tests.concurrency;

import com.hartveld.stream.reactive.concurrency.Scheduler;
import java.time.Duration;
import java.time.Instant;

public class TestSchedulers {

	public static Scheduler<Instant, Duration> neverScheduled() {
		return neverScheduled;
	};

	private static final NeverScheduled neverScheduled = new NeverScheduled();

	private TestSchedulers() { }

}
