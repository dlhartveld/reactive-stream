package com.hartveld.stream.reactive.tests.concurrency;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hartveld.stream.reactive.ForwardingAutoCloseable;
import com.hartveld.stream.reactive.Observable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultVirtualTimeScheduler<T> implements VirtualTimeScheduler<T> {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultVirtualTimeScheduler.class);

	private long time = 0;
	private long lastScheduleTime = 0;

	private final Queue<Action> actions = new PriorityQueue<>();

	private final ForwardingAutoCloseable<Observable<T>> subscription = new ForwardingAutoCloseable<>();

	private final RecordingObserver<T> target = new RecordingObserver<>(this);

	@Override
	public AutoCloseable schedule(final Runnable action) {
		LOG.trace("Scheduling action: {}", action);

		checkNotNull(action, "action");

		if (this.lastScheduleTime < this.now()) {
			this.lastScheduleTime = this.now();
		}

		this.lastScheduleTime++;

		final Action a = new Action(this.lastScheduleTime, action);

		LOG.trace("Scheduling @ {}", a.time);
		actions.offer(a);

		return a;
	}

	@Override
	public AutoCloseable schedule(final Runnable action, final Long delay) {
		LOG.trace("Scheduling action: {} after: {}", action, delay);

		checkNotNull(action, "action");
		checkArgument(delay >= 0, "delay < 0");

		if (this.lastScheduleTime < this.now()) {
			this.lastScheduleTime = this.now();
		}

		final Action a = new Action(this.now() + delay, action);

		LOG.trace("Scheduling @ {}", a.time);
		actions.offer(a);

		return a;
	}

	@Override
	public Long now() {
		return time;
	}

	@Override
	public List<Notification<T>> run(final Observable<T> source) {
		LOG.trace("Running Observable: {}", source);

		checkNotNull(source, "source");

		this.subscribe(source, 100);
		this.closeSubscription(1000);

		return run();
	}

	@Override
	public void subscribe(final Observable<T> source, final long time) {
		LOG.trace("Offering subscription @ {} ...", time);

		checkNotNull(source, "source");

		actions.offer(new Action(
				time,
				() -> subscription.set(source.subscribe(target))
		));
	}

	@Override
	public void closeSubscription(final long time) {
		LOG.trace("Offering close @ {} ...", time);

		actions.offer(new Action(
				time,
				() -> {
					checkState(subscription != null, "Not subscribed");

					closeSubscriptionQuietly();
				}
		));
	}

	private void closeSubscriptionQuietly() {
		if (subscription != null) {
			try	{
				subscription.close();
			} catch (Exception e) { }
		}
	}

	@Override
	public List<Notification<T>> run() {
		LOG.trace("Running scheduler ...");

		runQueue();

		return target.getRecordings();
	}

	private void runQueue() {
		LOG.trace("Running queue ...");

		while (!this.actions.isEmpty()) {
			final Action action = this.actions.poll();

			LOG.trace("Got action: {}", action);
			if (this.time < action.time) {
				LOG.trace("Setting time to action.time: {} ...", action.time);
				this.time = action.time;
			} else if (this.time > action.time) {
				LOG.trace("Warning: next scheduled action is in the past: current time: {} - action time: {} - {}",
						this.time, action.time, action);
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

		@Override
		public String toString() {
			final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
			builder.append("time", this.time);
			builder.append("runnable", this.runnable);
			return builder.toString();
		}

	}

}
