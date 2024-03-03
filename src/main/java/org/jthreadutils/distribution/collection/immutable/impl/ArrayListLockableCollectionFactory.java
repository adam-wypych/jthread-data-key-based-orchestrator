package org.jthreadutils.distribution.collection.immutable.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jthreadutils.distribution.collection.immutable.LockableCollection;
import org.jthreadutils.distribution.collection.immutable.LockableCollectionFactory;

/**
 * Factory responsible for creation of {@link LockableCollection} for {@link ArrayList}.
 * 
 * @author adam-wypych
 * @since 1.0.0
 * @version %I%, %G%
 */
public class ArrayListLockableCollectionFactory implements LockableCollectionFactory {
	@SuppressWarnings("rawtypes")
	private final static List<Class<? extends Collection>> SUPPORTED_TYPES = Arrays.asList(ArrayList.class);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Class<? extends Collection>> getSupportedCollections() {
		return SUPPORTED_TYPES;
	}

	@Override
	public <T, A extends Collection<T>> LockableCollection<T> createLockableCollection(final Class<A> orginalCollectionType) {
		if (orginalCollectionType == ArrayList.class) {
			return new ArrayListLockableCollection<T>();
		} else {
			throw new IllegalArgumentException("Type " +  orginalCollectionType + " is not supported by " + this.getClass());
		}
	}

}
