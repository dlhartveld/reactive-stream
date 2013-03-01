package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

class SubscribeOnOp<T> extends SchedulingOperatorBase<T, T> {

	public SubscribeOnOp(final Observable<T> source, final Executor executor) {
		super(source, executor);
	}

	@Override
	protected void onNext(T element, Consumer<T> onNext) {
		onNext.accept(element);
	}

	@Override
	public AutoCloseable subscribe(final Consumer<T> onNext, final Consumer<Throwable> onError, final Runnable onCompleted) {
		checkNotNull(onNext, "onNext");
		checkNotNull(onError, "onError");
		checkNotNull(onCompleted, "onCompleted");

		schedule(() -> 	super.subscribe(onNext, onError, onCompleted));

		return () -> schedule(() -> unsubscribe());
	}

}
