package org.jthreadutils.distribution.collection.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.google.common.collect.Multimap;

public class RunnableWrapperBuilderTests {

	@Test
	public void givenOnlyMandatoryFieldsAreSet__whenRunnableWrapperIsBeingCreated__thenOptionalLatchesShouldNotBeSet()
			throws Exception {
		// prepare
		Runnable toWrap = mock(Runnable.class);
		@SuppressWarnings("unchecked")
		Multimap<Runnable, Throwable> errorsPerRunnable = mock(Multimap.class);

		// execute
		RunnableWrapper sut = RunnableWrapper.builder().wrappedRunnable(toWrap).errorReport(errorsPerRunnable).build();

		// verify
		assertThat(sut.wrappedRunnable).isSameAs(toWrap);
		assertThat(sut.errorsPerRunnable).isSameAs(errorsPerRunnable);
		assertThat(sut.notifiersAboutStart).isEmpty();
		assertThat(sut.awaiters).isEmpty();
		assertThat(sut.notifiersAboutDone).isEmpty();
	}

	@Test
	public void givenOneNotifierStartAdded__whenRunnableWrapperIsBeingCreated__thenItShouldBeReflectedInCreatedWrapper()
			throws Exception {
		// prepare
		Runnable toWrap = mock(Runnable.class);
		@SuppressWarnings("unchecked")
		Multimap<Runnable, Throwable> errorsPerRunnable = mock(Multimap.class);
		CountDownLatch theFirstNotifierAboutStart = mock(CountDownLatch.class);

		// execute
		RunnableWrapper sut = RunnableWrapper.builder().wrappedRunnable(toWrap).errorReport(errorsPerRunnable)
				.notifyStartedLatch(theFirstNotifierAboutStart).build();

		// verify
		assertThat(sut.wrappedRunnable).isSameAs(toWrap);
		assertThat(sut.errorsPerRunnable).isSameAs(errorsPerRunnable);
		assertThat(sut.notifiersAboutStart).containsExactly(theFirstNotifierAboutStart);
		assertThat(sut.awaiters).isEmpty();
		assertThat(sut.notifiersAboutDone).isEmpty();
	}

	@Test
	public void givenThreeNotifierStartAdded__whenRunnableWrapperIsBeingCreated__thenItShouldBeReflectedInCreatedWrapper()
			throws Exception {
		// prepare
		Runnable toWrap = mock(Runnable.class);
		@SuppressWarnings("unchecked")
		Multimap<Runnable, Throwable> errorsPerRunnable = mock(Multimap.class);
		CountDownLatch theFirstNotifierAboutStart = mock(CountDownLatch.class);
		CountDownLatch theSecondNotifierAboutStart = mock(CountDownLatch.class);
		CountDownLatch theThirdNotifierAboutStart = mock(CountDownLatch.class);

		// execute
		RunnableWrapper sut = RunnableWrapper.builder().wrappedRunnable(toWrap).errorReport(errorsPerRunnable)
				.notifyStartedLatch(theFirstNotifierAboutStart).notifyStartedLatch(theSecondNotifierAboutStart)
				.notifyStartedLatch(theThirdNotifierAboutStart).build();

		// verify
		assertThat(sut.wrappedRunnable).isSameAs(toWrap);
		assertThat(sut.errorsPerRunnable).isSameAs(errorsPerRunnable);
		assertThat(sut.notifiersAboutStart).containsExactly(theFirstNotifierAboutStart, theSecondNotifierAboutStart,
				theThirdNotifierAboutStart);
		assertThat(sut.awaiters).isEmpty();
		assertThat(sut.notifiersAboutDone).isEmpty();
	}

