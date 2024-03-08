package org.jthreadutils.distribution.collection.immutable.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jthreadutils.distribution.collection.utils.MultithreadCoordinationTestHarness;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Multimap;

public class ArrayListLockableCollectionMultiThreadTests {

	private ArrayListLockableCollection<Object> sut;
	
	@Test(timeout = 60000)
	public void given__when__then() throws Exception {
		// prepare
		
		// execute
		
		// verify
	}

	@Test(timeout = 60000)
	public void givenTwoThreads__whenClearIsBeingExecuted_andMutableCollection__thenBothThreadsShouldSucceed() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.clear();
		
		Runnable priorRunnable = () -> { sut.remove("0"); sut.add("1"); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.clear(); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(priorRunnable, secondUnsuccessfulRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isTrue();
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenClearIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.clear();
		
		Runnable priorRunnable = () -> { sut.clear(); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.add("1"); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(priorRunnable, secondUnsuccessfulRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isFalse();
		assertThat(errorsPerThread.keySet()).containsExactlyInAnyOrder(secondUnsuccessfulRunnable);
		assertThat(errorsPerThread.get(secondUnsuccessfulRunnable)).hasSize(1);
		assertThat(new ArrayList<>(errorsPerThread.get(secondUnsuccessfulRunnable)).get(0)).isInstanceOf(UnsupportedOperationException.class);
	}	
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenRemoveElementIsBeingExecuted_andMutableCollection__thenBothThreadsShouldSucceed() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.remove("0");
		expected.remove("1");
		
		Runnable priorRunnable = () -> { sut.remove("0"); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.remove("1"); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(priorRunnable, secondUnsuccessfulRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isTrue();
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenRemoveElementIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.remove("0");
		
		Runnable priorRunnable = () -> { sut.remove("0"); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.remove("1"); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(priorRunnable, secondUnsuccessfulRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isFalse();
		assertThat(errorsPerThread.keySet()).containsExactlyInAnyOrder(secondUnsuccessfulRunnable);
		assertThat(errorsPerThread.get(secondUnsuccessfulRunnable)).hasSize(1);
		assertThat(new ArrayList<>(errorsPerThread.get(secondUnsuccessfulRunnable)).get(0)).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test(timeout = 60000)
	public void givenTwoThreads__whenRemoveElementAtIndexIsBeingExecuted_andMutableCollection__thenBothThreadsShouldSucceed() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.remove(0);
		expected.remove(1);
		
		Runnable priorRunnable = () -> { sut.remove(0); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.remove(1); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(priorRunnable, secondUnsuccessfulRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isTrue();
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenRemoveElementAtIndexIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.remove(0);
		
		Runnable priorRunnable = () -> { sut.remove(0); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.remove(1); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(priorRunnable, secondUnsuccessfulRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isFalse();
		assertThat(errorsPerThread.keySet()).containsExactlyInAnyOrder(secondUnsuccessfulRunnable);
		assertThat(errorsPerThread.get(secondUnsuccessfulRunnable)).hasSize(1);
		assertThat(new ArrayList<>(errorsPerThread.get(secondUnsuccessfulRunnable)).get(0)).isInstanceOf(UnsupportedOperationException.class);
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenAddElementAtIndexIsBeingExecuted_andMutableCollection__thenBothThreadsShouldSucceed() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.add(0, "<REPLACED>");
		expected.add(1, "<ERROR>");
		
		Runnable priorRunnable = () -> { sut.add(0, "<REPLACED>"); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.add(1, "<ERROR>"); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(priorRunnable, secondUnsuccessfulRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isTrue();
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenAddElementAtIndexIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.add(0, "<REPLACED>");
		
		Runnable priorRunnable = () -> { sut.add(0, "<REPLACED>"); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.add(1, "<ERROR>"); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(priorRunnable, secondUnsuccessfulRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isFalse();
		assertThat(errorsPerThread.keySet()).containsExactlyInAnyOrder(secondUnsuccessfulRunnable);
		assertThat(errorsPerThread.get(secondUnsuccessfulRunnable)).hasSize(1);
		assertThat(new ArrayList<>(errorsPerThread.get(secondUnsuccessfulRunnable)).get(0)).isInstanceOf(UnsupportedOperationException.class);
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenAddElementIsBeingExecuted_andMutableCollection__thenBothThreadsShouldSucceed() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.add("<REPLACED>");
		expected.add("<REPLACED>");
		
		Runnable priorRunnable = () -> { sut.add("<REPLACED>"); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.add("<REPLACED>"); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(priorRunnable, secondUnsuccessfulRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isTrue();
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenAddElementIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.add("<REPLACED>");
		
		Runnable priorRunnable = () -> { sut.add("<REPLACED>"); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.add("<ERROR>"); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(priorRunnable, secondUnsuccessfulRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isFalse();
		assertThat(errorsPerThread.keySet()).containsExactlyInAnyOrder(secondUnsuccessfulRunnable);
		assertThat(errorsPerThread.get(secondUnsuccessfulRunnable)).hasSize(1);
		assertThat(new ArrayList<>(errorsPerThread.get(secondUnsuccessfulRunnable)).get(0)).isInstanceOf(UnsupportedOperationException.class);
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenSetElementIsBeingExecuted_andMutableCollection__thenBothThreadsShouldSucceed() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.set(0, "<REPLACED>");
		expected.set(1, "<ERROR>");
		
		Runnable priorRunnable = () -> { sut.set(0, "<REPLACED>"); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.set(1, "<ERROR>"); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(priorRunnable, secondUnsuccessfulRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isTrue();
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenSetElementIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.set(0, "<REPLACED>");
		
		Runnable priorRunnable = () -> { sut.set(0, "<REPLACED>"); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.set(1, "<ERROR>"); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(priorRunnable, secondUnsuccessfulRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isFalse();
		assertThat(errorsPerThread.keySet()).containsExactlyInAnyOrder(secondUnsuccessfulRunnable);
		assertThat(errorsPerThread.get(secondUnsuccessfulRunnable)).hasSize(1);
		assertThat(new ArrayList<>(errorsPerThread.get(secondUnsuccessfulRunnable)).get(0)).isInstanceOf(UnsupportedOperationException.class);
	}

	@Before
	public void setUp() {
		this.sut = new ArrayListLockableCollection<>();
	}
}
