package com.hartveld.rx.operators;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.hartveld.rx.Observable;
import com.hartveld.rx.ObservableFactory;
import com.hartveld.rx.Observer;
import com.hartveld.rx.tests.AbstractSubjectObserverTestBase;
import org.junit.Test;

public class FilterTest extends AbstractSubjectObserverTestBase {

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		source.filter(x -> true).subscribe(target);
	}

	@Test
	public void testThatAlwaysTrueFilterPassesEverything() {
		Observable<String> source = ObservableFactory.observableOf(hello, world);
		source.filter(x -> true).subscribe(target);

		verify(target).onNext(hello);
		verify(target).onNext(world);
		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatAlwaysFalseFilterPassesNothing() {
		Observable<String> source = ObservableFactory.observableOf(hello, world);
		source.filter(x -> false).subscribe(target);

		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatCorrectStringOfTwoIsPassedThrough() {
		Observable<String> source = ObservableFactory.observableOf(hello, world);
		source.filter(s -> s.equals(hello)).subscribe(target);

		verify(target).onNext(hello);
		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

}
