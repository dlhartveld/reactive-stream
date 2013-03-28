package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hartveld.stream.reactive.concurrency.Scheduler;
import java.util.function.Consumer;

class SubscribeOnOp<T, A, R> extends SchedulingOperatorBase<T, T, A, R> {

	SubscribeOnOp(final Observable<T> source, final Scheduler<A, R> scheduler) {
		super(source, scheduler);
	}

	@Override
	protected void onNext(T element, Consumer<T> onNext) {
		onNext.accept(element);
	}

	@Override
	public AutoCloseable subscribe(final Consumer<T> onNext, final Consumer<Exception> onError, final Runnable onCompleted) {
		checkNotNull(onNext, "onNext");
		checkNotNull(onError, "onError");
		checkNotNull(onCompleted, "onCompleted");

		schedule(() -> 	super.subscribe(onNext, onError, onCompleted));

		return () -> schedule(() -> unsubscribe());
	}

}