	@Test
	public void givenThreeNotifierStartAddedUsingLists__whenRunnableWrapperIsBeingCreated__thenItShouldBeReflectedInCreatedWrapper()
			throws Exception {
		// prepare
		Runnable toWrap = mock(Runnable.class);
		@SuppressWarnings("unchecked")
		Multimap<Runnable, Throwable> errorsPerRunnable = mock(Multimap.class);
		CountDownLatch theFirstNotifierAboutStart = mock(CountDownLatch.class);
		CountDownLatch theSecondNotifierAboutStart = mock(CountDownLatch.class);
		CountDownLatch theThirdNotifierAboutStart = mock(CountDownLatch.class);

		// execute
		RunnableWrapper sut = RunnableWrapper.builder().wrappedRunnable(toWrap).errorReport(errorsPerRunnable)
				.notifyStartedLatch(Arrays.asList(theFirstNotifierAboutStart))
				.notifyStartedLatch(Arrays.asList(theSecondNotifierAboutStart))
				.notifyStartedLatch(Arrays.asList(theThirdNotifierAboutStart)).build();

		// verify
		assertThat(sut.wrappedRunnable).isSameAs(toWrap);
		assertThat(sut.errorsPerRunnable).isSameAs(errorsPerRunnable);
		assertThat(sut.notifiersAboutStart).containsExactly(theFirstNotifierAboutStart, theSecondNotifierAboutStart,
				theThirdNotifierAboutStart);
		assertThat(sut.awaiters).isEmpty();
		assertThat(sut.notifiersAboutDone).isEmpty();
	}

	@Test
	public void givenOneAwaiterAdded__whenRunnableWrapperIsBeingCreated__thenItShouldBeReflectedInCreatedWrapper()
			throws Exception {
		// prepare
		Runnable toWrap = mock(Runnable.class);
		@SuppressWarnings("unchecked")
		Multimap<Runnable, Throwable> errorsPerRunnable = mock(Multimap.class);
		CountDownLatch theFirstAwaiter = mock(CountDownLatch.class);

		// execute
		RunnableWrapper sut = RunnableWrapper.builder().wrappedRunnable(toWrap).errorReport(errorsPerRunnable)
				.awaitLatch(theFirstAwaiter).build();

		// verify
		assertThat(sut.wrappedRunnable).isSameAs(toWrap);
		assertThat(sut.errorsPerRunnable).isSameAs(errorsPerRunnable);
		assertThat(sut.notifiersAboutStart).isEmpty();
		assertThat(sut.awaiters).containsExactly(theFirstAwaiter);
		assertThat(sut.notifiersAboutDone).isEmpty();
	}

	@Test
	public void givenThreeAwaitersAdded__whenRunnableWrapperIsBeingCreated__thenItShouldBeReflectedInCreatedWrapper()
			throws Exception {
		// prepare
		Runnable toWrap = mock(Runnable.class);
		@SuppressWarnings("unchecked")
		Multimap<Runnable, Throwable> errorsPerRunnable = mock(Multimap.class);
		CountDownLatch theFirstAwaiter = mock(CountDownLatch.class);
		CountDownLatch theSecondAwaiter = mock(CountDownLatch.class);
		CountDownLatch theThirdAwaiter = mock(CountDownLatch.class);

		// execute
		RunnableWrapper sut = RunnableWrapper.builder().wrappedRunnable(toWrap).errorReport(errorsPerRunnable)
				.awaitLatch(theFirstAwaiter).awaitLatch(theSecondAwaiter).awaitLatch(theThirdAwaiter).build();

		// verify
		assertThat(sut.wrappedRunnable).isSameAs(toWrap);
		assertThat(sut.errorsPerRunnable).isSameAs(errorsPerRunnable);
		assertThat(sut.notifiersAboutStart).isEmpty();
		assertThat(sut.awaiters).containsExactly(theFirstAwaiter, theSecondAwaiter, theThirdAwaiter);
		assertThat(sut.notifiersAboutDone).isEmpty();
	}

