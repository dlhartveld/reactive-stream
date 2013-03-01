package com.hartveld.stream.reactive.subjects;

import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.Observer;
import com.hartveld.stream.reactive.tests.AbstractSubjectObserverTestBase;
import org.junit.Before;

public class BasicSubjectTest extends AbstractSubjectObserverTestBase {

	private Subject<String> subject;

	@Before
	@Override
	public void setUp() {
		super.setUp();

		this.subject = new BasicSubject<>();
	}

	@Override
	protected void initializeFor(Observable<String> source, Observer<String> target) {
		subject.subscribe(target);
		source.subscribe(subject);
	}

}
