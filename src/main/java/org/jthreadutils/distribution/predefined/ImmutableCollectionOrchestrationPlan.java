package org.jthreadutils.distribution.predefined;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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
	private final Multimap<O, Long> mappingBetweenGroupKeyAndElementsPositionInsideCollection;

	private final Map<O, Long> currentProcessingPositionWithinGroupKeyDataSet = new HashMap<>();
	private final Multimap<Thread, O> assignmentOfDataGroupIdToThread = ArrayListMultimap.create();
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
			final Multimap<O, Long> mappingBetweenGroupKeyAndElementsPositionInsideCollection,
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
		return null;
	}

	public static class ImmutableCollectionOrchestrationPlanBuilder<T, O> {
		private final Collection<T> originalImmutableCollection;
		private final Multimap<O, Long> mappingBetweenGroupKeyAndElementsPositionInsideCollection = ArrayListMultimap
				.create();
		private final ImmutableCollectionOrchestrationHelper<O> orchestrationHelper = new ImmutableCollectionOrchestrationHelper<>();

		private ImmutableCollectionOrchestrationPlanBuilder(final Collection<T> originalImmutableCollection) {
			this.originalImmutableCollection = originalImmutableCollection;
		}

		public ImmutableCollectionOrchestrationPlanBuilder<T, O> putElementGroupIdAssignment(final long elementIndex,
				final O groupId) {
			mappingBetweenGroupKeyAndElementsPositionInsideCollection.put(groupId, elementIndex);
			return this;
		}

		public ImmutableCollectionOrchestrationPlan<T, O> build() {
			return new ImmutableCollectionOrchestrationPlan<>(originalImmutableCollection,
					mappingBetweenGroupKeyAndElementsPositionInsideCollection,
					orchestrationHelper.createListWithGroupsSortedBasedOnBiggestNumberOfElements(
							mappingBetweenGroupKeyAndElementsPositionInsideCollection));
		}
	}
}
