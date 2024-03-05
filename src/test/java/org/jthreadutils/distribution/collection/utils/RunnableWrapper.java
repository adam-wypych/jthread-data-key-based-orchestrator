package org.jthreadutils.distribution.collection.utils;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import com.google.common.collect.Multimap;

public class RunnableWrapper implements Runnable {
	private final Runnable wrappedRunnable;
	private final Multimap<Runnable, Throwable> errorsPerRunnable;
	private final Optional<CountDownLatch> await;
	private final Optional<CountDownLatch> notifyStarted;
	private final Optional<CountDownLatch> notifyDone;

	private RunnableWrapper(final Runnable wrappedRunnable, final Multimap<Runnable, Throwable> errorsPerRunnable,
			final Optional<CountDownLatch> await, final Optional<CountDownLatch> notifyStarted,
			final Optional<CountDownLatch> notifyDone) {
		this.wrappedRunnable = wrappedRunnable;
		this.errorsPerRunnable = errorsPerRunnable;
		this.await = await;
		this.notifyStarted = notifyStarted;
		this.notifyDone = notifyDone;
	}

	@Override
	public void run() {
		try {
			if (notifyStarted.isPresent()) {
				notifyStarted.get().countDown();
			}
			
			if (await.isPresent()) {
				await.get().await();
			}
			
			this.wrappedRunnable.run();
		} catch (Throwable th) {
			errorsPerRunnable.put(wrappedRunnable, th);
		} finally {
			if (notifyDone.isPresent()) {
				notifyDone.get().countDown();
			}
		}
	}

	public static StepWrapperOfUserRunnable builder() {
		return (StepWrapperOfUserRunnable) new RunnableWrapperBuilder();
	}
	
	public interface StepWrapperOfUserRunnable {
		StepErrorReporter wrappedRunnable(final Runnable runnable);
	}

	public interface StepErrorReporter {
		StepOptional errorReport(final Multimap<Runnable, Throwable> errorsPerRunnable);
	}

	public interface StepOptional {
		StepOptional awaitLatch(final CountDownLatch await);

		StepOptional notifyStartedLatch(final CountDownLatch notifyStarted);

		StepOptional notifyDoneLatch(final CountDownLatch notifyDone);

		RunnableWrapper build();
	}
	
	private static class RunnableWrapperBuilder implements StepWrapperOfUserRunnable, StepErrorReporter, StepOptional {
		private Runnable wrappedRunnable;
		private Multimap<Runnable, Throwable> errorsPerRunnable;
		private CountDownLatch await;
		private CountDownLatch notifyStarted;
		private CountDownLatch notifyDone;

		private RunnableWrapperBuilder() {
		}

		@Override
		public StepErrorReporter wrappedRunnable(final Runnable runnable) {
			this.wrappedRunnable = runnable;
			return (StepErrorReporter) this;
		}

		@Override
		public StepOptional errorReport(final Multimap<Runnable, Throwable> errorsPerRunnable) {
			this.errorsPerRunnable = errorsPerRunnable;
			return (StepOptional) this;
		}

		@Override
		public StepOptional awaitLatch(final CountDownLatch await) {
			this.await = await;
			return (StepOptional) this;
		}

		@Override
		public StepOptional notifyStartedLatch(final CountDownLatch notifyStarted) {
			this.notifyStarted = notifyStarted;
			return (StepOptional) this;
		}

		@Override
		public StepOptional notifyDoneLatch(final CountDownLatch notifyDone) {
			this.notifyDone = notifyDone;
			return (StepOptional) this;
		}

		@Override
		public RunnableWrapper build() {
			return new RunnableWrapper(wrappedRunnable, errorsPerRunnable, Optional.ofNullable(await),
					Optional.ofNullable(notifyStarted), Optional.ofNullable(notifyDone));
		}
	}	
}