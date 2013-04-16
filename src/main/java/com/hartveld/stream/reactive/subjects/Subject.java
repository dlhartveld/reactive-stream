package com.hartveld.stream.reactive.subjects;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.Observer;

public interface Subject<T, R> extends Observable<R>, Observer<T> {

}
