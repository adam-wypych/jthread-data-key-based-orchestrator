package org.jthreadutils.distribution.collection.immutable.impl;

import java.util.Arrays;

import org.jthreadutils.distribution.collection.immutable.FactoryForLockableCollectionFactories;
import org.jthreadutils.distribution.collection.immutable.LockableCollection;

/**
 * This is factory which includes all factories for creating {@link LockableCollection}.
 * 
 * @author adam-wypych
 * @since 1.0.0
 * @version %I%, %G%
 * 
 * @see ArrayListLockableCollectionFactory
 */
public class DefaultFactoryForLockableCollectionFactories extends FactoryForLockableCollectionFactories {

	public DefaultFactoryForLockableCollectionFactories() {
		super(Arrays.asList(new ArrayListLockableCollectionFactory()));
	}
}