	@Test
	public void givenThreeAwaitersAddedUsingLists__whenRunnableWrapperIsBeingCreated__thenItShouldBeReflectedInCreatedWrapper()
			throws Exception {
		// prepare
		Runnable toWrap = mock(Runnable.class);
		@SuppressWarnings("unchecked")
		Multimap<Runnable, Throwable> errorsPerRunnable = mock(Multimap.class);
		CountDownLatch theFirstAwaiter = mock(CountDownLatch.class);
		CountDownLatch theSecondAwaiter = mock(CountDownLatch.class);
		CountDownLatch theThirdAwaiter = mock(CountDownLatch.class);

		// execute
		RunnableWrapper sut = RunnableWrapper.builder().wrappedRunnable(toWrap).errorReport(errorsPerRunnable)
				.awaitLatch(Arrays.asList(theFirstAwaiter)).awaitLatch(Arrays.asList(theSecondAwaiter))
				.awaitLatch(Arrays.asList(theThirdAwaiter)).build();

		// verify
		assertThat(sut.wrappedRunnable).isSameAs(toWrap);
		assertThat(sut.errorsPerRunnable).isSameAs(errorsPerRunnable);
		assertThat(sut.notifiersAboutStart).isEmpty();
		assertThat(sut.awaiters).containsExactly(theFirstAwaiter, theSecondAwaiter, theThirdAwaiter);
		assertThat(sut.notifiersAboutDone).isEmpty();
	}

	@Test
	public void givenOneNotifierDoneAdded__whenRunnableWrapperIsBeingCreated__thenItShouldBeReflectedInCreatedWrapper()
			throws Exception {
		// prepare
		Runnable toWrap = mock(Runnable.class);
		@SuppressWarnings("unchecked")
		Multimap<Runnable, Throwable> errorsPerRunnable = mock(Multimap.class);
		CountDownLatch theFirstNotifierAboutDone = mock(CountDownLatch.class);

		// execute
		RunnableWrapper sut = RunnableWrapper.builder().wrappedRunnable(toWrap).errorReport(errorsPerRunnable)
				.notifyDoneLatch(theFirstNotifierAboutDone).build();

		// verify
		assertThat(sut.wrappedRunnable).isSameAs(toWrap);
		assertThat(sut.errorsPerRunnable).isSameAs(errorsPerRunnable);
		assertThat(sut.notifiersAboutStart).isEmpty();
		assertThat(sut.awaiters).isEmpty();
		assertThat(sut.notifiersAboutDone).containsExactly(theFirstNotifierAboutDone);
	}

	@Test
	public void givenThreeNotifierDoneAdded__whenRunnableWrapperIsBeingCreated__thenItShouldBeReflectedInCreatedWrapper()
			throws Exception {
		// prepare
		Runnable toWrap = mock(Runnable.class);
		@SuppressWarnings("unchecked")
		Multimap<Runnable, Throwable> errorsPerRunnable = mock(Multimap.class);
		CountDownLatch theFirstNotifierAboutDone = mock(CountDownLatch.class);
		CountDownLatch theSecondNotifierAboutDone = mock(CountDownLatch.class);
		CountDownLatch theThirdNotifierAboutDone = mock(CountDownLatch.class);

		// execute
		RunnableWrapper sut = RunnableWrapper.builder().wrappedRunnable(toWrap).errorReport(errorsPerRunnable)
				.notifyDoneLatch(theFirstNotifierAboutDone).notifyDoneLatch(theSecondNotifierAboutDone)
				.notifyDoneLatch(theThirdNotifierAboutDone).build();

		// verify
		assertThat(sut.wrappedRunnable).isSameAs(toWrap);
		assertThat(sut.errorsPerRunnable).isSameAs(errorsPerRunnable);
		assertThat(sut.notifiersAboutStart).isEmpty();
		assertThat(sut.awaiters).isEmpty();
		assertThat(sut.notifiersAboutDone).containsExactly(theFirstNotifierAboutDone, theSecondNotifierAboutDone,
				theThirdNotifierAboutDone);
	}

