package org.jthreadutils.distribution.collection.immutable.impl;

import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultFactoryForLockableCollectionFactoriesTest {
	
	private DefaultFactoryForLockableCollectionFactories sut;

	@Test
	public void givenArrayListCollectionClass__whenAskForFactory__thenFactoryShouldBeReturn() {
		assertThat(sut.getFactory(ArrayList.class)).containsInstanceOf(ArrayListLockableCollectionFactory.class);
	}
	
	@Test
	public void givenCollectionClassNotIncludedInFactoryOfFactories__whenAskForFactory__thenOptionalEmptyShouldBeReturn() {
		assertThat(sut.getFactory(Collection.class)).isEmpty();
	}
	
	@Before
	public void setUp() {
		this.sut = new DefaultFactoryForLockableCollectionFactories();
	}
}
