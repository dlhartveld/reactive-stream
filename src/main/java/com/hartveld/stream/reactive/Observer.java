package com.hartveld.stream.reactive;

public interface Observer<T> {

	void onNext(T value);

	void onError(Throwable e);

	void onCompleted();

}
