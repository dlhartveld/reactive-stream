package com.hartveld.rx;

public interface DoubleObserver {

	void onNext(double value);

	void onError(Throwable e);

	void onCompleted();

}
