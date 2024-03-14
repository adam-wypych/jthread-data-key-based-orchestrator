package org.jthreadutils.distribution.predefined;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.jthreadutils.distribution.collection.CollectionUtils;

import com.google.common.collect.ArrayListMultimap;

/**
 * Please use this class only within
 * {@link DataOrchestratorBasedOnImmutableCollection}, which is thread-safe.
 * 
 * @param <T> type of data inside collection
 * @param <O> type of group id object
 * 
 * @author adam-wypych
 * @since 1.0.0
 * @version %I%, %G%
 */
@NotThreadSafe
public class ImmutableCollectionOrchestrationPlan<T, O> {
	private final Collection<T> originalImmutableCollection;
	private final ArrayListMultimap<O, Integer> mappingBetweenGroupKeyAndElementsPositionInsideCollection;

	private final Map<O, Integer> currentProcessingPositionWithinGroupKeyDataSet = new HashMap<>();
	private final ArrayListMultimap<Thread, O> assignmentOfDataGroupIdToThread = ArrayListMultimap.create();
	private final List<O> freeGroupsToProcessSortedBasedOnBiggestNumberOfElements;

	/**
	 * Please use builder {@link ImmutableCollectionOrchestrationPlanBuilder}
	 * available also via {@link #builder(Collection)}, in order to create planner.
	 * 
	 * @param originalImmutableCollection                               data
	 *                                                                  elements for
	 *                                                                  which
	 *                                                                  planner is
	 *                                                                  created
	 * @param mappingBetweenGroupKeyAndElementsPositionInsideCollection it is map
	 *                                                                  between
	 *                                                                  group key
	 *                                                                  and position
	 *                                                                  of elements
	 *                                                                  inside data
	 *                                                                  collection
	 * @param freeGroupsToProcessSortedBasedOnBiggestNumberOfElements   list of
	 *                                                                  available
	 *                                                                  keys sorted
	 *                                                                  based on
	 *                                                                  number of
	 *                                                                  elements
	 *                                                                  inside data
	 *                                                                  collection
	 */
	private ImmutableCollectionOrchestrationPlan(final Collection<T> originalImmutableCollection,
			final ArrayListMultimap<O, Integer> mappingBetweenGroupKeyAndElementsPositionInsideCollection,
			final List<O> freeGroupsToProcessSortedBasedOnBiggestNumberOfElements) {
		this.originalImmutableCollection = originalImmutableCollection;
		this.mappingBetweenGroupKeyAndElementsPositionInsideCollection = mappingBetweenGroupKeyAndElementsPositionInsideCollection;
		this.freeGroupsToProcessSortedBasedOnBiggestNumberOfElements = freeGroupsToProcessSortedBasedOnBiggestNumberOfElements;
	}

	/**
	 * Basic plan builder.
	 * 
	 * @param <T>                         type of data inside collection
	 * @param <O>                         type of group id object
	 * @param originalImmutableCollection data elements to process
	 * @return
	 */
	public static <T, O> ImmutableCollectionOrchestrationPlanBuilder<T, O> builder(
			final Collection<T> originalImmutableCollection) {
		return new ImmutableCollectionOrchestrationPlanBuilder<T, O>(originalImmutableCollection);
	}

	protected Collection<T> poolNextBatchOfData(final Thread currentThread, final int batchSize) {
		final List<T> nextBatchOfData = new ArrayList<>();

		if (assignmentOfDataGroupIdToThread.containsKey(currentThread)) {
			removeFullyProcessedGroupIdsFor(currentThread);
			nextBatchOfData.addAll(loadDataFromStillNotFullyProcessedGroups(currentThread, batchSize));
		}

		final int dataRemainingToCollect = batchSize - nextBatchOfData.size();
		if (dataRemainingToCollect > 0) {
			if (!freeGroupsToProcessSortedBasedOnBiggestNumberOfElements.isEmpty()) {
				nextBatchOfData.addAll(loadGroupsDataNotProcessedYetFor(currentThread, dataRemainingToCollect));
			}
		}

		return nextBatchOfData;
	}

	private void removeFullyProcessedGroupIdsFor(final Thread currentThread) {
		final List<O> groupsFullyProcessed = new ArrayList<>();
		for (O groupId : assignmentOfDataGroupIdToThread.get(currentThread)) {
			if (currentProcessingPositionWithinGroupKeyDataSet.get(groupId)
					.equals(getLastElementPositionInCollectionForGivenGroupId(groupId))) {
				groupsFullyProcessed.add(groupId);
			}
		}

		groupsFullyProcessed.forEach(e -> {
			currentProcessingPositionWithinGroupKeyDataSet.remove(e);
			assignmentOfDataGroupIdToThread.remove(currentThread, e);
		});
	}

	private Integer getLastElementPositionInCollectionForGivenGroupId(final O groupId) {
		List<Integer> values = mappingBetweenGroupKeyAndElementsPositionInsideCollection.get(groupId);
		return values.isEmpty() ? null : values.get(values.size() - 1);
	}

