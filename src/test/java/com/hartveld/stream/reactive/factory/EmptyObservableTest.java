package com.hartveld.stream.reactive.factory;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.ObservableFactory;
import com.hartveld.stream.reactive.Observer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EmptyObservableTest {

	private Observable<String> source;

	@Mock
	private Observer<String> target;

	@Before
	public void setUp() {
		this.source = ObservableFactory.emptyObservable();
	}

	@Test
	public void testThatOnlyOnCompletedIsCalled() {
		this.source.subscribe(target);

		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatOnCompletedIsCalledForEachSubscribe() throws Exception {
		final int numRunTimes = 10;

		for (int i = 0; i < numRunTimes; i++) {
			this.source.subscribe(target);
		}

		verify(target, times(numRunTimes)).onCompleted();
		verifyNoMoreInteractions(target);
	}

}
