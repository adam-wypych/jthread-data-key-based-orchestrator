package org.jthreadutils.distribution.collection.immutable;

import java.util.Collection;
import java.util.List;

/**
 * Template factory interface for {@link LockableCollection} creators.
 * 
 * @author adam-wypych
 * @since 1.0.0
 * @version %I%, %G%
 *
 */
public interface LockableCollectionFactory {
	/**
	 * @author adam-wypych
	 * @since 1.0.0
	 * @version %I%, %G%
	 * 
	 * @param <A> generic type for classes extending {@link Collection} type
	 * @return supported types of {@link Collection}
	 */
	<A extends Collection<?>> List<Class<A>> getSupportedCollections();

	/**
	 * @author adam-wypych
	 * @since 1.0.0
	 * @version %I%, %G%
	 * 
	 * @param <T>                   type of element stored inside collection
	 * @param <A>                   collection type to be replaced by lockable
	 *                              version
	 * @param orginalCollectionType a type of {@link Collection} for which
	 *                              {@link LockableCollection} should be provided
	 * @return a {@link LockableCollection} to use as one of option for processing
	 *         fixed/static amount of data
	 */
	<T, A extends Collection<T>> LockableCollection<T> createLockableCollection(final Class<A> orginalCollectionType);
}
