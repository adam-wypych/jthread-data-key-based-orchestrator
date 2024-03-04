package org.jthreadutils.distribution.collection.immutable.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

public class ArrayListLockableCollectionSingleThreadTests {
	
	private ArrayListLockableCollection<Object> sut;

	@Test
	public void testIfMutabilityFlagWorks() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		
		// set object as immutable
		sut.immutable();
		try {
			sut.add("X");
			fail("Expected exception to be thrown.");
		} catch (UnsupportedOperationException e) {}
		
		// set object as mutable
		sut.mutable();
		sut.add("Z");
		assertThat(sut).containsExactly("0", "1", "2", "3", "4", "Z");
	}
	
	@Test
	public void givenCollectionIsUnlocked__whenSetElementAtIndex__thenOperationShouldBeSuccessful() {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
	
		// execute
		assertThat(sut.set(3, "<REPLACED>")).isEqualTo("3");
		originalCollection.set(3, "<REPLACED>");
		
		// verify
		assertThat(originalCollection).isEqualTo(sut);
	}
	
	@Test
	public void givenCollectionIsLocked__whenSetElementAtIndex__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { return sut.set(3, "<REPLACED>"); }); 
		assertThat(originalCollection).isEqualTo(sut);
	}
	
	@Test
	public void givenCollectionIsUnlocked__whenAddElement__thenOperationShouldBeSuccesful() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		
		// execute
		assertThat(sut.add("ADD")).isTrue();
		originalCollection.add("ADD");
		
		// verify
		assertThat(originalCollection).isEqualTo(sut);
	}
	
	@Test
	public void givenCollectionIsLocked__whenAddElement__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { return sut.add("ADD"); });
		assertThat(originalCollection).isEqualTo(sut);
	}

	@Test
	public void givenCollectionIsUnlocked__whenAddElementAtIndex__thenOperationShouldBeSuccessful() {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
	
		// execute
		sut.add(3, "<INSERTED>");
		originalCollection.add(3, "<INSERTED>");
		
		// verify
		assertThat(originalCollection).isEqualTo(sut);
	}
	
	@Test
	public void givenCollectionIsLocked__whenAddElementAtIndex__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { sut.add(3, "<INSERTED>"); return null; }); 
		assertThat(originalCollection).isEqualTo(sut);
	}

	@Test
	public void givenCollectionIsUnlocked__whenRemoveElementAtIndex__thenOperationShouldBeSuccessful() {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
	
		// execute
		assertThat(sut.remove(3)).isEqualTo("3");
		originalCollection.remove(3);
		
		// verify
		assertThat(originalCollection).isEqualTo(sut);
	}

	@Test
	public void givenCollectionIsLocked__whenRemoveElementAtIndex__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { return sut.remove(3); }); 
		assertThat(originalCollection).isEqualTo(sut);
	}

	@Test
	public void givenCollectionIsUnlocked__whenRemoveElementAsObject__thenOperationShouldBeSuccessful() {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
	
		// execute
		assertThat(sut.remove("3")).isTrue();
		originalCollection.remove("3");
		
		// verify
		assertThat(originalCollection).isEqualTo(sut);
	}	

	@Test
	public void givenCollectionIsLocked__whenRemoveElementAsObject__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { return sut.remove("3"); }); 
		assertThat(originalCollection).isEqualTo(sut);
	}
	
	@Test
	public void givenCollectionIsUnlocked__whenClear__thenOperationShouldBeSuccessful() {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
	
		// execute
		sut.clear();
		
		// verify
		assertThat(sut).isEmpty();
	}

	@Test
	public void givenCollectionIsLocked__whenClear__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { sut.clear(); return null; }); 
		assertThat(originalCollection).isEqualTo(sut);
	}

	@Test
	public void givenCollectionIsUnlocked__whenAddAll__thenOperationShouldBeSuccessful() {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
	
		// execute
		assertThat(sut.addAll(originalCollection)).isTrue();
		originalCollection.addAll(originalCollection);
		
		// verify
		assertThat(originalCollection).isEqualTo(sut);
	}
	
	@Test
	public void givenCollectionIsLocked__whenAddAll__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { return sut.addAll(originalCollection); }); 
		assertThat(originalCollection).isEqualTo(sut);
	}

	@Test
	public void givenCollectionIsUnlocked__whenAddAllAtIndex__thenOperationShouldBeSuccessful() {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
	
		// execute
		assertThat(sut.addAll(1, originalCollection)).isTrue();
		originalCollection.addAll(1, originalCollection);
		
		// verify
		assertThat(originalCollection).isEqualTo(sut);
	}
	
	@Test
	public void givenCollectionIsLocked__whenAddAllAtIndex__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { return sut.addAll(1, originalCollection); }); 
		assertThat(originalCollection).isEqualTo(sut);
	}

	@Test
	public void givenCollectionIsUnlocked__whenRemoveRange__thenOperationShouldBeSuccessful() {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
	
		// execute
		sut.removeRange(2, 4);
		
		// verify
		assertThat(sut).containsExactly("0", "1", "4");
	}
	
	@Test
	public void givenCollectionIsLocked__whenRemoveRange__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { sut.removeRange(2, 4); return null; }); 
		assertThat(originalCollection).isEqualTo(sut);
	}

	@Test
	public void givenCollectionIsUnlocked__whenRemoveAll__thenOperationShouldBeSuccessful() {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		List<Object> toRemove = Arrays.asList("0", "4");
		
		// execute
		assertThat(sut.removeAll(toRemove)).isTrue();
		originalCollection.removeAll(toRemove);
		
		// verify
		assertThat(sut).isEqualTo(originalCollection);
	}
	
	@Test
	public void givenCollectionIsLocked__whenRemoveAll__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		final List<Object> toRemove = Arrays.asList("0", "4");
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { return sut.removeAll(toRemove); }); 
		assertThat(originalCollection).isEqualTo(sut);
	}

	@Test
	public void givenCollectionIsUnlocked__whenRetainAll__thenOperationShouldBeSuccessful() {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		List<Object> toRetain = Arrays.asList("0", "4");
		
		// execute
		assertThat(sut.retainAll(toRetain)).isTrue();
		
		// verify
		assertThat(sut).isEqualTo(toRetain);
	}
	
	@Test
	public void givenCollectionIsLocked__whenRetainAll__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		final List<Object> toRetain = Arrays.asList("0", "4");
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { return sut.retainAll(toRetain); }); 
		assertThat(originalCollection).isEqualTo(sut);
	}

	@Test
	public void givenCollectionIsUnlocked__whenRemoveIf__thenOperationShouldBeSuccessful() {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		
		// execute
		assertThat(sut.removeIf(e -> e.equals("2"))).isTrue();
		originalCollection.removeIf(e -> e.equals("2"));
		
		// verify
		assertThat(sut).isEqualTo(originalCollection);
	}

	@Test
	public void givenCollectionIsLocked__whenRemoveIf__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { return sut.removeIf(e -> e.equals("2")); }); 
		assertThat(originalCollection).isEqualTo(sut);
	}

	@Test
	public void givenCollectionIsUnlocked__whenReplaceAll__thenOperationShouldBeSuccessful() {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		
		// execute
		sut.replaceAll(e -> e.equals("2"));
		originalCollection.replaceAll(e -> e.equals("2"));
		
		// verify
		assertThat(sut).isEqualTo(originalCollection);
	}

	@Test
	public void givenCollectionIsLocked__whenReplaceAll__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { sut.replaceAll(e -> e.equals("2")); return null;}); 
		assertThat(originalCollection).isEqualTo(sut);
	}

	@Test
	public void givenCollectionIsUnlocked__whenSort__thenOperationShouldBeSuccessful() {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		
		// execute
		sut.sort(Collections.reverseOrder());
		originalCollection.sort(Collections.reverseOrder());
		
		// verify
		assertThat(originalCollection).isEqualTo(sut);
	}

	@Test
	public void givenCollectionIsLocked__whenSort__thenOperationShouldFailAndCollectionShouldStayUntouched() throws Exception {
		// prepare
		List<Object> originalCollection = IntStream.range(0, 5).mapToObj(Integer::toString).collect(Collectors.toList());
		originalCollection.forEach(e -> { sut.add(e); });
		sut.immutable();
		
		// execute & verify
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { sut.sort(Collections.reverseOrder()); return null; }); 
		assertThat(originalCollection).isEqualTo(sut);
	}
	
	@Before
	public void setUp() {
		this.sut = new ArrayListLockableCollection<Object>();
	}
	
	private void assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(final Callable<?> callable) throws Exception {
		UnsupportedOperationException error = null;
		try {
			callable.call();
		} catch (UnsupportedOperationException e) {
			error = e;
		} 
		
		assertThat(error).isNotNull();
		assertThat(error).hasMessage("Operation can't be performed given colleciton is locked for modification.");
	}
}
