package com.hartveld.rx.tests;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.hartveld.rx.IObservable;
import com.hartveld.rx.IObserver;
import com.hartveld.rx.Observables;
import java.util.concurrent.ExecutorService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractOperatorTestBase {

	protected static final String hello = "Hello";
	protected static final String world = "world";

	protected static final RuntimeException expectedException = new RuntimeException("This is expected");

	protected boolean gotHello;
	protected boolean gotWorld;

	protected boolean gotError;
	protected boolean completed;

	protected ExecutorService syncExecSvc;

	protected abstract IObservable<String> getTestableObservableFrom(IObservable<String> o);

	@Mock
	private IObserver<String> target;

	@Before
	public void setUp() {
		gotHello = false;
		gotWorld = false;

		gotError = false;
		completed = false;

		syncExecSvc = new SynchronousExecutorService();
	}

	@After
	public void tearDown() {
		syncExecSvc.shutdown();
	}

	@Test
	public void testThatObservationsAfterErrorAreIgnored() {
		IObservable<String> source = (onNext, onError, onCompleted) -> {
			onNext.accept(hello);
			onError.accept(expectedException);
			onNext.accept(world);

			return () -> { };
		};

		getTestableObservableFrom(source).subscribe(target);

		verify(target).onNext(hello);
		verify(target).onError(expectedException);
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatObservationsAfterCompletedAreIgnored() {
		IObservable<String> source = (onNext, onError, onCompleted) -> {
			onNext.accept(hello);
			onCompleted.procedure();
			onNext.accept(world);

			return () -> { };
		};

		getTestableObservableFrom(source).subscribe(target);

		verify(target).onNext(hello);
		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

}
