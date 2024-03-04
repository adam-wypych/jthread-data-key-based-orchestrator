package org.jthreadutils.distribution.collection.immutable.impl;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
		sut.set(3, "<REPLACED>");
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
		assertThatUnsupportedOperationExceptionWillBeThrown_whenCollectionIsLocked(() -> { sut.set(3, "<REPLACED>"); return null; }); 
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
