package com.hartveld.rx;

import java.util.function.Block;

public class ForwardingObserver<T> implements IObserver<T> {

	private final Block<T> onNext;
	private final Block<Throwable> onError;
	private final Runnable onCompleted;

	public ForwardingObserver(Block<T> onNext, Block<Throwable> onError, Runnable onCompleted) {
		this.onNext = onNext;
		this.onError = onError;
		this.onCompleted = onCompleted;
	}

	@Override
	public void onNext(T value) {
		onNext.accept(value);
	}

	@Override
	public void onError(Throwable cause) {
		onError.accept(cause);
	}

	@Override
	public void onCompleted() {
		onCompleted.run();
	}

}
