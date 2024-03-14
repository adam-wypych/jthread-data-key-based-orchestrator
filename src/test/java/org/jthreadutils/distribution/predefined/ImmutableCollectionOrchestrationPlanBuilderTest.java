package org.jthreadutils.distribution.predefined;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.jthreadutils.distribution.predefined.ImmutableCollectionOrchestrationPlan.ImmutableCollectionOrchestrationPlanBuilder;
import org.junit.Test;

public class ImmutableCollectionOrchestrationPlanBuilderTest {
	
	@Test
	public void givenEmptyList__whenImmutableCollectionOrchestrationPlanBuilding__thenEmptyImmutableCollectionOrchestrationPlanShouldBeCreated() {
		// prepare
		final List<String> dataCollection = Arrays.asList();
		
		// execute
		ImmutableCollectionOrchestrationPlanBuilder<String, String> orchestrationPlanBuilder = ImmutableCollectionOrchestrationPlan.builder(dataCollection);
		ImmutableCollectionOrchestrationPlan<String, String> orchestrationPlan = orchestrationPlanBuilder.build();
		
		// verify
		assertThat(orchestrationPlan.poolNextBatchOfData(Thread.currentThread(), 1)).isEmpty();
	}
	
	@Test
	public void givenNonEmptyList__whenImmutableCollectionOrchestrationPlanBuilding_andRequestedSizeIsSameAsCollection__thenAllElementsOfImmutableCollectionOrchestrationPlanShouldBeCreated() {
		// prepare
		final List<String> dataCollection = Arrays.asList("1", "2", "3");
		
		// execute
		ImmutableCollectionOrchestrationPlanBuilder<String, String> orchestrationPlanBuilder = ImmutableCollectionOrchestrationPlan.<String, String>builder(dataCollection).putElementGroupIdAssignment(0, "GROUP-1").putElementGroupIdAssignment(1, "GROUP-2").putElementGroupIdAssignment(2, "GROUP-1");
		ImmutableCollectionOrchestrationPlan<String, String> orchestrationPlan = orchestrationPlanBuilder.build();
		
		// verify
		assertThat(orchestrationPlan.poolNextBatchOfData(Thread.currentThread(), 3)).containsExactly("1", "3", "2");
	}
	
	@Test
	public void givenNonEmptyList__whenImmutableCollectionOrchestrationPlanBuilding_andRequestedSizeIsBiggerThanCollectionOfData__thenAllElementsOfImmutableCollectionOrchestrationPlanShouldBeCreated() {
		// prepare
		final List<String> dataCollection = Arrays.asList("1", "2", "3");
		
		// execute
		ImmutableCollectionOrchestrationPlanBuilder<String, String> orchestrationPlanBuilder = ImmutableCollectionOrchestrationPlan.<String, String>builder(dataCollection).putElementGroupIdAssignment(0, "GROUP-1").putElementGroupIdAssignment(1, "GROUP-2").putElementGroupIdAssignment(2, "GROUP-1");
		ImmutableCollectionOrchestrationPlan<String, String> orchestrationPlan = orchestrationPlanBuilder.build();
		
		// verify
		assertThat(orchestrationPlan.poolNextBatchOfData(Thread.currentThread(), 5)).containsExactly("1", "3", "2");
	}
	
	@Test
	public void givenNonEmptyList__whenImmutableCollectionOrchestrationPlanBuilding_andRequestedSizeIsLessThanCollectionOfData__thenRequiredNumberOfElementsFromImmutableCollectionOrchestrationPlanShouldBeCreated() {
		// prepare
		final List<String> dataCollection = Arrays.asList("1", "2", "3");
		
		// execute
		ImmutableCollectionOrchestrationPlanBuilder<String, String> orchestrationPlanBuilder = ImmutableCollectionOrchestrationPlan.<String, String>builder(dataCollection).putElementGroupIdAssignment(0, "GROUP-1").putElementGroupIdAssignment(1, "GROUP-2").putElementGroupIdAssignment(2, "GROUP-1");
		ImmutableCollectionOrchestrationPlan<String, String> orchestrationPlan = orchestrationPlanBuilder.build();
		
		// verify
		assertThat(orchestrationPlan.poolNextBatchOfData(Thread.currentThread(), 2)).containsExactly("1", "3");
	}
}
