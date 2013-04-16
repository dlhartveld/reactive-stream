package com.hartveld.stream.reactive;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

class FlatMapOp<T, R> extends OperatorBase<T, R> {

	private final Function<? super T, ? extends Stream<? extends R>> mapper;

	FlatMapOp(final Observable<T> source, final Function<? super T, ? extends Stream<? extends R>> mapper) {
		super(source);

		checkNotNull(mapper, "mapper");

		this.mapper = mapper;
	}

	@Override
	protected void onNext(final T element, final Consumer<? super R> onNext) {
		this.mapper.apply(element).forEach(onNext);
	}

}
