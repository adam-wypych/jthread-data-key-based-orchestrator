package org.jthreadutils.distribution.predefined;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jthreadutils.distribution.predefined.ImmutableCollectionOrchestrationPlan.ImmutableCollectionOrchestrationPlanBuilder;
import org.junit.Test;

public class ImmutableCollectionOrchestrationPlanSingleThreadExecutionTest {

	@Test
	public void givenNoDataToProcessAtAll__whenPoolNextBatchOfData__thenEmptyCollectionShouldBeReturn() throws Exception {
		// prepare
		final List<String> elements = Arrays.asList();
		
		// execute
		ImmutableCollectionOrchestrationPlan<String, String> orchPlan = ImmutableCollectionOrchestrationPlan.<String,String>builder(elements).build();
		
		// verify
		assertThat(orchPlan.poolNextBatchOfData(Thread.currentThread(), 1)).isEmpty();
	}
	
	@Test
	public void givenOneGroupWithSingleElement__whenPoolNextBatchOfDataExecutedTripleTimes_withOneHundredSize__thenTheFirstRequestShouldReturnAllData_andNextShouldBeEmpty() throws Exception {
		// prepare
		final List<String> elements = Arrays.asList("1");
		
		// execute
		ImmutableCollectionOrchestrationPlan<String, String> orchPlan = ImmutableCollectionOrchestrationPlan.<String,String>builder(elements).putElementGroupIdAssignment(0, "GROUP-1").build();
		
		// verify
		Thread current = Thread.currentThread();
		assertThat(orchPlan.poolNextBatchOfData(current, 100)).containsExactly("1");
		assertThat(orchPlan.poolNextBatchOfData(current, 100)).isEmpty();
		assertThat(orchPlan.poolNextBatchOfData(current, 100)).isEmpty();
	}
	
	@Test
	public void givenThreeGroupWithSingleElement__whenPoolNextBatchOfDataExecutedTripleTimes_withOneHundredSize__thenTheFirstRequestShouldReturnAllData_andNextShouldBeEmpty() throws Exception {
		// prepare
		final List<String> elements = Arrays.asList("1", "2", "3");
		
		// execute
		ImmutableCollectionOrchestrationPlan<String, String> orchPlan = ImmutableCollectionOrchestrationPlan.<String,String>builder(elements).putElementGroupIdAssignment(0, "GROUP-1").putElementGroupIdAssignment(1, "GROUP-2").putElementGroupIdAssignment(2, "GROUP-3").build();
		
		// verify
		Thread current = Thread.currentThread();
		assertThat(orchPlan.poolNextBatchOfData(current, 100)).containsExactly("1", "2", "3");
		assertThat(orchPlan.poolNextBatchOfData(current, 100)).isEmpty();
		assertThat(orchPlan.poolNextBatchOfData(current, 100)).isEmpty();
	}	
	
	@Test
	public void givenThreeGroupWithThreeElementsEach__whenPoolNextBatchOfDataExecutedTripleTimes_withFiveElementInBatchSize__thenFirstAndSecondShouldReturnDataAvailable_andLastShouldBeEmpty() throws Exception {
		// prepare
		final List<String> elements = IntStream.range(1, 10).mapToObj(Integer::toString).collect(Collectors.toList());
		
		// execute
		ImmutableCollectionOrchestrationPlanBuilder<String, String> orchPlanBuilder = ImmutableCollectionOrchestrationPlan.<String,String>builder(elements);
		for (int i = 0; i < elements.size(); i++) {
			orchPlanBuilder.putElementGroupIdAssignment(i, "GROUP-" + (i % 3));
		}
		ImmutableCollectionOrchestrationPlan<String, String> orchPlan = orchPlanBuilder.build();
		
		// verify
		Thread current = Thread.currentThread();
		assertThat(orchPlan.poolNextBatchOfData(current, 5)).containsExactly("1", "4", "7", "2", "5");
		assertThat(orchPlan.poolNextBatchOfData(current, 5)).containsExactly("8", "3", "6", "9");
		assertThat(orchPlan.poolNextBatchOfData(current, 5)).isEmpty();
	}
	