	@Test
	public void givenThreeNotifierDoneAddedUsingLists__whenRunnableWrapperIsBeingCreated__thenItShouldBeReflectedInCreatedWrapper()
			throws Exception {
		// prepare
		Runnable toWrap = mock(Runnable.class);
		@SuppressWarnings("unchecked")
		Multimap<Runnable, Throwable> errorsPerRunnable = mock(Multimap.class);
		CountDownLatch theFirstNotifierAboutDone = mock(CountDownLatch.class);
		CountDownLatch theSecondNotifierAboutDone = mock(CountDownLatch.class);
		CountDownLatch theThirdNotifierAboutDone = mock(CountDownLatch.class);

		// execute
		RunnableWrapper sut = RunnableWrapper.builder().wrappedRunnable(toWrap).errorReport(errorsPerRunnable)
				.notifyDoneLatch(Arrays.asList(theFirstNotifierAboutDone))
				.notifyDoneLatch(Arrays.asList(theSecondNotifierAboutDone))
				.notifyDoneLatch(Arrays.asList(theThirdNotifierAboutDone)).build();

		// verify
		assertThat(sut.wrappedRunnable).isSameAs(toWrap);
		assertThat(sut.errorsPerRunnable).isSameAs(errorsPerRunnable);
		assertThat(sut.notifiersAboutStart).isEmpty();
		assertThat(sut.awaiters).isEmpty();
		assertThat(sut.notifiersAboutDone).containsExactly(theFirstNotifierAboutDone, theSecondNotifierAboutDone,
				theThirdNotifierAboutDone);
	}

	@Test
	public void givenThreeNotifiersForDoneAndStartPlusThreeAwaitersAdded__whenRunnableWrapperIsBeingCreated__thenItShouldBeReflectedInCreatedWrapper()
			throws Exception {
		// prepare
		Runnable toWrap = mock(Runnable.class);
		@SuppressWarnings("unchecked")
		Multimap<Runnable, Throwable> errorsPerRunnable = mock(Multimap.class);
		CountDownLatch theFirstNotifierAboutStart = mock(CountDownLatch.class);
		CountDownLatch theSecondNotifierAboutStart = mock(CountDownLatch.class);
		CountDownLatch theThirdNotifierAboutStart = mock(CountDownLatch.class);

		CountDownLatch theFirstAwaiter = mock(CountDownLatch.class);
		CountDownLatch theSecondAwaiter = mock(CountDownLatch.class);
		CountDownLatch theThirdAwaiter = mock(CountDownLatch.class);

		CountDownLatch theFirstNotifierAboutDone = mock(CountDownLatch.class);
		CountDownLatch theSecondNotifierAboutDone = mock(CountDownLatch.class);
		CountDownLatch theThirdNotifierAboutDone = mock(CountDownLatch.class);

		// execute
		RunnableWrapper sut = RunnableWrapper.builder().wrappedRunnable(toWrap).errorReport(errorsPerRunnable)
				.notifyStartedLatch(theFirstNotifierAboutStart).notifyStartedLatch(theSecondNotifierAboutStart)
				.notifyStartedLatch(theThirdNotifierAboutStart).awaitLatch(theFirstAwaiter).awaitLatch(theSecondAwaiter)
				.awaitLatch(theThirdAwaiter).notifyDoneLatch(theFirstNotifierAboutDone)
				.notifyDoneLatch(theSecondNotifierAboutDone).notifyDoneLatch(theThirdNotifierAboutDone).build();

		// verify
		assertThat(sut.wrappedRunnable).isSameAs(toWrap);
		assertThat(sut.errorsPerRunnable).isSameAs(errorsPerRunnable);
		assertThat(sut.notifiersAboutStart).containsExactly(theFirstNotifierAboutStart, theSecondNotifierAboutStart,
				theThirdNotifierAboutStart);
		assertThat(sut.awaiters).containsExactly(theFirstAwaiter, theSecondAwaiter, theThirdAwaiter);
		assertThat(sut.notifiersAboutDone).containsExactly(theFirstNotifierAboutDone, theSecondNotifierAboutDone,
				theThirdNotifierAboutDone);
	}
}
