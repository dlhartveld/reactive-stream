package com.hartveld.stream.reactive;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class TestIntObservable implements IntObservable {

	public AutoCloseable subscribe(IntConsumer onNext, Consumer<Throwable> onError, Runnable onCompleted) {
		return null;
	}

}
