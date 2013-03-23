package com.hartveld.stream.reactive;

import java.util.function.Consumer;

public class ForwardingObserver<T> implements Observer<T> {

	private final Consumer<T> onNext;
	private final Consumer<Exception> onError;
	private final Runnable onCompleted;

	public ForwardingObserver(Consumer<T> onNext, Consumer<Exception> onError, Runnable onCompleted) {
		this.onNext = onNext;
		this.onError = onError;
		this.onCompleted = onCompleted;
	}

	@Override
	public void onNext(T value) {
		onNext.accept(value);
	}

	@Override
	public void onError(Exception cause) {
		onError.accept(cause);
	}

	@Override
	public void onCompleted() {
		onCompleted.run();
	}

}
