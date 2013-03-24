package com.hartveld.stream.reactive.operators;

import static com.hartveld.stream.reactive.ObservableFactory.emptyObservable;
import static com.hartveld.stream.reactive.ObservableFactory.observableOf;
import static com.hartveld.stream.reactive.ObservableFactory.throwsObservable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.Observer;
import com.hartveld.stream.reactive.tests.AbstractSubjectObserverTestBase;
import org.junit.Test;

public class MergeTest extends AbstractSubjectObserverTestBase {

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		source.merge(emptyObservable()).subscribe(target);
	}

	@Test
	public void testBasicMergeFunctionality() {
		final String byeBye = "bye bye";
		final Observable<String> o1 = observableOf(hello, world);
		final Observable<String> o2 = observableOf(byeBye);

		final Observable<String> merged = o1.merge(o2);

		merged.subscribe(target);

		verify(target).onNext(hello);
		verify(target).onNext(world);
		verify(target).onNext(byeBye);
		verify(target).onCompleted();

		verifyZeroInteractions(target);
	}

	@Test
	public void testThatMergeOfEmptyAndEmptyOnlyNotifiesCompletedOnce() {
		Observable<String> o1 = emptyObservable();
		Observable<String> o2 = emptyObservable();

		o1.merge(o2).subscribe(target);

		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatMergeOfEmptyAndThrowsOnlyNotifiesError() {
		Observable<String> o1 = emptyObservable();
		Observable<String> o2 = throwsObservable(expectedException);

		o1.merge(o2).subscribe(target);

		verify(target).onError(expectedException);
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatMergeOfThrowsAndEmptyOnlyNotifiesError() {
		Observable<String> o1 = throwsObservable(expectedException);
		Observable<String> o2 = emptyObservable();

		o1.merge(o2).subscribe(target);

		verify(target).onError(expectedException);
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatMergeOfThrowsAndThrowsOnlyNotifiesSingleError() {
		Observable<String> o1 = throwsObservable(expectedException);
		Observable<String> o2 = throwsObservable(expectedException);

		o1.merge(o2).subscribe(target);

		verify(target).onError(expectedException);
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatMergeOfListAndThrowsReturnsListThenError() {
		Observable<String> o1 = observableOf(hello, world);
		Observable<String> o2 = throwsObservable(expectedException);

		o1.merge(o2).subscribe(target);

		verify(target).onNext(hello);
		verify(target).onNext(world);
		verify(target).onError(expectedException);
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatMergeOfThrowsAndListOnlyNotifiersSingleError() {
		Observable<String> o1 = throwsObservable(expectedException);
		Observable<String> o2 = observableOf(hello, world);

		o1.merge(o2).subscribe(target);

		verify(target).onError(expectedException);
		verifyNoMoreInteractions(target);
	}

}
