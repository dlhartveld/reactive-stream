package com.hartveld.stream.reactive.subjects;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hartveld.stream.reactive.Observer;
import com.hartveld.stream.reactive.concurrency.Scheduler;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskSubject<T, A, R> extends BasicSubject<T, Callable<T>> {

	private static final Logger LOG = LoggerFactory.getLogger(TaskSubject.class);

	private final Scheduler<A, R> scheduler;
	private final Callable<T> task;

	public TaskSubject(final Scheduler<A, R> scheduler, final Callable<T> task) {
		checkNotNull(scheduler, "scheduler");
		checkNotNull(task, "task");

		this.scheduler = scheduler;
		this.task = task;
	}

	@Override
	protected Callable<T> onSubscribe(final Observer<? super T> observer) {
		LOG.trace("Scheduling task on executor ...");

		scheduler.schedule(() -> {
			try {
				final T result = task.call();
				onNext(result);
				onCompleted();
			} catch (Exception ex) {
				LOG.warn("Caught exception: {}", ex.getMessage(), ex);
				onError(ex);
			}
		});

		return task;
	}

}
