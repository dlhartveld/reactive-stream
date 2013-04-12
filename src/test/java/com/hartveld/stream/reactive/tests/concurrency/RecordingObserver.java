package com.hartveld.stream.reactive.tests.concurrency;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hartveld.stream.reactive.Observer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class RecordingObserver<T> implements Observer<T> {

	private final VirtualTimeScheduler<T> scheduler;

	private final List<Notification<T>> results;

	public RecordingObserver(final VirtualTimeScheduler<T> scheduler) {
		checkNotNull(scheduler, "scheduler");

		this.scheduler = scheduler;
		this.results = new ArrayList<>();
	}

	public List<Notification<T>> getRecordings() {
		return Collections.unmodifiableList(results);
	}

	@Override
	public void onNext(T value) {
		results.add(Notification.onNext(scheduler.now(), value));
	}

	@Override
	public void onError(Exception e) {
		results.add(Notification.onError(scheduler.now(), e));
	}

	@Override
	public void onCompleted() {
		results.add(Notification.onCompleted(scheduler.now()));
	}

}
