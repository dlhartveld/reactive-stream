package com.hartveld.stream.reactive.concurrency;

import java.time.Duration;
import java.time.Instant;

public class Schedulers {

	public static final Scheduler<Instant, Duration> DEFAULT = new DefaultScheduler();

	public static final Scheduler<Instant, Duration> EDT = new SwingEDTScheduler();

	public static final Scheduler<Instant, Duration> IMMEDIATE = new ImmediateScheduler();

	private Schedulers() { }

}
