package com.hartveld.rx;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;
import java.util.function.Function;

class MapOp<T, TResult> extends OperatorBase<T, TResult> {

	private final Function<? super T, ? extends TResult> mapper;

	public MapOp(final Observable<T> source, final Function<? super T, ? extends TResult> mapper) {
		super(source);

		checkNotNull(mapper, "mapper");

		this.mapper = mapper;
	}

	@Override
	protected void onNext(T element, Consumer<TResult> onNext) {
		onNext.accept(mapper.apply(element));
	}

}
