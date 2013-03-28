package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hartveld.stream.reactive.concurrency.Scheduler;

abstract class SchedulingOperatorBase<T, R, Abs, Rel> extends OperatorBase<T, R> {

	private final Scheduler<Abs, Rel> scheduler;

	protected SchedulingOperatorBase(final Observable<T> source, final Scheduler<Abs, Rel> scheduler) {
		super(source);

		checkNotNull(scheduler, "scheduler");

		this.scheduler = scheduler;
	}

	protected final void schedule(final Runnable runnable) {
		this.scheduler.schedule(runnable);
	}

}
