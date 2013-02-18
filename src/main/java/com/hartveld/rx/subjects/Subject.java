package com.hartveld.rx.subjects;

import com.hartveld.rx.Observable;
import com.hartveld.rx.Observer;

public interface Subject<T> extends Observable<T>, Observer<T> {

}
