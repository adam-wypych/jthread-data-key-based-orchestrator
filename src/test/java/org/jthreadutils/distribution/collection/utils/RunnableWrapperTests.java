package org.jthreadutils.distribution.collection.utils;

import org.junit.Test;
import org.mockito.InOrder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

public class RunnableWrapperTests {

	@Test
	public void givenAllLatchesAreAvailable_andNoErrors__whenRunIsBeingExecuted__thenEmptyMapWithErrorsShouldBeReturnAndAllLatchesHit()
			throws Exception {
		// prepare
		Runnable wrapped = mock(Runnable.class);

		// execute & verify
		assertThat(executeFullRunInWrapperAndVerifyIfAllLatchesWasExecuted(wrapped).isEmpty()).isTrue();
	}

	@Test
	public void givenAllLatchesAreAvailable_andError__whenRunIsBeingExecuted__thenEmptyMapWithErrorsShouldBeReturnAndAllLatchesHit()
			throws Exception {
		// prepare
		Runnable wrapped = mock(Runnable.class);
		RuntimeException error = new RuntimeException();
		doThrow(error).when(wrapped).run();

		// execute
		Multimap<Runnable, Throwable> errorsPerRunnable = executeFullRunInWrapperAndVerifyIfAllLatchesWasExecuted(wrapped);

		// verify
		assertThat(errorsPerRunnable.get(wrapped)).containsExactly(error);
	}

	private Multimap<Runnable, Throwable> executeFullRunInWrapperAndVerifyIfAllLatchesWasExecuted(final Runnable wrapped) throws Exception {
		// prepare
		Multimap<Runnable, Throwable> errorsPerRunnable = ArrayListMultimap.create();
		List<CountDownLatch> notifiersAboutStart = mockedCountDownLatches(3);
		List<CountDownLatch> awaiters = mockedCountDownLatches(3);
		List<CountDownLatch> notifiersAboutDone = mockedCountDownLatches(3);

		RunnableWrapper sut = RunnableWrapper.builder().wrappedRunnable(wrapped).errorReport(errorsPerRunnable)
				.notifyStartedLatch(notifiersAboutStart).awaitLatch(awaiters).notifyDoneLatch(notifiersAboutDone)
				.build();

		List<Object> allMocks = new ArrayList<>();
		allMocks.addAll(notifiersAboutStart);
		allMocks.addAll(awaiters);
		allMocks.addAll(notifiersAboutDone);
		allMocks.add(wrapped);

		// execute
		sut.run();

		// verify
		InOrder verify = inOrder(allMocks.toArray(new Object[allMocks.size()]));
		notifiersAboutStart.stream().forEach(e -> verify.verify(e, times(1)).countDown());
		for (CountDownLatch awaiter : awaiters) {
			verify.verify(awaiter, times(1)).await();
		}
		verify.verify(wrapped, times(1)).run();
		notifiersAboutDone.stream().forEach(e -> verify.verify(e, times(1)).countDown());

		return errorsPerRunnable;
	}

	private List<CountDownLatch> mockedCountDownLatches(final int number) {
		List<CountDownLatch> latches = new ArrayList<>(number);
		for (int i = 0; i < number; i++) {
			latches.add(mock(CountDownLatch.class));
		}

		return latches;
	}

}
