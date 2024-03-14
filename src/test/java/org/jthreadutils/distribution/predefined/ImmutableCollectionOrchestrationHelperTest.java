package org.jthreadutils.distribution.predefined;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class ImmutableCollectionOrchestrationHelperTest {

	private ImmutableCollectionOrchestrationHelper<String> sut;

	@Test
	public void givenOnlyThreeGroupKeys_withTwoGroupsWithSameNumberOfElements__whenCreateListWithGroupsSortedBasedOnBiggestNumberOfElements__thenThreeKeysShouldBeReturn()
			throws Exception {
		// prepare
		final String groupOneKey = "GROUP-1";
		final String groupTwoKey = "GROUP-2";
		final String groupThreeKey = "GROUP-3";

		final Multimap<String, Integer> groupKeysToPositionInList = ArrayListMultimap.create();
		IntStream.range(1, 15).forEachOrdered(e -> groupKeysToPositionInList.put(groupOneKey, e));
		IntStream.range(15, 30).forEachOrdered(e -> groupKeysToPositionInList.put(groupTwoKey, e));
		IntStream.range(18, 40).forEachOrdered(e ->  groupKeysToPositionInList.put(groupThreeKey, e));
		
		// execute
		final List<String> groupKeysFromHighestToLowestOccurance = sut
				.createListWithGroupsSortedBasedOnBiggestNumberOfElements(groupKeysToPositionInList);

		// verify
		assertThat(groupKeysFromHighestToLowestOccurance).hasSize(3);
		assertThat(groupKeysFromHighestToLowestOccurance).element(0).isEqualTo(groupThreeKey);
		List<String> sameSizeGroupKeys = new ArrayList<>(Arrays.asList(groupOneKey, groupTwoKey));
		assertThat(groupKeysFromHighestToLowestOccurance).element(1).isIn(sameSizeGroupKeys);
		sameSizeGroupKeys.remove(groupKeysFromHighestToLowestOccurance.get(1));
		assertThat(groupKeysFromHighestToLowestOccurance).element(2).isIn(sameSizeGroupKeys);
	}
	
	@Test
	public void givenOnlyThreeGroupKeys_withDifferentNumberOfElements__whenCreateListWithGroupsSortedBasedOnBiggestNumberOfElements__thenThreeKeysShouldBeReturn()
			throws Exception {
		// prepare
		final String groupOneKey = "GROUP-1";
		final String groupTwoKey = "GROUP-2";
		final String groupThreeKey = "GROUP-3";

		final Multimap<String, Integer> groupKeysToPositionInList = ArrayListMultimap.create();
		IntStream.range(1, 15).forEachOrdered(e -> groupKeysToPositionInList.put(groupOneKey, e));
		IntStream.range(15, 18).forEachOrdered(e -> groupKeysToPositionInList.put(groupTwoKey, e));
		IntStream.range(18, 40).forEachOrdered(e ->  groupKeysToPositionInList.put(groupThreeKey, e));
		
		// execute
		final List<String> groupKeysFromHighestToLowestOccurance = sut
				.createListWithGroupsSortedBasedOnBiggestNumberOfElements(groupKeysToPositionInList);

		// verify
		assertThat(groupKeysFromHighestToLowestOccurance).containsExactly(groupThreeKey, groupOneKey, groupTwoKey);
	}
	
	@Test
	public void givenOnlyThreeGroupKeys_withSameNumberOfElements_equalThree__whenCreateListWithGroupsSortedBasedOnBiggestNumberOfElements__thenThreeKeysShouldBeReturn()
			throws Exception {
		// prepare
		final String groupOneKey = "GROUP-1";
		final String groupTwoKey = "GROUP-2";
		final String groupThreeKey = "GROUP-3";

		final Multimap<String, Integer> groupKeysToPositionInList = ArrayListMultimap.create();
		IntStream.range(1, 4).forEachOrdered(e -> groupKeysToPositionInList.put(groupOneKey, e));
		IntStream.range(4, 7).forEachOrdered(e -> groupKeysToPositionInList.put(groupTwoKey, e));
		IntStream.range(8, 11).forEachOrdered(e ->  groupKeysToPositionInList.put(groupThreeKey, e));
		
		// execute
		final List<String> groupKeysFromHighestToLowestOccurance = sut
				.createListWithGroupsSortedBasedOnBiggestNumberOfElements(groupKeysToPositionInList);

		// verify
		assertThat(groupKeysFromHighestToLowestOccurance).containsExactlyInAnyOrder(groupOneKey, groupTwoKey,
				groupThreeKey);
	}
	
	@Test
	public void givenOnlyThreeGroupKeys_withSameNumberOfElements_equalOne__whenCreateListWithGroupsSortedBasedOnBiggestNumberOfElements__thenThreeKeysShouldBeReturn()
			throws Exception {
		// prepare
		final String groupOneKey = "GROUP-1";
		final String groupTwoKey = "GROUP-2";
		final String groupThreeKey = "GROUP-3";

		final Multimap<String, Integer> groupKeysToPositionInList = ArrayListMultimap.create();
		groupKeysToPositionInList.put(groupOneKey, 1);
		groupKeysToPositionInList.put(groupTwoKey, 2);
		groupKeysToPositionInList.put(groupThreeKey, 3);

		// execute
		final List<String> groupKeysFromHighestToLowestOccurance = sut
				.createListWithGroupsSortedBasedOnBiggestNumberOfElements(groupKeysToPositionInList);

		// verify
		assertThat(groupKeysFromHighestToLowestOccurance).containsExactlyInAnyOrder(groupOneKey, groupTwoKey,
				groupThreeKey);
	}

	@Test
	public void givenOnlyOneGroupKey__whenCreateListWithGroupsSortedBasedOnBiggestNumberOfElements__thenOnlyOneElementShouldBeReturn()
			throws Exception {
		// prepare
		final String groupOneKey = "GROUP-1";
		final Multimap<String, Integer> groupKeysToPositionInList = ArrayListMultimap.create();
		groupKeysToPositionInList.put(groupOneKey, 10);

		// execute
		final List<String> groupKeysFromHighestToLowestOccurance = sut
				.createListWithGroupsSortedBasedOnBiggestNumberOfElements(groupKeysToPositionInList);

		// verify
		assertThat(groupKeysFromHighestToLowestOccurance).containsExactly(groupOneKey);
	}

	@Test
	public void givenNoGroupKeys__whenCreateListWithGroupsSortedBasedOnBiggestNumberOfElements__thenEmptyListShouldBeReturn()
			throws Exception {
		// prepare
		final Multimap<String, Integer> groupKeysToPositionInList = ArrayListMultimap.create();

		// execute
		final List<String> groupKeysFromHighestToLowestOccurance = sut
				.createListWithGroupsSortedBasedOnBiggestNumberOfElements(groupKeysToPositionInList);

		// verify
		assertThat(groupKeysFromHighestToLowestOccurance).isEmpty();
	}

	@Before
	public void setUp() {
		this.sut = new ImmutableCollectionOrchestrationHelper<>();
	}
}
