package com.hartveld.rx;

public interface IObservable<T> {

	AutoCloseable subscribe(Procedure1<T> onNext, Procedure1<Throwable> onError, Procedure onCompleted);

}
