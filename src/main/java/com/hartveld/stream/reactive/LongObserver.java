package com.hartveld.stream.reactive;

public interface LongObserver {

	void onNext(long value);

	void onError(Throwable e);

	void onCompleted();

}
