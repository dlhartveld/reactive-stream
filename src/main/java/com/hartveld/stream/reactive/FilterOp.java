package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

class FilterOp<T> extends OperatorBase<T, T> {

	private final Predicate<? super T> predicate;

	public FilterOp(final Observable<T> source, final Predicate<? super T> predicate) {
		super(source);

		checkNotNull(predicate, "predicate");

		this.predicate = predicate;
	};

	@Override
	protected void onNext(T element, Consumer<T> onNext) {
		if (predicate.test(element)) {
			onNext.accept(element);
		}
	}

}
