package org.jthreadutils.distribution.predefined;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * This is helper class used by {@link ImmutableCollectionOrchestrationPlan} in
 * order to perform operations not strictly related into executing of data
 * distribution plan.
 * 
 * @param <O> type of group id object
 * 
 * @author adam-wypych
 * @since 1.0.0
 * @version %I%, %G%
 */
class ImmutableCollectionOrchestrationHelper<O> {

	/**
	 * This method based on mappings between group keys and elements in collection,
	 * creates list where group keys are placed based on their occurrence inside
	 * data collection.
	 * 
	 * @param mappingBetweenGroupKeyAndElementsPositionInsideCollection mapping
	 *                                                                  between
	 *                                                                  group key
	 *                                                                  and elements
	 *                                                                  inside
	 *                                                                  collection
	 * @return list of group keys sorted based on their count in data collection
	 * 
	 * @since 1.0.0
	 */
	public List<O> createListWithGroupsSortedBasedOnBiggestNumberOfElements(
			final Multimap<O, Integer> mappingBetweenGroupKeyAndElementsPositionInsideCollection) {
		final List<O> freeGroupsToProcessSortedBasedOnBiggestNumberOfElements = new ArrayList<>();

		final TreeMultimap<Integer, O> numberOfElementsToGivenGroupKey = collectNumberOfElementsToGivenGroupKeySortedFromBiggestCount(
				mappingBetweenGroupKeyAndElementsPositionInsideCollection);
		for (Integer sizeOfKeys : numberOfElementsToGivenGroupKey.keySet()) {
			freeGroupsToProcessSortedBasedOnBiggestNumberOfElements
					.addAll(numberOfElementsToGivenGroupKey.get(sizeOfKeys));
		}

		return freeGroupsToProcessSortedBasedOnBiggestNumberOfElements;
	}

	private TreeMultimap<Integer, O> collectNumberOfElementsToGivenGroupKeySortedFromBiggestCount(
			final Multimap<O, Integer> mappingBetweenGroupKeyAndElementsPositionInsideCollection) {
		final Comparator<O> ignoreValueOrderComparator = (a, b) -> {
			return 1;
		};
		final TreeMultimap<Integer, O> numberOfElementsToGivenGroupKey = TreeMultimap.create(Comparator.reverseOrder(),
				ignoreValueOrderComparator);
		for (O groupKey : mappingBetweenGroupKeyAndElementsPositionInsideCollection.keySet()) {
			if (!mappingBetweenGroupKeyAndElementsPositionInsideCollection.get(groupKey).isEmpty()) {
				numberOfElementsToGivenGroupKey
						.put(mappingBetweenGroupKeyAndElementsPositionInsideCollection.get(groupKey).size(), groupKey);
			}
		}

		return numberOfElementsToGivenGroupKey;
	}
}
