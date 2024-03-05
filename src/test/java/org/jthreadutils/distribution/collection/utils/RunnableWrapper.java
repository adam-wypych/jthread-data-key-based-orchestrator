package org.jthreadutils.distribution.collection.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.google.common.collect.Multimap;

/**
 * This wrapper is being used within {@link MultithreadCoordinationTestHarness}
 * in order to synchronized threads each other in terms mostly of starting on
 * right time.
 * 
 * All synchronization is being done around 3 subset of {@link CountDownLatch} -
 * below is some pseudo-code <br/>
 * <code>
 * 	notifyOthersAboutTaskStarted.countDown(); <br/>
 *  waitForConditionMeetsForStartWrappedRunnable.await(); <br/>
 *  wrappedRunnable.run(); <br/>
 *  notifyOthersAboutTaskFinished.countDown();</br>
 * </code>
 * 
 * In order to build {@link RunnableWrapper} please use
 * {@link RunnableWrapperBuilder} to which access can be get via
 * {@link #builder()} method.
 * 
 * @author adam-wypych
 * @since 1.0.0
 * @version %I%, %G%
 */
public class RunnableWrapper implements Runnable {
	private final Runnable wrappedRunnable;
	private final Multimap<Runnable, Throwable> errorsPerRunnable;
	private final List<CountDownLatch> awaiters = new ArrayList<>();
	private final List<CountDownLatch> notifiersAboutStart = new ArrayList<>();
	private final List<CountDownLatch> notifiersAboutDone = new ArrayList<>();

	private RunnableWrapper(final Runnable wrappedRunnable, final Multimap<Runnable, Throwable> errorsPerRunnable,
			final List<CountDownLatch> awaiters, final List<CountDownLatch> notifiersAboutStart,
			final List<CountDownLatch> notifiersAboutDone) {
		this.wrappedRunnable = wrappedRunnable;
		this.errorsPerRunnable = errorsPerRunnable;
		this.awaiters.addAll(awaiters);
		this.notifiersAboutStart.addAll(notifiersAboutStart);
		this.notifiersAboutDone.addAll(notifiersAboutDone);
	}

	@Override
	public void run() {
		try {
			for (CountDownLatch startNotifier : notifiersAboutStart) {
				startNotifier.countDown();
			}

			for (CountDownLatch await : awaiters) {
				await.await();
			}

			this.wrappedRunnable.run();
		} catch (Throwable th) {
			errorsPerRunnable.put(wrappedRunnable, th);
		} finally {
			for (CountDownLatch doneNotifier : notifiersAboutDone) {
				doneNotifier.countDown();
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

		StepOptional awaitLatch(final List<CountDownLatch> awaiters);

		StepOptional notifyStartedLatch(final CountDownLatch notifyStarted);

		StepOptional notifyStartedLatch(final List<CountDownLatch> notifiersAboutStarted);

		StepOptional notifyDoneLatch(final CountDownLatch notifyDone);

		StepOptional notifyDoneLatch(final List<CountDownLatch> notifiersAboutDone);

		RunnableWrapper build();
	}

	private static class RunnableWrapperBuilder implements StepWrapperOfUserRunnable, StepErrorReporter, StepOptional {
		private Runnable wrappedRunnable;
		private Multimap<Runnable, Throwable> errorsPerRunnable;
		private final List<CountDownLatch> awaiters = new ArrayList<>();
		private final List<CountDownLatch> notifiersAboutStart = new ArrayList<>();
		private final List<CountDownLatch> notifiersAboutDone = new ArrayList<>();

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
			this.awaiters.add(await);
			return (StepOptional) this;
		}

		@Override
		public StepOptional awaitLatch(final List<CountDownLatch> awaiters) {
			this.awaiters.addAll(awaiters);
			return (StepOptional) this;
		}

		@Override
		public StepOptional notifyStartedLatch(final CountDownLatch notifyStarted) {
			this.notifiersAboutStart.add(notifyStarted);
			return (StepOptional) this;
		}

		@Override
		public StepOptional notifyStartedLatch(final List<CountDownLatch> notifiersAboutStarted) {
			this.notifiersAboutStart.addAll(notifiersAboutStarted);
			return (StepOptional) this;
		}

		@Override
		public StepOptional notifyDoneLatch(final CountDownLatch notifyDone) {
			this.notifiersAboutDone.add(notifyDone);
			return (StepOptional) this;
		}

		@Override
		public StepOptional notifyDoneLatch(final List<CountDownLatch> notifiersAboutDone) {
			this.notifiersAboutDone.addAll(notifiersAboutDone);
			return (StepOptional) this;
		}

		@Override
		public RunnableWrapper build() {
			return new RunnableWrapper(wrappedRunnable, errorsPerRunnable, awaiters, notifiersAboutStart,
					notifiersAboutDone);
		}
	}
}