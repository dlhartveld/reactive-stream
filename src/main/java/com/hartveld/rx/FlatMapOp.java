package com.hartveld.rx;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;
import java.util.stream.FlatMapper;

class FlatMapOp<T, R> extends OperatorBase<T, R> {

	private final FlatMapper<? super T, R> mapper;

	public FlatMapOp(final Observable<T> source, final FlatMapper<? super T, R> mapper) {
		super(source);

		checkNotNull(mapper, "mapper");

		this.mapper = mapper;
	}

	@Override
	protected void onNext(T element, Consumer<R> onNext) {
		mapper.flattenInto(element, onNext);
	}

}
