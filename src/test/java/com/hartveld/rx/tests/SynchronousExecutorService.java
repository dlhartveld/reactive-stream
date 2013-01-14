package com.hartveld.rx.tests;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class SynchronousExecutorService implements ExecutorService {

	private boolean shutdown = false;

	@Override
	public void shutdown() {
		shutdown = true;
	}

	@Override
	public List<Runnable> shutdownNow() {
		return emptyList();
	}

	@Override
	public boolean isShutdown() {
		return shutdown;
	}

	@Override
	public boolean isTerminated() {
		return shutdown;
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) {
		return true;
	}

	@Override
	public void execute(Runnable command) {
		if (shutdown) {
			throw new RejectedExecutionException("Synchronous executor service is already shut down.");
		}

		command.run();
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		if (shutdown) {
			throw new RejectedExecutionException("Synchronous executor service is already shut down.");
		}

		FutureTask<T> futureTask = new FutureTask<>(task);
		futureTask.run();
		return futureTask;
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		if (shutdown) {
			throw new RejectedExecutionException("Synchronous executor service is already shut down.");
		}

		FutureTask<T> futureTask = new FutureTask<T>(task, result);
		futureTask.run();
		return futureTask;
	}

	@Override
	public Future<?> submit(Runnable task) {
		if (shutdown) {
			throw new RejectedExecutionException("Synchronous executor service is already shut down.");
		}

		FutureTask<?> futureTask = new FutureTask<Object>(task, null);
		task.run();
		return futureTask;
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
		throw new RuntimeException("Method not implemented");
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
		throw new RuntimeException("Method not implemented");
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) {
		throw new RuntimeException("Method not implemented");
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
		throw new RuntimeException("Method not implemented");
	}

}
