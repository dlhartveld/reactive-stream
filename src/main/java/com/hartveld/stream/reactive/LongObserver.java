package com.hartveld.stream.reactive;

public interface LongObserver {

	void onNext(long value);

	void onError(Exception e);

	void onCompleted();

}