	private List<T> loadDataFromStillNotFullyProcessedGroups(final Thread currentThread, final int batchSize) {
		final List<T> dataRemainingForGroupsAssignedForGivenThread = new ArrayList<>();

		final List<O> dataGroupIdsAssignedToThread = assignmentOfDataGroupIdToThread.get(currentThread);
		for (int groupIndex = 0; dataRemainingForGroupsAssignedForGivenThread.size() < batchSize
				&& groupIndex < dataGroupIdsAssignedToThread.size(); groupIndex++) {
			final O groupId = dataGroupIdsAssignedToThread.get(groupIndex);
			dataRemainingForGroupsAssignedForGivenThread.addAll(loadRemainingDataFromGivenGroupForThread(currentThread,
					batchSize - dataRemainingForGroupsAssignedForGivenThread.size(), groupId));
		}

		return dataRemainingForGroupsAssignedForGivenThread;
	}

	private List<T> loadGroupsDataNotProcessedYetFor(final Thread currentThread, final int freeSlotInCurrentBatch) {
		final List<T> dataToProcess = new ArrayList<>();

		while (!freeGroupsToProcessSortedBasedOnBiggestNumberOfElements.isEmpty()
				&& dataToProcess.size() < freeSlotInCurrentBatch) {
			final O groupId = freeGroupsToProcessSortedBasedOnBiggestNumberOfElements.remove(0);
			dataToProcess.addAll(loadRemainingDataFromGivenGroupForThread(currentThread,
					freeSlotInCurrentBatch - dataToProcess.size(), groupId));
			assignmentOfDataGroupIdToThread.put(currentThread, groupId);
		}

		return dataToProcess;
	}

	private List<T> loadRemainingDataFromGivenGroupForThread(final Thread currentThread, final int remainingSize,
			final O groupId) {
		final List<Integer> groupElementPositionToProcessWithinOriginalCollection = findDataIndexesNotProcessedForGivenGroup(
				groupId, remainingSize);
		final List<T> dataToProcessForGivenGroup = CollectionUtils
				.getElementsByIndexes(originalImmutableCollection, groupElementPositionToProcessWithinOriginalCollection);
		if (!groupElementPositionToProcessWithinOriginalCollection.isEmpty()) {
			currentProcessingPositionWithinGroupKeyDataSet.put(groupId, groupElementPositionToProcessWithinOriginalCollection
					.get(groupElementPositionToProcessWithinOriginalCollection.size() - 1));
		}

		return dataToProcessForGivenGroup;
	}

	private List<Integer> findDataIndexesNotProcessedForGivenGroup(final O groupId, final long dataRemainingToCollect) {
		final List<Integer> elementPositionInsideOriginalCollection = new ArrayList<>();

		final Integer currentIndexOfElementInOriginalCollection = currentProcessingPositionWithinGroupKeyDataSet
				.getOrDefault(groupId, -1);
		final List<Integer> positionsOfElementWithinOriginalCollectionForGroup = mappingBetweenGroupKeyAndElementsPositionInsideCollection
				.get(groupId);

		for (int index = positionsOfElementWithinOriginalCollectionForGroup
				.lastIndexOf(currentIndexOfElementInOriginalCollection)
				+ 1; index < positionsOfElementWithinOriginalCollectionForGroup.size()
						&& elementPositionInsideOriginalCollection.size() < dataRemainingToCollect; index++) {
			elementPositionInsideOriginalCollection.add(positionsOfElementWithinOriginalCollectionForGroup.get(index));
		}

		return elementPositionInsideOriginalCollection;
	}

	protected static class ImmutableCollectionOrchestrationPlanBuilder<T, O> {
		private final Collection<T> originalImmutableCollection;
		private final ArrayListMultimap<O, Integer> mappingBetweenGroupKeyAndElementsPositionInsideCollection = ArrayListMultimap
				.create();
		private final ImmutableCollectionOrchestrationHelper<O> orchestrationHelper = new ImmutableCollectionOrchestrationHelper<>();

		private ImmutableCollectionOrchestrationPlanBuilder(final Collection<T> originalImmutableCollection) {
			this.originalImmutableCollection = originalImmutableCollection;
		}

		public ImmutableCollectionOrchestrationPlanBuilder<T, O> putElementGroupIdAssignment(final int elementIndex,
				final O groupId) {
			mappingBetweenGroupKeyAndElementsPositionInsideCollection.put(groupId, elementIndex);
			return this;
		}

		public ImmutableCollectionOrchestrationPlan<T, O> build() {
			return new ImmutableCollectionOrchestrationPlan<T, O>(originalImmutableCollection,
					mappingBetweenGroupKeyAndElementsPositionInsideCollection,
					orchestrationHelper.createListWithGroupsSortedBasedOnBiggestNumberOfElements(
							mappingBetweenGroupKeyAndElementsPositionInsideCollection));
		}
	}
}
