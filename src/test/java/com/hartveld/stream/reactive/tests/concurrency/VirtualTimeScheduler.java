package com.hartveld.stream.reactive.tests.concurrency;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.hartveld.stream.reactive.ForwardingAutoCloseable;
import com.hartveld.stream.reactive.Observable;
import com.hartveld.stream.reactive.concurrency.Scheduler;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualTimeScheduler implements Scheduler<Long, Long> {

	private static final Logger LOG = LoggerFactory.getLogger(VirtualTimeScheduler.class);

	private long time;

	private final Queue<Action> actions = new PriorityQueue<>();

	@Override
	public <T> AutoCloseable schedule(final Runnable action) {
		LOG.trace("Scheduling action: {}", action);

		checkNotNull(action, "action");

		final Action a = new Action(this.now() + 1, action);

		actions.offer(a);

		return a;
	}

	@Override
	public <T> AutoCloseable schedule(final Runnable action, final Long delay) {
		LOG.trace("Scheduling action: {} after: {}", action, delay);

		checkNotNull(action, "action");
		checkArgument(delay >= 0, "delay < 0");

		final Action a = new Action(this.now() + delay, action);

		actions.offer(a);

		return a;
	}

	@Override
	public Long now() {
		return time;
	}

	public <T> List<Notification<T>> run(final Observable<T> source) {
		LOG.trace("Running Observable: {}", source);

		checkNotNull(source, "source");

		final RecordingObserver<T> target = new RecordingObserver<>(this);

		final ForwardingAutoCloseable<?> fac = new ForwardingAutoCloseable<>();

		LOG.trace("Offering subscription @ 100 ...");
		actions.offer(new Action(
				100,
				() -> fac.set(source.subscribe(target))
		));

		LOG.trace("Offering close @ 1000 ...");
		actions.offer(new Action(
				1000,
				() -> {
					try {
						fac.close();
					} catch (Exception e) { }
				}
		));

		runQueue();

		return target.getRecordings();
	}

	private void runQueue() {
		LOG.trace("Running queue ...");

		while (!this.actions.isEmpty()) {
			final Action action = this.actions.poll();

			LOG.trace("Got action: {}", action);
			if (this.time < action.time) {
				LOG.trace("Setting time to action.time ...");
				this.time = action.time;
			}

			LOG.trace("Running action.runnable ...");
			action.runnable.run();
		}
	}

	private class Action implements AutoCloseable, Comparable<Action> {

		public final long time;
		public final Runnable runnable;

		public Action(final long time, final Runnable runnable) {
			checkNotNull(runnable, "runnable");

			this.time = time;
			this.runnable = runnable;
		}

		@Override
		public int compareTo(final Action that) {
			return Long.compare(this.time, that.time);
		}

		@Override
		public void close() {
			actions.remove(this);
		}

	}

}
