package com.hartveld.stream.reactive.tests.concurrency;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.Before;
import org.junit.Test;

public class DefaultVirtualTimeSchedulerTest {

	private DefaultVirtualTimeScheduler<String> scheduler;

	@Before
	public void setUp() {
		this.scheduler = new DefaultVirtualTimeScheduler<>();
	}

	@Test
	public void testThatFirstJobIsScheduledAtTimeUnitOne() {
		final AtomicLong t0 = new AtomicLong();

		this.scheduler.schedule(() -> { t0.set(scheduler.now()); });

		this.scheduler.run();

		assertThat(t0.get(), is(1l));
	}

	@Test
	public void testThatSecondJobIsScheduledOneTimeunitAfterFirstJob() {
		final AtomicLong t0 = new AtomicLong();
		final AtomicLong t1 = new AtomicLong();

		this.scheduler.schedule(() -> { t0.set(scheduler.now()); });
		this.scheduler.schedule(() -> { t1.set(scheduler.now()); });

		this.scheduler.run();

		assertThat(t1.get(), is(2l));
	}

}
