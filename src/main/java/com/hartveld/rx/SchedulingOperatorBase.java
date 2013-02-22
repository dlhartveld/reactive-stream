package com.hartveld.rx;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Executor;

abstract class SchedulingOperatorBase<T, R> extends OperatorBase<T, R> {

	private final Executor executor;

	protected SchedulingOperatorBase(final Observable<T> source, final Executor executor) {
		super(source);

		checkNotNull(executor, "executor");

		this.executor = executor;
	};

	protected final void schedule(final Runnable runnable) {
		this.executor.execute(runnable);
	}

}
