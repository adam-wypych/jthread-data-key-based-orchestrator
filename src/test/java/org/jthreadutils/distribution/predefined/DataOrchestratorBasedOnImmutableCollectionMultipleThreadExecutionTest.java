package org.jthreadutils.distribution.predefined;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jthreadutils.distribution.DataGroupIdExtractor;
import org.jthreadutils.distribution.collection.CollectionUtils;
import org.jthreadutils.distribution.collection.utils.MultithreadCoordinationTestHarness;
import org.junit.Test;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class DataOrchestratorBasedOnImmutableCollectionMultipleThreadExecutionTest {

	@Test(timeout = 60000)
	public void givenFollowingSizeOfGroups_asOneTwoThree_andThreeThreads__whenAllThreadsStartedSameTime_andTryToConsumeFiveElementsEach__thenThreadsShouldUseOnlyAssignedGroup_andOneThreadShouldNotGetAnyData()
			throws Exception {
		// prepare
		final List<Integer> elements = IntStream.range(0, 21).boxed().collect(Collectors.toList());
		DataGroupIdExtractor<Integer, String> extractor = (e) -> { if (e < 18) { return "GROUP-" + ((e % 3) + 1); } else if (e == 18 || e == 19) { return "GROUP-1"; } else { return "GROUP-2"; }};
		
		DataOrchestratorBasedOnImmutableCollection<Integer, String> dataOrchestrator = new DataOrchestratorBasedOnImmutableCollection<>(extractor);
		ImmutableCollectionOrchestrationPlan<Integer, String> orchPlan = dataOrchestrator.createPlan(elements);
		
		final Multimap<Thread, Integer> resultsPerThread = Multimaps.synchronizedMultimap(LinkedHashMultimap.create());
		final Runnable[] runnables = new Runnable[3];
		for (int i = 0; i < runnables.length; i++) {
			runnables[i] = () -> {
				Collection<Integer> data = new ArrayList<>(0);
				do {
					data = dataOrchestrator.nextPortionOfData(orchPlan, 9);
					resultsPerThread.putAll(Thread.currentThread(), data);
				} while(!data.isEmpty());				
			};
		}

		// execute
		final Multimap<Runnable, Throwable> errorsPerThread = new MultithreadCoordinationTestHarness()
				.allThreadsShouldStartOnSameTime(runnables);

		// verify
		assertThat(errorsPerThread.isEmpty()).isTrue();
		assertThat(resultsPerThread.keySet()).hasSize(2);
		
		List<Thread> resultKeys = new ArrayList<>(resultsPerThread.keySet());
		List<List<Integer>> possibleElementsToConsume = new ArrayList<>();
		// group-1 and group-2
		possibleElementsToConsume.add(Arrays.asList(0, 3, 6, 9, 12, 15, 18, 19, 1, 4, 7, 10, 13, 16, 20));
		// group-3
		possibleElementsToConsume.add(Arrays.asList(2, 5, 8, 11, 14, 17));
		
		for (Thread th: resultKeys) {
			Collection<Integer> dataConsumed = resultsPerThread.get(th);
			int position = indexOfCollectionInsideComparingElements(possibleElementsToConsume, dataConsumed);
			assertThat(position).isGreaterThan(-1);
			possibleElementsToConsume.remove(position);
		}
	}

	private int indexOfCollectionInsideComparingElements(final List<List<Integer>> possibleElementsToConsume,
			final Collection<Integer> dataConsumed) {
		for (int index = 0; index < possibleElementsToConsume.size(); index++) {
			if (CollectionUtils.areEqualByContent(possibleElementsToConsume.get(index), dataConsumed)) {
				return index;
			}
		}
		
		return -1;
	}
}
