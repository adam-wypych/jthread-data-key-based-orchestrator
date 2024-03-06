package org.jthreadutils.distribution.collection.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.assertj.core.data.TemporalUnitWithinOffset;
import org.junit.Test;

import com.google.common.collect.Multimap;

public class MultithreadCoordinationTestHarnessTests {

	@Test(timeout = 60000)
	public void givenThreeThreadsWhichShouldBeRunningPrallel__whenAllThreadsShouldStartOnSameTimeIsBeingUsed__thenAllThreadsShouldBeExecutedWithoutAwaiting()
			throws Exception {
		// prepare
		final ConcurrentHashMap<Runnable, LocalDateTime> runnableVsExecutionTime = new ConcurrentHashMap<>();

		List<Runnable> runnables = new ArrayList<>(3);
		for (int i = 0; i < 3; i++) {
			runnables.add(new Runnable() {
				public void run() {
					runnableVsExecutionTime.put(this, LocalDateTime.now());
				}
			});
		}

		// execute
		Multimap<Runnable, Throwable> errorsPerRunnable = new MultithreadCoordinationTestHarness()
				.allThreadsShouldStartOnSameTime(runnables.toArray(new Runnable[3]));

		// verify
		assertThat(errorsPerRunnable.isEmpty()).isTrue();
		assertThat(runnableVsExecutionTime).hasSize(3);
		List<LocalDateTime> executionTimes = new ArrayList<>(runnableVsExecutionTime.values());
		executionTimes.sort((a, b) -> {
			return a.compareTo(b);
		});
		assertThat(executionTimes.get(2)).isCloseTo(executionTimes.get(1),
				new TemporalUnitWithinOffset(1L, ChronoUnit.SECONDS));
		assertThat(executionTimes.get(1)).isCloseTo(executionTimes.get(0),
				new TemporalUnitWithinOffset(1L, ChronoUnit.SECONDS));
	}

	@Test(timeout = 60000)
	public void givenThreeDependantThreadsAndOnePrior__whenOtherThreadsShouldProceedAfterPriorThreadIsBeingUsed__thenThePriorThreadShouldStartAsTheFirst()
			throws Exception {
		// prepare
		final ConcurrentHashMap<Runnable, LocalDateTime> runnableVsExecutionTime = new ConcurrentHashMap<>();

		Runnable priorRunnable = new Runnable() {
			public void run() {
				runnableVsExecutionTime.put(this, LocalDateTime.now());
				try {
					Thread.sleep(100L);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			};
		};

		List<Runnable> runnables = new ArrayList<>(3);
		for (int i = 0; i < 3; i++) {
			runnables.add(new Runnable() {
				public void run() {
					runnableVsExecutionTime.put(this, LocalDateTime.now());
				}
			});
		}

		// execute
		Multimap<Runnable, Throwable> errorsPerRunnable = new MultithreadCoordinationTestHarness()
				.otherThreadsShouldProceedAfterPriorThread(priorRunnable, runnables.toArray(new Runnable[3]));

		// verify
		assertThat(errorsPerRunnable.isEmpty()).isTrue();
		assertThat(runnableVsExecutionTime).hasSize(4);
		List<LocalDateTime> executionTimes = new ArrayList<>(runnableVsExecutionTime.values());
		executionTimes.sort((a, b) -> {
			return a.compareTo(b);
		});
		
		assertThat(runnableVsExecutionTime.get(priorRunnable)).isEqualTo(executionTimes.get(0));

		assertThat(executionTimes.get(3)).isCloseTo(executionTimes.get(2),
				new TemporalUnitWithinOffset(1L, ChronoUnit.SECONDS));
		assertThat(executionTimes.get(2)).isCloseTo(executionTimes.get(1),
				new TemporalUnitWithinOffset(1L, ChronoUnit.SECONDS));
		assertThat(executionTimes.get(1)).isAfterOrEqualTo(executionTimes.get(0).plus(100L, ChronoUnit.MILLIS));
	}
}
