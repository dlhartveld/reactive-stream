package com.hartveld.rx.subjects;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.hartveld.rx.Observable;
import com.hartveld.rx.Observer;
import com.hartveld.rx.concurrency.Schedulers;
import java.util.concurrent.Callable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TaskSubjectTest extends BasicSubjectTest {

	private static final String VALUE = "Hello, world!";

	private TaskSubject<String> subject;

	@Mock
	private Callable<String> task;

	@Before
	@Override
	public void setUp() {
		super.setUp();

		this.subject = new TaskSubject<>(scheduler, task);
	}

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		subject.subscribe(target);
		source.subscribe(subject);
	}

	@Test
	public void testThatExecutorMustExecuteOnSubscription() {
		this.subject.subscribe(target);

		verify(scheduler).execute(any(Runnable.class));
	}

	@Test
	public void testThatTaskResultIsForwardedToTarget() throws Exception {
		when(task.call()).thenReturn(VALUE);

		this.subject = new TaskSubject<>(Schedulers.IMMEDIATE, task);
		this.subject.subscribe(target);

		verify(target).onNext(VALUE);
		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

}
