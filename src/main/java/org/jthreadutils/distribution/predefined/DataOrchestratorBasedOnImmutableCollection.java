package org.jthreadutils.distribution.predefined;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

import org.jthreadutils.distribution.DataGroupIdExtractor;
import org.jthreadutils.distribution.collection.immutable.LockableCollection;
import org.jthreadutils.distribution.predefined.ImmutableCollectionOrchestrationPlan.ImmutableCollectionOrchestrationPlanBuilder;

/**
 * This orchestrator based on <i>immutable collection</i>, which internally
 * group data based on their key and sort from biggest group in terms of size
 * into smallest. This should provide equal distribution of data between threads
 * and give impression of fully parallel processing in case same condition for
 * all threads.
 * 
 * In case no more data to process orchestrator return empty list. The
 * assumption is that data handling will be done by processing thread also in
 * terms of retry mechanism or error handling.
 * 
 * The swap/disinherit of thread for processing of given group key is not
 * possible by this implementation.
 * 
 * @param <T> type of data inside collection
 * @param <O> type of group id object
 * 
 * @author adam-wypych
 * @since 1.0.0
 * @version %I%, %G%
 */
@ThreadSafe
public class DataOrchestratorBasedOnImmutableCollection<T, O> {

	private final DataGroupIdExtractor<T, O> groupIdExtractor;

	/**
	 * @param groupIdExtractor extractor used for get <code>groupId</code> for given
	 *                         dataset
	 * @since 1.0.0
	 */
	public DataOrchestratorBasedOnImmutableCollection(final DataGroupIdExtractor<T, O> groupIdExtractor) {
		this.groupIdExtractor = groupIdExtractor;
	}

	/**
	 * This method has side-effect of internally call
	 * {@link LockableCollection#immutable()} for prohibit collection modification
	 * prior to processing will be performed and plan for split data generated. This
	 * method should be used mostly for 3rd party libraries on which potential
	 * modification of data could happen during processing. In such condition user
	 * should the first create {@link LockableCollection} object, insert data and by
	 * his own call {@link LockableCollection#immutable()}. The call for immutable
	 * method in this method should be redundant.
	 * 
	 * @see #createPlan(Collection)
	 * @param data for distribution
	 * @return {@link ImmutableCollectionOrchestrationPlan} for given collection of
	 *         data
	 * @since 1.0.0
	 */
	public ImmutableCollectionOrchestrationPlan<T, O> createPlan(final LockableCollection<T> data) {
		data.immutable();
		return createPlan((Collection<T>) data);
	}

	/**
	 * This method creates plan of data distribution between threads using algorithm
	 * described in class JavaDoc.
	 * 
	 * @param data for distribution
	 * @return {@link ImmutableCollectionOrchestrationPlan} for given collection of
	 *         data
	 */
	public ImmutableCollectionOrchestrationPlan<T, O> createPlan(final Collection<T> data) {
		final ImmutableCollectionOrchestrationPlanBuilder<T, O> plannerBuilder = ImmutableCollectionOrchestrationPlan
				.builder(data);
		final Iterator<T> iter = data.iterator();
		for (int index = 0; iter.hasNext(); index++) {
			plannerBuilder.putElementGroupIdAssignment(index, groupIdExtractor.extractGroupId(iter.next()));
		}

		return plannerBuilder.build();
	}

	/**
	 * This method is <b>thread safe</b> in terms lock is being made on
	 * <code>orchestrationPlan</code> object. Its main responsibility is to provide
	 * next portion of data based on what data are being consumed by which thread.
	 * 
	 * @param orchestrationPlan which internally contains information about data for
	 *                          processing
	 * @param batchSize         number of elements to provide should be greater than
	 *                          0
	 * @return available elements for given orchestration plan, it might be less
	 *         than data requested in case data are being consumed by other threads
	 * 
	 * @since 1.0.0
	 */
	public Collection<T> nextPortionOfData(final ImmutableCollectionOrchestrationPlan<T, O> orchestrationPlan,
			final int batchSize) {
		if (batchSize <= 0) {
			throw new IllegalArgumentException(
					"Batch size parameter should be greater than 0, currently it is " + batchSize);
		}

		synchronized (orchestrationPlan) {
			return orchestrationPlan.poolNextBatchOfData(Thread.currentThread(), batchSize);
		}
	}
}
