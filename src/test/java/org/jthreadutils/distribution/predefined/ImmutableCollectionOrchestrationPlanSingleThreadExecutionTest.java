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
		assertThat(orchPlan.poolNextBatchOfData(current, 5)).containsExactly("3", "6", "9");
		assertThat(orchPlan.poolNextBatchOfData(current, 5)).isEmpty();
	}
	
	@Test
	public void givenThreeGroupWithThreeElementsEach__whenPoolNextBatchOfDataExecutedForthTimes_withThreeElementsInBatchSize__thenFirstAndSecondAndThirdShouldReturnDataAvailable_andLastShouldBeEmpty() throws Exception {
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
	public void givenThreeGroupWithSixElementsEach__whenPoolNextBatchOfDataExecutedForthTimes_withTwoThreads_withFiveElementsInBatchSize__thenFirstAndSecondAndThirdShouldReturnDataAvailable_andLastShouldBeEmpty() throws Exception {
		// prepare
		final List<String> elements = IntStream.range(1, 19).mapToObj(Integer::toString).collect(Collectors.toList());
		
		// execute
		ImmutableCollectionOrchestrationPlanBuilder<String, String> orchPlanBuilder = ImmutableCollectionOrchestrationPlan.<String,String>builder(elements);
		orchPlanBuilder.putElementGroupIdAssignment(0, "GROUP-1");
		orchPlanBuilder.putElementGroupIdAssignment(3, "GROUP-1");
		orchPlanBuilder.putElementGroupIdAssignment(6, "GROUP-1");

		orchPlanBuilder.putElementGroupIdAssignment(1, "GROUP-2");
		orchPlanBuilder.putElementGroupIdAssignment(4, "GROUP-2");
		orchPlanBuilder.putElementGroupIdAssignment(4, "GROUP-2");

		orchPlanBuilder.putElementGroupIdAssignment(2, "GROUP-3");
		orchPlanBuilder.putElementGroupIdAssignment(5, "GROUP-3");
		
		ImmutableCollectionOrchestrationPlan<String, String> orchPlan = orchPlanBuilder.build();
		
		// verify
		Thread current = Thread.currentThread();
		Thread next = new Thread();
		assertThat(orchPlan.poolNextBatchOfData(current, 5)).containsExactly("1", "4", "7", "10", "13");
		assertThat(orchPlan.poolNextBatchOfData(next, 5)).containsExactly("2", "5", "8", "11", "14");
		assertThat(orchPlan.poolNextBatchOfData(next, 1)).isEmpty();
		assertThat(orchPlan.poolNextBatchOfData(current, 5)).containsExactly("3");
	}	
}
