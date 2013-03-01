package com.hartveld.stream.reactive;

public interface DoubleObserver {

	void onNext(double value);

	void onError(Throwable e);

	void onCompleted();

}
