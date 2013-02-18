package com.hartveld.rx.subjects;

import com.hartveld.rx.Observer;
import java.util.function.Consumer;

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
	public AutoCloseable subscribe(Observer<T> observer) {
		final AutoCloseable ac = super.subscribe(observer);

		onSubscription(observer::onNext, observer::onError, observer::onCompleted);

		return ac;
	}

	private void onSubscription(Consumer<T> onNext, Consumer<Throwable> onError, Runnable onCompleted) throws IllegalStateException {
		if (current != null) {
			onNext.accept(current);
		} else if (error != null) {
			onError.accept(error);
		} else if (completed) {
			onCompleted.run();
		} else {
			throw new IllegalStateException("Behavior subject is in undefined state");
		}
	}

}
