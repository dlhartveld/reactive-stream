package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;
import java.util.function.Function;

class MapOp<T, R> extends OperatorBase<T, R> {

	private final Function<? super T, ? extends R> mapper;

	MapOp(final Observable<T> source, final Function<? super T, ? extends R> mapper) {
		super(source);

		checkNotNull(mapper, "mapper");

		this.mapper = mapper;
	}

	@Override
	protected void onNext(T element, Consumer<? super R> onNext) {
		onNext.accept(mapper.apply(element));
	}

}
