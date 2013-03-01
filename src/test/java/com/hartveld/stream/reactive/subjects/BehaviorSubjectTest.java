package com.hartveld.stream.reactive.subjects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.Observer;
import com.hartveld.stream.reactive.tests.AbstractSubjectObserverTestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BehaviorSubjectTest extends AbstractSubjectObserverTestBase {

	private static final String INITIAL = "X";

	private BehaviorSubject<String, Object> subject;

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		this.subject.subscribe(target);
		verify(target).onNext(INITIAL);

		source.subscribe(this.subject);
	}

	@Before
	@Override
	public void setUp() {
		super.setUp();

		subject = new BehaviorSubject<>(INITIAL);
	}

	@Test
	public void testThatSingleSubscriptionSendsInitialNotification() {
		subject.subscribe(target);

		verify(target).onNext(INITIAL);
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatSecondSubscriptionAlsoReceivesNotification() throws Exception {
		@SuppressWarnings("unchecked")
		Observer<String> secondTarget = mock(Observer.class);

		subject.subscribe(target).close();
		subject.subscribe(secondTarget).close();

		verify(target).onNext(INITIAL);
		verify(secondTarget).onNext(INITIAL);
		verifyNoMoreInteractions(target, secondTarget);
	}

	@Test
	public void testThatSubscriptionObserversLatestElement() {
		subject.onNext(hello);
		subject.onNext(world);

		subject.subscribe(target);

		verify(target).onNext(world);
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatSubscriptionObserversOnCompletedAfterCompletion() {
		subject.onCompleted();

		subject.subscribe(target);

		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatSubscriptionObserversOnErrorAfterError() {
		subject.onError(expectedException);

		subject.subscribe(target);

		verify(target).onError(expectedException);
		verifyNoMoreInteractions(target);
	}

}
