package com.hartveld.rx.tests.operators;

import static java.util.function.Predicates.alwaysFalse;
import static java.util.function.Predicates.alwaysTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.hartveld.rx.IObservable;
import com.hartveld.rx.Observables;
import com.hartveld.rx.tests.AbstractOperatorTestBase;
import org.junit.Test;

public class FilterTest extends AbstractOperatorTestBase {

	@Override
	public IObservable<String> getTestableObservableFrom(IObservable<String> source) {
		return source.filter(alwaysTrue());
	}

	@Test
	public void testThatAlwaysTrueFilterPassesEverything() {
		IObservable<String> source = Observables.observableOf(hello, world);
		source.filter(alwaysTrue()).subscribe(target);
		
		verify(target).onNext(hello);
		verify(target).onNext(world);
		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatAlwaysFalseFilterPassesNothing() {
		IObservable<String> source = Observables.observableOf(hello, world);
		source.filter(alwaysFalse()).subscribe(target);

		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

	@Test
	public void testThatCorrectStringOfTwoIsPassedThrough() {
		IObservable<String> source = Observables.observableOf(hello, world);
		source.filter(s -> s.equals(hello)).subscribe(target);

		verify(target).onNext(hello);
		verify(target).onCompleted();
		verifyNoMoreInteractions(target);
	}

}
