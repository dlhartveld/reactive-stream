package com.hartveld.stream.reactive;

import com.hartveld.stream.reactive.concurrency.Scheduler;
import java.util.function.Consumer;

class ObserveOnOp<T, A, R> extends SchedulingOperatorBase<T, T, A, R> {

	ObserveOnOp(final Observable<T> source, final Scheduler<A, R> scheduler) {
		super(source, scheduler);
	}

	@Override
	protected void onNext(final T element, final Consumer<T> onNext) {
		schedule(() -> onNext.accept(element));
	}

	@Override
	protected void onError(final Exception exception, final Consumer<Exception> onError) {
		schedule(() -> onError.accept(exception));
	}

	@Override
	protected void onCompleted(final Runnable onCompleted) {
		schedule(onCompleted);
	}

}
