package com.hartveld.stream.reactive.tests.concurrency;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class Notification<T> {

	public static <T> Notification<T> onNext(final long time, final T value) {
		return new OnNext<>(time, value);
	}

	public static <T> Notification<T> onCompleted(final long time) {
		return new OnCompleted<>(time);
	}

	public static <T> Notification<T> onError(final long time, final Exception exception) {
		return new OnError<>(time, exception);
	}

	public final long time;

	private Notification(final long time) {
		this.time = time;
	}

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();

	private static class OnNext<T> extends Notification<T> {

		public final T value;

		public OnNext(final long time, final T value) {
			super(time);

			checkNotNull(value, "value");

			this.value = value;
		}

		@Override
		public int hashCode() {
			final HashCodeBuilder builder = new HashCodeBuilder();

			builder.append(this.time);
			builder.append(this.value);

			return builder.toHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}

			@SuppressWarnings("unchecked")
			final OnNext<T> that = (OnNext<T>) obj;

			final EqualsBuilder builder = new EqualsBuilder();

			builder.append(this.time, that.time);
			builder.append(this.value, that.value);

			return builder.isEquals();
		}

		@Override
		public String toString() {
			final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);

			builder.append("time", this.time);
			builder.append("value", this.value);

			return builder.toString();
		}

	}

	private static class OnCompleted<T> extends Notification<T> {

		public OnCompleted(final long time) {
			super(time);
		}

		@Override
		public int hashCode() {
			final HashCodeBuilder builder = new HashCodeBuilder();

			builder.append(this.time);

			return builder.toHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}

			@SuppressWarnings("unchecked")
			final OnCompleted<T> that = (OnCompleted<T>) obj;

			final EqualsBuilder builder = new EqualsBuilder();

			builder.append(this.time, that.time);

			return builder.build();
		}

		@Override
		public String toString() {
			final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);

			builder.append("time", this.time);

			return builder.toString();
		}

	}

	private static class OnError<T> extends Notification<T> {

		public final Exception exception;

		public OnError(final long time, final Exception exception) {
			super(time);

			checkNotNull(exception, "exception");

			this.exception = exception;
		}

		@Override
		public int hashCode() {
			final HashCodeBuilder builder = new HashCodeBuilder();

			builder.append(this.time);
			builder.append(this.exception);

			return builder.toHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}

			@SuppressWarnings("unchecked")
			final OnError<T> that = (OnError<T>) obj;

			final EqualsBuilder builder = new EqualsBuilder();

			builder.append(this.time, that.time);
			builder.append(this.exception, that.exception);

			return builder.build();
		}

		@Override
		public String toString() {
			final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);

			builder.append("time", this.time);
			builder.append("exception", this.exception.getClass().getSimpleName());

			return builder.toString();
		}

	}

}
