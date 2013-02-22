package com.hartveld.rx;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

class ObserveOnOp<T> extends SchedulingOperatorBase<T, T> {

	public ObserveOnOp(final Observable<T> source, final Executor executor) {
		super(source, executor);
	}

	@Override
	protected void onNext(final T element, final Consumer<T> onNext) {
		schedule(() -> onNext.accept(element));
	}

	@Override
	protected void onError(final Throwable exception, final Consumer<Throwable> onError) {
		schedule(() -> onError.accept(exception));
	}

	@Override
	protected void onCompleted(final Runnable onCompleted) {
		schedule(onCompleted);
	}

}
