package org.jthreadutils.distribution.collection.immutable.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jthreadutils.distribution.collection.utils.MultithreadCoordinationTestHarness;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Multimap;

public class ArrayListLockableCollectionMultiThreadTests {

	private ArrayListLockableCollection<String> sut;

	@Test(timeout = 60000)
	public void givenTwoThreads__whenSortIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<>(collection);
		expected.sort(Comparator.naturalOrder());
		
		Runnable priorRunnable = () -> { sut.sort(Comparator.naturalOrder()); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.sort(Collections.reverseOrder()); };
		
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
	public void givenTwoThreads__whenReplaceAllIsBeingExecuted_andMutableCollection__thenBothThreadsShouldSucceed() throws Exception {
		// prepare
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = Arrays.asList("A0", "A1", "2", "B3", "B4");
		
		Runnable firstRunnable = () -> { sut.replaceAll(e -> (e.equals("0") || e.equals("1")) ? "A" + e : e); };
		Runnable secondRunnable = () -> { sut.replaceAll(e -> (e.equals("3") || e.equals("4")) ? "B" + e : e); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(firstRunnable, secondRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isTrue();
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenReplaceAllIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = Arrays.asList("A0", "A1", "2", "3", "4");
		
		Runnable priorRunnable = () -> { sut.replaceAll(e -> (e.equals("0") || e.equals("1")) ? "A" + e : e); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.replaceAll(e -> (e.equals("3") || e.equals("4")) ? "B" + e : e); };
		
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
	public void givenTwoThreads__whenRemoveIfIsBeingExecuted_andMutableCollection__thenBothThreadsShouldSucceed() throws Exception {
		// prepare
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = Arrays.asList("2", "3");
		
		Runnable firstRunnable = () -> { sut.removeIf(e -> e.equals("1") || e.equals("0")); };
		Runnable secondRunnable = () -> { sut.removeIf(e -> e.equals("4") || e.equals("5")); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(firstRunnable, secondRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isTrue();
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenRemoveIfIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = Arrays.asList("1");
		
		Runnable priorRunnable = () -> { sut.removeIf(e -> !e.equals("1")); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.removeIf(e -> e.equals("1")); };
		
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
	public void givenTwoThreads__whenRetainAllIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = Arrays.asList("1", "2");
		
		Runnable priorRunnable = () -> { sut.retainAll(Arrays.asList("1", "2")); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.retainAll(Arrays.asList("1", "4")); };
		
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
	public void givenTwoThreads__whenRemoveAllIsBeingExecuted_andMutableCollection__thenBothThreadsShouldSucceed() throws Exception {
		// prepare
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = Arrays.asList("2", "3");
		
		Runnable firstRunnable = () -> { sut.removeAll(Arrays.asList("0", "5")); };
		Runnable secondRunnable = () -> { sut.removeAll(Arrays.asList("1", "4")); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(firstRunnable, secondRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isTrue();
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenRemoveAllIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = Arrays.asList("1", "2", "3", "4");
		
		Runnable priorRunnable = () -> { sut.removeAll(Arrays.asList("0", "5")); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.removeAll(Arrays.asList("1", "4")); };
		
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
	public void givenTwoThreads__whenRemoveRangeIsBeingExecuted_andMutableCollection__thenBothThreadsShouldSucceed() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection.subList(2, 3));
		
		Runnable firstRunnable = () -> { sut.removeRange(0, 2); };
		Runnable secondRunnable = () -> { sut.removeRange(sut.size() - 2, sut.size()); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(firstRunnable, secondRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isTrue();
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenRemoveRangeIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection.subList(2, 5));
		
		Runnable priorRunnable = () -> { sut.removeRange(0, 2); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.removeRange(3, 4); };
		
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
	public void givenTwoThreads__whenAddAllAtIndexIsBeingExecuted_andMutableCollection__thenBothThreadsShouldSucceed() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.addAll(0, collection);
		expected.addAll(0, collection);
		
		Runnable firstRunnable = () -> { sut.addAll(0, collection); };
		Runnable secondRunnable = () -> { sut.addAll(0, collection); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(firstRunnable, secondRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isTrue();
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenAddAllAtIndexIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.addAll(0, collection);
		
		Runnable priorRunnable = () -> { sut.addAll(0, collection); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.addAll(0, collection); };
		
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
	public void givenTwoThreads__whenAddAllIsBeingExecuted_andMutableCollection__thenBothThreadsShouldSucceed() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.addAll(collection);
		expected.addAll(collection);
		
		Runnable firstRunnable = () -> { sut.addAll(collection); };
		Runnable secondRunnable = () -> { sut.addAll(collection); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(firstRunnable, secondRunnable);
		
		// verify
		assertThat(sut).isEqualTo(expected);
		assertThat(errorsPerThread.isEmpty()).isTrue();
	}
	
	@Test(timeout = 60000)
	public void givenTwoThreads__whenAddAllIsBeingExecuted_andOneThreadIsMarkingCollectionAsImmutable__thenOtherThreadsShouldFailWithUpdates() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.addAll(collection);
		
		Runnable priorRunnable = () -> { sut.addAll(collection); sut.immutable(); };
		Runnable secondUnsuccessfulRunnable = () -> { sut.addAll(collection); };
		
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
	public void givenTwoThreads__whenClearIsBeingExecuted_andMutableCollection__thenBothThreadsShouldSucceed() throws Exception {
		// prepare
		final List<String> collection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		sut.addAll(collection);
		final List<String> expected = new ArrayList<String>(collection);
		expected.clear();
		
		Runnable firstRunnable = () -> { sut.remove("0"); sut.add("1"); };
		Runnable secondRunnable = () -> { sut.clear(); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(firstRunnable, secondRunnable);
		
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
		
		Runnable firstRunnable = () -> { sut.remove("0"); };
		Runnable secondRunnable = () -> { sut.remove("1"); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(firstRunnable, secondRunnable);
		
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
		
		Runnable firstRunnable = () -> { sut.remove(0); };
		Runnable secondRunnable = () -> { sut.remove(1); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(firstRunnable, secondRunnable);
		
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
		
		Runnable firstRunnable = () -> { sut.add(0, "<REPLACED>"); };
		Runnable secondRunnable = () -> { sut.add(1, "<ERROR>"); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(firstRunnable, secondRunnable);
		
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
		
		Runnable firstRunnable = () -> { sut.add("<REPLACED>"); };
		Runnable secondRunnable = () -> { sut.add("<REPLACED>"); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(firstRunnable, secondRunnable);
		
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
		
		Runnable firstRunnable = () -> { sut.set(0, "<REPLACED>"); };
		Runnable secondRunnable = () -> { sut.set(1, "<ERROR>"); };
		
		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness().otherThreadsShouldProceedAfterPriorThread(firstRunnable, secondRunnable);
		
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
