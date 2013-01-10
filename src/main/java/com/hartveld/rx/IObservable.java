package com.hartveld.rx;

import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IObservable<T> {

	static final Logger LOG = LoggerFactory.getLogger(IObservable.class);

	AutoCloseable subscribe(Procedure1<T> onNext, Procedure1<Throwable> onError, Procedure onCompleted);

	default IObservable<T> observeOn(ExecutorService svc) {
		return new IObservable<T>() {
			@Override public AutoCloseable subscribe(Procedure1<T> onNext, Procedure1<Throwable> onError, Procedure onCompleted) {
				Procedure1<T> scheduledOnNext = e -> svc.execute(() -> onNext.procedure(e));
				Procedure1<Throwable> scheduledOnError = e -> svc.execute(() -> onError.procedure(e));
				Procedure scheduledOnCompleted = () -> svc.execute(() -> onCompleted.procedure());

				final AutoCloseable ac = IObservable.this.subscribe(scheduledOnNext, scheduledOnError, scheduledOnCompleted);
				return () -> ac.close();
			}
		};
	}

}
