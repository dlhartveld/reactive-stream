package com.hartveld.rx;

import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IObservable<T> {

	static final Logger LOG = LoggerFactory.getLogger(IObservable.class);

	AutoCloseable subscribe(Procedure1<T> onNext, Procedure1<Throwable> onError, Procedure onCompleted);

	default IObservable<T> observeOn(ExecutorService svc) {
		return (onNext, onError, onCompleted) -> {
			final AutoCloseable ac = IObservable.this.subscribe(
				e -> svc.execute(() -> onNext.procedure(e)),
				e -> svc.execute(() -> onError.procedure(e)),
				() -> svc.execute(() -> onCompleted.procedure())
			);
			return () -> ac.close();
		};
	}

}
