package com.hartveld.rx;

public interface LongObserver {

	void onNext(long value);

	void onError(Throwable e);

	void onCompleted();

}
