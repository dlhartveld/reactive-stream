package com.hartveld.rx.subjects;

import com.hartveld.rx.IObservable;
import com.hartveld.rx.IObserver;

public interface ISubject<T> extends IObservable<T>, IObserver<T> {

}
