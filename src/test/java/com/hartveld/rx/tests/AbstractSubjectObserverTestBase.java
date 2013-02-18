package com.hartveld.rx.tests;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.hartveld.rx.Observable;
import com.hartveld.rx.Observer;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractSubjectObserverTestBase {

	protected static final String hello = "Hello";
	protected static final String world = "world";

	protected static final RuntimeException expectedException = new RuntimeException("This is expected");

	protected boolean gotHello;
	protected boolean gotWorld;
	protected boolean gotError;
	protected boolean completed;

	protected ExecutorService syncExecSvc;

	@Mock
	protected Observer<String> target;

	protected abstract void initializeFor(Observable<String> source, Observer<String> target);

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
	public void testThatCompletedAfterErrorIsIgnored() {
		Observable<String> source = (Consumer<String> onNext, Consumer<Throwable> onError, Runnable onCompleted) -> {
			onNext.accept(hello);
			onError.accept(expectedException);
			onCompleted.run();
			return () -> { };
		};

		initializeFor(source, target);

		verify(target).onNext(hello);
		verify(target).onError(expectedException);
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatErrorAfterCompletedIsIgnored() {
		Observable<String> source = (Consumer<String> onNext, Consumer<Throwable> onError, Runnable onCompleted) -> {
			onNext.accept(hello);
			onCompleted.run();
			onError.accept(expectedException);
			return () -> { };
		};

		initializeFor(source, target);

		verify(target).onNext(hello);
		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatObservationsAfterCompletedAreIgnored() {
		Observable<String> source = (Consumer<String> onNext, Consumer<Throwable> onError, Runnable onCompleted) -> {
			onNext.accept(hello);
			onCompleted.run();
			onNext.accept(world);
			return () -> { };
		};

		initializeFor(source, target);

		verify(target).onNext(hello);
		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatObservationsAfterErrorAreIgnored() {
		Observable<String> source = (Consumer<String> onNext, Consumer<Throwable> onError, Runnable onCompleted) -> {
			onNext.accept(hello);
			onError.accept(expectedException);
			onNext.accept(world);
			return () -> { };
		};

		initializeFor(source, target);

		verify(target).onNext(hello);
		verify(target).onError(expectedException);
		verifyNoMoreInteractions(target);
	}

}
