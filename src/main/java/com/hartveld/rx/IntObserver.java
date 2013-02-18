package com.hartveld.rx;

public interface IntObserver {

	void onNext(int value);

	void onError(Throwable e);

	void onCompleted();

}
