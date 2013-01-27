package com.hartveld.rx.subjects;

import com.hartveld.rx.IObservable;
import com.hartveld.rx.IObserver;
import com.hartveld.rx.tests.AbstractSubjectObserverTestBase;
import org.junit.Before;

public class BasicSubjectTest extends AbstractSubjectObserverTestBase {

	private ISubject<String> subject;

	@Before
	@Override
	public void setUp() {
		super.setUp();

		this.subject = new BasicSubject<>();
	}

	@Override
	protected void initializeFor(IObservable<String> source, IObserver<String> target) {
		subject.subscribe(target);
		source.subscribe(subject);
	}

}
