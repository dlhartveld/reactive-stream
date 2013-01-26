package com.hartveld.rx.subjects;

import java.util.function.Block;

public class BehaviorSubject<T> extends BasicSubject<T> {

	private T current;
	private Throwable error;
	private boolean completed;

	public BehaviorSubject(T value) {
		current = value;
		error = null;
		completed = false;
	}

	@Override
	public void onNext(T value) {
		if (completed) {
			return;
		}

		current = value;
		error = null;

		super.onNext(value);
	}

	@Override
	public void onError(Throwable e) {
		if (completed) {
			return;
		}

		current = null;
		error = e;

		super.onError(e);
	}

	@Override
	public void onCompleted() {
		if (completed) {
			return;
		}

		current = null;
		error = null;

		super.onCompleted();
	}

	@Override
	public AutoCloseable subscribe(Block<T> onNext, Block<Throwable> onError, Runnable onCompleted) {
		final AutoCloseable ac = super.subscribe(onNext, onError, onCompleted);

		if (current != null) {
			onNext.accept(current);
		} else if (error != null) {
			onError.accept(error);
		} else if (completed) {
			onCompleted.run();
		} else {
			throw new IllegalStateException("Behavior subject is in undefined state");
		}

		return ac;
	}

}