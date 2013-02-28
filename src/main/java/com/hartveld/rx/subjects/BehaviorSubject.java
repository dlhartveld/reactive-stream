package com.hartveld.rx.subjects;

import com.hartveld.rx.Observer;

public class BehaviorSubject<T, Source> extends BasicSubject<T, Source> {

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

		super.onNext(value);
	}

	@Override
	public void onError(Throwable e) {
		if (completed) {
			return;
		}

		current = null;
		error = e;
		completed = true;

		super.onError(e);
	}

	@Override
	public void onCompleted() {
		if (completed) {
			return;
		}

		current = null;
		error = null;
		completed = true;

		super.onCompleted();
	}

	@Override
	protected Source onSubscribe(final Observer<T> observer) {
		if (current != null) {
			observer.onNext(current);
		} else if (error != null) {
			observer.onError(error);
		} else if (completed) {
			observer.onCompleted();
		} else {
			throw new IllegalStateException("Behavior subject is in undefined state");
		}

		return super.onSubscribe(observer);
	}

}