	@Test
	public void givenThreeGroupWithThreeElementsEach__whenPoolNextBatchOfDataExecutedFourthTimes_withThreeElementsInBatchSize__thenFirstAndSecondAndThirdShouldReturnDataAvailable_andLastShouldBeEmpty() throws Exception {
		// prepare
		final List<String> elements = IntStream.range(1, 10).mapToObj(Integer::toString).collect(Collectors.toList());
		
		// execute
		ImmutableCollectionOrchestrationPlanBuilder<String, String> orchPlanBuilder = ImmutableCollectionOrchestrationPlan.<String,String>builder(elements);
		for (int i = 0; i < elements.size(); i++) {
			orchPlanBuilder.putElementGroupIdAssignment(i, "GROUP-" + (i % 3));
		}
		ImmutableCollectionOrchestrationPlan<String, String> orchPlan = orchPlanBuilder.build();
		
		// verify
		Thread current = Thread.currentThread();
		assertThat(orchPlan.poolNextBatchOfData(current, 3)).containsExactly("1", "4", "7");
		assertThat(orchPlan.poolNextBatchOfData(current, 3)).containsExactly("2", "5", "8");
		assertThat(orchPlan.poolNextBatchOfData(current, 3)).containsExactly("3", "6", "9");
		assertThat(orchPlan.poolNextBatchOfData(current, 3)).isEmpty();
	}
	
	@Test
	public void givenThreeGroupWithSixSevenEightElements__whenPoolNextBatchOfDataExecutedNineTimes_withTwoThreads_withFiveElementsInBatchSize__thenFirstUntilSixtCallShouldReturnDataAvailable_andLastShouldBeEmpty() throws Exception {
		// prepare
		final List<String> elements = IntStream.range(0, 21).mapToObj(Integer::toString).collect(Collectors.toList());
		
		// execute
		ImmutableCollectionOrchestrationPlanBuilder<String, String> orchPlanBuilder = ImmutableCollectionOrchestrationPlan.<String,String>builder(elements);
		for (int i = 0; i < 6 * 3; i++) {
			orchPlanBuilder.putElementGroupIdAssignment(i, "GROUP-" + ((i % 3)+1));
		}
		orchPlanBuilder.putElementGroupIdAssignment(18, "GROUP-1");
		orchPlanBuilder.putElementGroupIdAssignment(19, "GROUP-1");
		orchPlanBuilder.putElementGroupIdAssignment(20, "GROUP-2");
		ImmutableCollectionOrchestrationPlan<String, String> orchPlan = orchPlanBuilder.build();
		
		// verify
		Thread current = new Thread();
		Thread next = new Thread();
		
		// begin of group-1
		assertThat(orchPlan.poolNextBatchOfData(current, 5)).containsExactly("0", "3", "6", "9", "12");
		// begin of group-2
		assertThat(orchPlan.poolNextBatchOfData(next, 5)).containsExactly("1", "4", "7", "10", "13");
		// continue of group-2 and begin of group -3
		assertThat(orchPlan.poolNextBatchOfData(next, 5)).containsExactly("16", "20", "2", "5", "8");
		// end of group-1
		assertThat(orchPlan.poolNextBatchOfData(current, 5)).containsExactly("15", "18", "19");
		// end of group-3
		assertThat(orchPlan.poolNextBatchOfData(next, 5)).containsExactly("11", "14", "17");
		assertThat(orchPlan.poolNextBatchOfData(current, 5)).isEmpty();
		assertThat(orchPlan.poolNextBatchOfData(next, 5)).isEmpty();
		assertThat(orchPlan.poolNextBatchOfData(current, 5)).isEmpty();
		assertThat(orchPlan.poolNextBatchOfData(next, 5)).isEmpty();
	}	
}
