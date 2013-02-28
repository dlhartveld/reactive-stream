package com.hartveld.rx.subjects;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hartveld.rx.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskSubject<T> extends BasicSubject<T, Callable<T>> implements Subject<T> {

	private static final Logger LOG = LoggerFactory.getLogger(TaskSubject.class);

	private final Executor executor;
	private final Callable<T> task;

	public TaskSubject(final Executor executor, final Callable<T> task) {
		checkNotNull(executor, "executor");
		checkNotNull(task, "task");

		this.executor = executor;
		this.task = task;
	}

	@Override
	protected Callable<T> onSubscribe(final Observer<T> observer) {
		LOG.trace("Scheduling task on executor ...");

		executor.execute(() -> {
			try {
				final T result = task.call();
				onNext(result);
				onCompleted();
			} catch (Throwable t) {
				onError(t);
			}
		});

		return task;
	}

}
