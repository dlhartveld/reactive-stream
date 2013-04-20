package com.hartveld.stream.reactive.tests.concurrency;

import com.hartveld.stream.reactive.Observable;
import org.apache.commons.lang.NotImplementedException;

public final class TestObservableFactory {

	public static <T> Observable<T> observableOf(final VirtualTimeScheduler<T> scheduler, final Notification<T> ... notifications) {
		throw new NotImplementedException();
	}

	private TestObservableFactory() { }

}
