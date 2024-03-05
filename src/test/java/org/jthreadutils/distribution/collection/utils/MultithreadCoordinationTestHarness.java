package org.jthreadutils.distribution.collection.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class MultithreadCoordinationTestHarness {

	public Multimap<Runnable, Throwable> allThreadsShouldStartOnSameTime(final Runnable... runnables) throws Exception {
		final CountDownLatch awaitStartAll = new CountDownLatch(runnables.length);
		final CountDownLatch awaitDoneAll = new CountDownLatch(runnables.length);
		final Multimap<Runnable, Throwable> errorsPerRunnable = Multimaps.synchronizedMultimap(HashMultimap.create());

		final ThreadGroup testThreadsGroup = new ThreadGroup("TestThreadGroup");
		final List<Thread> threads = new ArrayList<>(runnables.length);
		for (Runnable userRunnable : runnables) {
			threads.add(new Thread(testThreadsGroup,
					RunnableWrapper.builder().wrappedRunnable(userRunnable).errorReport(errorsPerRunnable)
							.notifyStartedLatch(awaitStartAll).awaitLatch(awaitStartAll).notifyDoneLatch(awaitDoneAll)
							.build(),
					"Thread-" + userRunnable));
		}

		for (Thread th : threads) {
			th.start();
		}

		awaitStartAll.await();
		awaitDoneAll.await();

		for (Thread th : threads) {
			th.join();
		}

		return errorsPerRunnable;
	}

	public Multimap<Runnable, Throwable> otherThreadsShouldProceedAfterPriorThread(final Runnable priorRunnable,
			final Runnable... runnables) throws Exception {

		return null;
	}

}
