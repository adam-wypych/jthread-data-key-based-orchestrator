package org.jthreadutils.distribution.collection.immutable.impl;

import org.jthreadutils.distribution.collection.immutable.LockableCollection;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import javax.management.AttributeList;

public class ArrayListLockableCollectionFactoryTest {
	private ArrayListLockableCollectionFactory sut;

	@Test
	public void testIfOnlyArrayListIsReturnAsSupportedType() {
		assertThat(sut.getSupportedCollections()).containsExactly(ArrayList.class);
	}
	
	@Test
	public void givenSubclassOfArrayList__whenAskForCreatingLockableCollectionFactory__thenItShouldBeAnException() {
		// execute
		IllegalArgumentException error = null;
		
		try {
			sut.createLockableCollection(AttributeList.class);
		} catch (IllegalArgumentException e) {
			error = e;
		}
		
		// verify
		assertThat(error).isNotNull();
		assertThat(error).hasMessage("Type " + AttributeList.class + " is not supported by " + sut.getClass());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void givenOriginalCollectionTypeArrayList__whenAskForCreatingLockableCollectionFactory__thenNewObjectShouldBeReturn() {
		// execute
		LockableCollection<Object> result = sut.createLockableCollection(ArrayList.class);
		
		// verify
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(ArrayListLockableCollection.class);
	}
	
	@Before
	public void setUp() {
		this.sut = new ArrayListLockableCollectionFactory();
	}
}
