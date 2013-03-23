package com.hartveld.stream.reactive.operators;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.ObservableFactory;
import com.hartveld.stream.reactive.Observer;
import com.hartveld.stream.reactive.tests.AbstractSubjectObserverTestBase;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class ThrottleTest extends AbstractSubjectObserverTestBase {

	private static final Logger LOG = LoggerFactory.getLogger(ThrottleTest.class);

	@After
	public void tearDown() {
		LOG.info("scheduler: {}", scheduler);
	}

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		source.throttle(0, TimeUnit.MILLISECONDS).subscribe(target);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testThatThrottleSkipsSecondElement() {
		target = mock(Observer.class);

		final Observable<String> source = ObservableFactory.observableOf(hello, world);

		source.throttle(1, TimeUnit.SECONDS).subscribe(target);

		verify(target).onNext(hello);
		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

}
