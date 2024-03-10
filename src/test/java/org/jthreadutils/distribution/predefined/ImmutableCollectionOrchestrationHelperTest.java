package org.jthreadutils.distribution.predefined;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import static org.assertj.core.api.Assertions.assertThat;

public class ImmutableCollectionOrchestrationHelperTest {

	private ImmutableCollectionOrchestrationHelper<String> sut;

	@Test
	public void givenOnlyThreeGroupKeys_withTwoGroupsWithSameNumberOfElements__whenCreateListWithGroupsSortedBasedOnBiggestNumberOfElements__thenThreeKeysShouldBeReturn()
			throws Exception {
		// prepare
		final String groupOneKey = "GROUP-1";
		final String groupTwoKey = "GROUP-2";
		final String groupThreeKey = "GROUP-3";

		final Multimap<String, Long> groupKeysToPositionInList = ArrayListMultimap.create();
		LongStream.range(1L, 15L).forEachOrdered(e -> groupKeysToPositionInList.put(groupOneKey, e));
		LongStream.range(15L, 30L).forEachOrdered(e -> groupKeysToPositionInList.put(groupTwoKey, e));
		LongStream.range(18L, 40L).forEachOrdered(e ->  groupKeysToPositionInList.put(groupThreeKey, e));
		
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

		final Multimap<String, Long> groupKeysToPositionInList = ArrayListMultimap.create();
		LongStream.range(1L, 15L).forEachOrdered(e -> groupKeysToPositionInList.put(groupOneKey, e));
		LongStream.range(15L, 18L).forEachOrdered(e -> groupKeysToPositionInList.put(groupTwoKey, e));
		LongStream.range(18L, 40L).forEachOrdered(e ->  groupKeysToPositionInList.put(groupThreeKey, e));
		
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

		final Multimap<String, Long> groupKeysToPositionInList = ArrayListMultimap.create();
		LongStream.range(1L, 4L).forEachOrdered(e -> groupKeysToPositionInList.put(groupOneKey, e));
		LongStream.range(4L, 7L).forEachOrdered(e -> groupKeysToPositionInList.put(groupTwoKey, e));
		LongStream.range(8L, 11L).forEachOrdered(e ->  groupKeysToPositionInList.put(groupThreeKey, e));
		
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

		final Multimap<String, Long> groupKeysToPositionInList = ArrayListMultimap.create();
		groupKeysToPositionInList.put(groupOneKey, 1L);
		groupKeysToPositionInList.put(groupTwoKey, 2L);
		groupKeysToPositionInList.put(groupThreeKey, 3L);

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
		final Multimap<String, Long> groupKeysToPositionInList = ArrayListMultimap.create();
		groupKeysToPositionInList.put(groupOneKey, 10L);

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
		final Multimap<String, Long> groupKeysToPositionInList = ArrayListMultimap.create();

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
