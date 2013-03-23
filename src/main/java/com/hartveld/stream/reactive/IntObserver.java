package com.hartveld.stream.reactive;

public interface IntObserver {

	void onNext(int value);

	void onError(Exception e);

	void onCompleted();

}
