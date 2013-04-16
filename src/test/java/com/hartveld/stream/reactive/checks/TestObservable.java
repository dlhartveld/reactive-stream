package com.hartveld.stream.reactive.checks;

import com.hartveld.stream.reactive.Observable;
import java.util.function.Consumer;

public class TestObservable implements Observable<Object> {

	@Override
	public AutoCloseable subscribe(Consumer<Object> onNext, Consumer<Exception> onError, Runnable onCompleted) {
		return null;
	}

}
