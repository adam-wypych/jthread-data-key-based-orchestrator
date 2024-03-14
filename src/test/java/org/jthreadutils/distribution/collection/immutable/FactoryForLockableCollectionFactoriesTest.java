package org.jthreadutils.distribution.collection.immutable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Test;

public class FactoryForLockableCollectionFactoriesTest {

	@Test
	public void givenEmptyFactoryList__whenFactoryOfFactoriesAreBeingCreated__thenNoExceptionsShouldBeThrown() {
		FactoryForLockableCollectionFactories factoryOfFactories = new FactoryForLockableCollectionFactories(
				Collections.emptyList()) {};
		assertThat(factoryOfFactories).isNotNull();
	}

	@Test
	public void givenOneFactoryInsideList_withoutAnySupportedType__whenFactoryOfFactoriesAreBeingCreated__thenNoExpcetionsShouldBeThrown() {
		// prepare
		LockableCollectionFactory factoryOne = mock(LockableCollectionFactory.class);
		doReturn(Collections.emptyList()).when(factoryOne).getSupportedCollections();

		// execute
		FactoryForLockableCollectionFactories factoryOfFactories = new FactoryForLockableCollectionFactories(
				Arrays.asList(factoryOne)) {};

		// verify
		assertThat(factoryOfFactories).isNotNull();
	}

	@Test
	public void givenOneFactoryInsideList_withOneSupportedType__whenFactoryOfFactoriesIsBeingCreated_andCallForSupportedType__thenFactoryShouldBeReturn() {
		// prepare
		LockableCollectionFactory factoryOne = mock(LockableCollectionFactory.class);
		doReturn(Arrays.asList(ArrayList.class)).when(factoryOne).getSupportedCollections();

		// execute
		FactoryForLockableCollectionFactories factoryOfFactories = new FactoryForLockableCollectionFactories(
				Arrays.asList(factoryOne)) {};

		// verify
		assertThat(factoryOfFactories).isNotNull();
		assertThat(factoryOfFactories.getFactory(ArrayList.class)).contains(factoryOne);
	}

	@Test
	public void givenThreeFactoriesInsideList_withOneSupportedType_andNoCollision__whenFactoryOfFactoriesIsBeingCreated_andCallForSupportedType__thenFactoriesShouldBeReturn() {
		// prepare
		LockableCollectionFactory factoryOne = mock(LockableCollectionFactory.class);
		doReturn(Arrays.asList(ArrayList.class)).when(factoryOne).getSupportedCollections();
		LockableCollectionFactory factoryTwo = mock(LockableCollectionFactory.class);
		doReturn(Arrays.asList(LinkedList.class)).when(factoryTwo).getSupportedCollections();
		LockableCollectionFactory factoryThree = mock(LockableCollectionFactory.class);
		doReturn(Arrays.asList(CopyOnWriteArrayList.class)).when(factoryThree).getSupportedCollections();

		// execute
		FactoryForLockableCollectionFactories factoryOfFactories = new FactoryForLockableCollectionFactories(
				Arrays.asList(factoryOne, factoryTwo, factoryThree)) {};

		// verify
		assertThat(factoryOfFactories).isNotNull();
		assertThat(factoryOfFactories.getFactory(ArrayList.class)).contains(factoryOne);
		assertThat(factoryOfFactories.getFactory(LinkedList.class)).contains(factoryTwo);
		assertThat(factoryOfFactories.getFactory(CopyOnWriteArrayList.class)).contains(factoryThree);
	}

	@Test
	public void givenOneFactoryInsideList_withOneSupportedType__whenFactoryOfFactoriesIsBeingCreated_andCallForTypeNotSupported__thenEmptyOptionalShouldBeReturn() {
		// prepare
		LockableCollectionFactory factoryOne = mock(LockableCollectionFactory.class);
		doReturn(Arrays.asList(ArrayList.class)).when(factoryOne).getSupportedCollections();

		// execute
		FactoryForLockableCollectionFactories factoryOfFactories = new FactoryForLockableCollectionFactories(
				Arrays.asList(factoryOne)) {};

		// verify
		assertThat(factoryOfFactories).isNotNull();
		assertThat(factoryOfFactories.getFactory(LinkedList.class)).isEmpty();
	}

	@Test
	public void givenTwoFactoriesInsideList_withSameSupportedType__whenFactoryOfFactoriesIsBeingCreated__thenProperExceptionShouldBeThrown() {
		// prepare
		Class<?> supportedType = ArrayList.class;
		LockableCollectionFactory factoryOne = mock(LockableCollectionFactory.class);
		doReturn(Arrays.asList(supportedType)).when(factoryOne).getSupportedCollections();
		LockableCollectionFactory factoryTwo = mock(LockableCollectionFactory.class);
		doReturn(Arrays.asList(supportedType)).when(factoryTwo).getSupportedCollections();

		// execute
		IllegalArgumentException exception = null;
		try {
			new FactoryForLockableCollectionFactories(Arrays.asList(factoryOne, factoryTwo)) {};
		} catch (IllegalArgumentException e) {
			exception = e;
		}

		// verify
		assertThat(exception).isNotNull();
		assertThat(exception).hasMessage("It looks that at least 2 factories " + factoryTwo + " and " + factoryOne
				+ " are supporting " + supportedType);
	}
}
