package com.hartveld.rx;

public interface IObserver<T> {

	void onNext(T value);

	void onError(Throwable e);

	void onCompleted();

}
