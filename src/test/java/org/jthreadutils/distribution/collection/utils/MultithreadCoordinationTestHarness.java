package org.jthreadutils.distribution.collection.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * This class coordinates executing of multithreading tests scenario by using
 * {@link RunnableWrapper}.
 * 
 * @author adam-wypych
 * @since 1.0.0
 * @version %I%, %G%
 */
public class MultithreadCoordinationTestHarness {

	/**
	 * <b>Note! Timeout logic should be part of test method, internally code has
	 * infinity(0) timeout for any operation</b> This method should be used in case
	 * all tasks should start on same time. The only coordination is to make sure
	 * that before {@link Runnable#run()} is being executed all threads are ready.
	 * 
	 * @param runnables tasks to be executed by multiple threads
	 * @return any exceptions raised by {@link Runnable} with mapping to
	 *         corresponding task
	 * @throws Exception any exceptions raised from method but doesn't belongs to
	 *                   {@link Runnable}
	 * 
	 * @author adam-wypych
	 * @since 1.0.0
	 * @version %I%, %G%
	 */
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

	/**
	 * <b>Note! Timeout logic should be part of test method, internally code has
	 * infinity(0) timeout for any operation.</b> This method should be used in case
	 * one thread has higher priority than others and need to complete before other
	 * threads will start. Coordination is made around bringing all less priority
	 * threads into running state before prior thread will execute its
	 * {@link Runnable#run()}.
	 * 
	 * @param priorRunnable the task to be fully executed before other
	 *                      <code>runnables</code> will be executed
	 * @param runnables     less prior tasks to be executed after
	 *                      <code>priorRunnable</code>
	 * @return any exceptions raised by {@link Runnable} with mapping to
	 *         corresponding task
	 * @throws Exception any exceptions raised from method but doesn't belongs to
	 *                   {@link Runnable}
	 * 
	 * @author adam-wypych
	 * @since 1.0.0
	 * @version %I%, %G%
	 */
	public Multimap<Runnable, Throwable> otherThreadsShouldProceedAfterPriorThread(final Runnable priorRunnable,
			final Runnable... runnables) throws Exception {
		final CountDownLatch awaitStartAllDependant = new CountDownLatch(runnables.length);
		final CountDownLatch awaitStartPrior = new CountDownLatch(1);
		final CountDownLatch awaitDoneAllDependant = new CountDownLatch(runnables.length);
		final CountDownLatch awaitDonePrior = new CountDownLatch(1);

		final Multimap<Runnable, Throwable> errorsPerRunnable = Multimaps.synchronizedMultimap(HashMultimap.create());

		final ThreadGroup testThreadsGroup = new ThreadGroup("TestThreadGroup");
		final List<Thread> threads = new ArrayList<>(runnables.length + 1);

		threads.add(new Thread(testThreadsGroup,
				RunnableWrapper.builder().wrappedRunnable(priorRunnable).errorReport(errorsPerRunnable)
						.notifyStartedLatch(awaitStartPrior).awaitLatch(awaitStartAllDependant)
						.notifyDoneLatch(awaitDonePrior).build(),
				"Thread-Prior"));

		for (Runnable userRunnable : runnables) {
			threads.add(new Thread(testThreadsGroup,
					RunnableWrapper.builder().wrappedRunnable(userRunnable).errorReport(errorsPerRunnable)
							.notifyStartedLatch(awaitStartAllDependant).awaitLatch(awaitDonePrior)
							.notifyDoneLatch(awaitDoneAllDependant).build(),
					"Thread-" + userRunnable));
		}

		for (Thread th : threads) {
			th.start();
		}

		awaitStartPrior.await();
		awaitStartAllDependant.await();
		awaitDonePrior.await();
		awaitDoneAllDependant.await();

		for (Thread th : threads) {
			th.join();
		}

		return errorsPerRunnable;
	}

}
