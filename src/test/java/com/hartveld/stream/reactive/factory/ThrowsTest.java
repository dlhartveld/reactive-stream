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
public class ThrowsTest {

	private Observable<String> source;

	@Mock
	private Observer<String> target;

	private final Exception exception = new Exception();

	@Before
	public void setUp() {
		this.source = ObservableFactory.throwsObservable(exception);
	}

	@Test
	public void testThatExpectedExceptionIsSuppliedOnError() throws Exception {
		source.subscribe(target).close();

		verify(target).onError(exception);
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatMultipleSubscriptionsReturnsMultipleErrors() throws Exception {
		final int numRunTimes = 10;

		for (int i = 0; i < numRunTimes; i++) {
			source.subscribe(target).close();
		}

		verify(target, times(numRunTimes)).onError(exception);
		verifyNoMoreInteractions(target);
	}

}
