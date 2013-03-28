package com.hartveld.stream.reactive.concurrency;

public interface Scheduler<Absolute, Relative> {

	<T> AutoCloseable schedule(Runnable action);

	<T> AutoCloseable schedule(Runnable action, Relative delay);

	Absolute now();

}
