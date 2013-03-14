package com.hartveld.stream.reactive;

import java.util.function.Consumer;

public class TestObservable implements Observable<Object> {

	@Override
	public AutoCloseable subscribe(Consumer<Object> onNext, Consumer<Throwable> onError, Runnable onCompleted) {
		return null;
	}

}
