package com.hartveld.stream.reactive;

import java.util.function.Consumer;

class IdOp<T> extends OperatorBase<T, T> {

	IdOp(final Observable<T> source) {
		super(source);
	}

	@Override
	protected void onNext(T element, Consumer<? super T> onNext) {
		onNext.accept(element);
	}

}
