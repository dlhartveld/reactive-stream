package com.hartveld.rx;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LongObservableTest {

	@Mock
	private LongObserver target;

	@Test
	public void testBasicFunctionality() throws Exception {
		LongObservable source = ObservableFactory.observableOfLongs(1, 2, 3);

		source.subscribe(target).close();

		verify(target).onNext(1);
		verify(target).onNext(2);
		verify(target).onNext(3);
		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

}
