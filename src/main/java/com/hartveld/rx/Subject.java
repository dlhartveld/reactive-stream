package com.hartveld.rx;

import java.util.LinkedList;
import java.util.List;

public class Subject<T> implements IObservable<T>, IObserver<T> {

	private final List<AutoCloseableObserver<T>> observers = new LinkedList<>();

	@Override
	public AutoCloseable subscribe(Procedure1<T> onNext, Procedure1<Throwable> onError, Procedure onCompleted) {
		AutoCloseableObserver<T> observer = new AutoCloseableObserver<T>(onNext, onError, onCompleted);

		synchronized (observers) {
			observers.add(observer);
		}

		return observer;
	}
	
	@Override
	public void onNext(T value) {
		synchronized (observers) {
			for (IObserver<T> observer : observers) {
				observer.onNext(value);
			}
		}
	}
	
	@Override
	public void onError(Throwable cause) {
		synchronized (observers) {
			for (IObserver<T> observer : observers) {
				observer.onError(cause);
			}
		}
	}
	
	@Override
	public void onCompleted() {
		synchronized(observers) {
			for (IObserver<T> observer : observers) {
				observer.onCompleted();
			}
		}
	}

	private class AutoCloseableObserver<T> implements IObserver<T>, AutoCloseable {

		private final Procedure1<T> onNext;
		private final Procedure1<Throwable> onError;
		private final Procedure onCompleted;

		public AutoCloseableObserver(Procedure1<T> onNext, Procedure1<Throwable> onError, Procedure onCompleted) {
			this.onNext = onNext;
			this.onCompleted = onCompleted;
			this.onError = onError;
		}

		@Override
		public void onNext(T value) {
			onNext.procedure(value);
		}

		@Override
		public void onError(Throwable cause) {
			onError.procedure(cause);
		}

		@Override
		public void onCompleted() {
			onCompleted.procedure();
		}

		@Override public void close() {
			synchronized(observers) {
				observers.remove(this);
			}
		}
	}

}
