package org.jthreadutils.distribution.collection.immutable.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class ArrayListLockableCollectionMultiThreadTests {

	private ArrayListLockableCollection<Object> sut;

	@Test(timeout = 60000)
	public void givenTwoThreadsRunning_andCollectionIsBeingLockedForSecondThread__whenSetElementAtIndex__thenOnlyFirstThreadShouldBeSuccessful() throws Exception {
		// prepare
		sut.addAll(IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList()));
		
		final CountDownLatch markerThatFirstThreadStarted = new CountDownLatch(1);
		final CountDownLatch markerThatSecondThreadStarted = new CountDownLatch(1);
		final Callable<?> firstThreadCallableAction = () -> { return sut.set(1, "X"); };
		final Callable<?> secondThreadCallableAction = () -> { return sut.set(2, "Z"); };
		final Multimap<Thread, Throwable> errorsPerThread = Multimaps.synchronizedMultimap(HashMultimap.create());
		
		Runnable theFirstThreadAction = () -> {
			try {
				markerThatSecondThreadStarted.await();
			} catch (InterruptedException e) {
				errorsPerThread.put(Thread.currentThread(), e);
			}
			
			try {
				firstThreadCallableAction.call();
			} catch (Exception e) {
				errorsPerThread.put(Thread.currentThread(), e);
			}
			
			sut.immutable();
			markerThatFirstThreadStarted.countDown();
		};
		
		Runnable theSecondThreadAction = () -> {
			markerThatSecondThreadStarted.countDown();

			try {
				markerThatFirstThreadStarted.await();
			} catch (Exception e) {
				errorsPerThread.put(Thread.currentThread(), e);
			}
			
			try {
				secondThreadCallableAction.call();
			} catch (Exception e) {
				errorsPerThread.put(Thread.currentThread(), e);
			}
		};
		
		Thread theFirstThread = new Thread(theFirstThreadAction);
		Thread theSecondThread = new Thread(theSecondThreadAction);
		
		// execute
		theFirstThread.start();
		theSecondThread.start();
		
		theFirstThread.join();
		theSecondThread.join();
		
		// verify
		assertThat(errorsPerThread.isEmpty()).isTrue();
	}
	
	@Before
	public void setUp() {
		this.sut = new ArrayListLockableCollection<>();
	}
}
