package org.jthreadutils.distribution.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionUtilsTest {

	@Test
	public void givenBothCollectionsAreSameContent_andDifferentTypes__whenAreEqualByContentCalling__thenShouldReturnTrue() {
		// prepare
		final Collection<String> theFirst = new ArrayList<>(Arrays.asList("1", "2", "3"));
		final Collection<String> theSecond = new HashSet<>(Arrays.asList("1", "2", "3"));
		
		// execute & verify
		assertThat(CollectionUtils.areEqualByContent(theFirst, theSecond)).isTrue();
	}
	
	@Test
	public void givenBothCollectionsAreSameContent__whenAreEqualByContentCalling__thenShouldReturnTrue() {
		// prepare
		final Collection<String> theFirst = Arrays.asList("1", "2", "3");
		final Collection<String> theSecond = Arrays.asList("1", "2", "3");
		
		// execute & verify
		assertThat(CollectionUtils.areEqualByContent(theFirst, theSecond)).isTrue();
	}
	
	@Test
	public void givenBothCollectionsAreDifferentContentButSameSize_andDifferentTypes__whenAreEqualByContentCalling__thenShouldReturnFalse() {
		// prepare
		final Collection<String> theFirst = new ArrayList<>(Arrays.asList("1", "2", "3"));
		final Collection<String> theSecond = new HashSet<>(Arrays.asList("1", "2", "10"));
		
		// execute & verify
		assertThat(CollectionUtils.areEqualByContent(theFirst, theSecond)).isFalse();
	}
	
	@Test
	public void givenBothCollectionsAreDifferentContentButSameSize__whenAreEqualByContentCalling__thenShouldReturnFalse() {
		// prepare
		final Collection<String> theFirst = Arrays.asList("1", "2", "3");
		final Collection<String> theSecond = Arrays.asList("1", "2", "10");
		
		// execute & verify
		assertThat(CollectionUtils.areEqualByContent(theFirst, theSecond)).isFalse();
	}
	
	@Test
	public void givenBothCollectionsAreDifferentSize_andDifferentTypes__whenAreEqualByContentCalling__thenShouldReturnFalse() {
		// prepare
		final Collection<String> theFirst = new ArrayList<>(Arrays.asList("1", "2", "3"));
		final Collection<String> theSecond = new HashSet<>(Arrays.asList("1", "2"));
		
		// execute & verify
		assertThat(CollectionUtils.areEqualByContent(theFirst, theSecond)).isFalse();
	}
	
	@Test
	public void givenBothCollectionsAreDifferentSize__whenAreEqualByContentCalling__thenShouldReturnFalse() {
		// prepare
		final Collection<String> theFirst = Arrays.asList("1", "2", "3");
		final Collection<String> theSecond = Arrays.asList("1", "2");
		
		// execute & verify
		assertThat(CollectionUtils.areEqualByContent(theFirst, theSecond)).isFalse();
	}
	
	@Test
	public void givenBothCollectionsAreEmpty_andDifferentTypes__whenAreEqualByContentCalling__thenShouldReturnTrue() {
		// prepare
		final Collection<String> theFirst = new ArrayList<>();
		final Collection<String> theSecond = new HashSet<>();
		
		// execute & verify
		assertThat(CollectionUtils.areEqualByContent(theFirst, theSecond)).isTrue();
	}
	
	@Test
	public void givenBothCollectionsAreEmpty__whenAreEqualByContentCalling__thenShouldReturnTrue() {
		// prepare
		final Collection<String> theFirst = Arrays.asList();
		final Collection<String> theSecond = Arrays.asList();
		
		// execute & verify
		assertThat(CollectionUtils.areEqualByContent(theFirst, theSecond)).isTrue();
	}
	
	@Test
	public void givenNonEmptyList_andAllIndexesCorrect__whenGetForIndexesBasedOnList__thenCollectionWithExpectedDataShouldBeReturn() {
		// prepare
		final Collection<String> elements = Arrays.asList("A", "B", "C", "D", "E");

		// execute
		final Collection<String> foundElements = CollectionUtils.getElementsByIndexes(elements, Arrays.asList(2, 4, 0));

		// verify
		assertThat(foundElements).containsExactly("A", "C", "E");
	}

	@Test
	public void givenNonEmptyList_andAllIndexesCorrect__whenGetForIndexesBasedOnArray__thenCollectionWithExpectedDataShouldBeReturn() {
		// prepare
		final Collection<String> elements = Arrays.asList("A", "B", "C", "D", "E");

		// execute
		final Collection<String> foundElements = CollectionUtils.getElementsByIndexes(elements, 2, 4, 0);

		// verify
		assertThat(foundElements).containsExactly("A", "C", "E");
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void givenNonEmptyList_andOneIndexIsOutOfBounds__whenGetForIndexesBasedOnList__thenIndexOutOfBoundsExceptionShouldBeThrown() {
		// prepare
		final Collection<String> elements = Arrays.asList("A", "B", "C");

		// execute
		CollectionUtils.getElementsByIndexes(elements, Arrays.asList(0, 1, 3));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void givenNonEmptyList_andOneIndexIsOutOfBounds__whenGetForIndexesBasedOnArray__thenIndexOutOfBoundsExceptionShouldBeThrown() {
		// prepare
		final Collection<String> elements = Arrays.asList("A", "B", "C");

		// execute
		CollectionUtils.getElementsByIndexes(elements, 0, 1, 3);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void givenNonEmptyList_andIndexWithNegative__whenGetForIndexesBasedOnList__thenIndexOutOfBoundsExceptionShouldBeThrown() {
		// prepare
		final Collection<String> elements = Arrays.asList("A", "B", "C");

		// execute
		CollectionUtils.getElementsByIndexes(elements, Arrays.asList(-1));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void givenNonEmptyList_andIndexWithNegative__whenGetForIndexesBasedOnArray__thenIndexOutOfBoundsExceptionShouldBeThrown() {
		// prepare
		final Collection<String> elements = Arrays.asList("A", "B", "C");

		// execute
		CollectionUtils.getElementsByIndexes(elements, -1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void givenEmptyList_andSomeIndexes__whenGetForIndexesBasedOnList__thenIndexOutOfBoundsExceptionShouldBeThrown() {
		// prepare
		final Collection<String> elements = Arrays.asList();

		// execute
		CollectionUtils.getElementsByIndexes(elements, Arrays.asList(0));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void givenEmptyList_andSomeIndexes__whenGetForIndexesBasedOnArray__thenIndexOutOfBoundsExceptionShouldBeThrown() {
		// prepare
		final Collection<String> elements = Arrays.asList();

		// execute
		CollectionUtils.getElementsByIndexes(elements, 0);
	}

	@Test
	public void givenEmptyList_andEmptyIndexes__whenGetForIndexesBasedOnList_thenEmptyListShouldBeReturn() {
		// prepare
		final Collection<String> elements = Arrays.asList();

		// execute
		final Collection<String> foundElements = CollectionUtils.getElementsByIndexes(elements, Arrays.asList());

		// verify
		assertThat(foundElements).isEmpty();
	}

	@Test
	public void givenEmptyList_andEmptyIndexes__whenGetForIndexesBasedOnArray__thenEmptyListShouldBeReturn() {
		// prepare
		final Collection<String> elements = Arrays.asList();

		// execute
		final Collection<String> foundElements = CollectionUtils.getElementsByIndexes(elements);

		// verify
		assertThat(foundElements).isEmpty();
	}
}
