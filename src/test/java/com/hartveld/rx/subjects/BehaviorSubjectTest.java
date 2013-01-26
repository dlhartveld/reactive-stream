package com.hartveld.rx.subjects;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.hartveld.rx.IObserver;
import com.hartveld.rx.subjects.BehaviorSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BehaviorSubjectTest {

	private static final String INITIAL = "X";

	private BehaviorSubject<String> subject;

	@Mock
	private IObserver<String> target;

	@Before
	public void setUp() {
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
		subject.subscribe(target).close();
		subject.subscribe(target).close();

		verify(target, Mockito.times(2)).onNext(INITIAL);
	}

}
