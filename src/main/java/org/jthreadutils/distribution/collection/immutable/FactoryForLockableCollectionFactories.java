package org.jthreadutils.distribution.collection.immutable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Factory of {@link LockableCollectionFactory} which take responsibility for
 * check if given {@link Collection} has only one factory responsible for
 * creating its {@link LockableCollection} version.
 * 
 * @author adam-wypych
 * @since 1.0.0
 * @version %I%, %G%
 */
public abstract class FactoryForLockableCollectionFactories {
	private Map<Class<? extends Collection<?>>, LockableCollectionFactory> mappedCollectionIntoLockableFactoryProvider = new HashMap<>();

	/**
	 * Internally constructor perform de-duplication in terms of supported
	 * {@link Collection}.
	 * 
	 * @param factories
	 * @throws IllegalArgumentException in case given class of {@link Collection} is
	 *                                  being supported by at least 2
	 *                                  {@link LockableCollectionFactory}
	 * @author adam-wypych
	 * @since 1.0.0
	 * @version %I%, %G%
	 */
	public FactoryForLockableCollectionFactories(final List<LockableCollectionFactory> factories) {
		init(factories);
	}

	private void init(final List<LockableCollectionFactory> factories) {
		for (LockableCollectionFactory factory : factories) {
			for (Class<? extends Collection<?>> supportedCollectionType : factory.getSupportedCollections()) {
				if (mappedCollectionIntoLockableFactoryProvider.containsKey(supportedCollectionType)) {
					throw new IllegalArgumentException("It looks that at least 2 factories " + factory + " and "
							+ mappedCollectionIntoLockableFactoryProvider.get(supportedCollectionType)
							+ " are supporting " + supportedCollectionType);
				} else {
					mappedCollectionIntoLockableFactoryProvider.put(supportedCollectionType, factory);
				}
			}
		}
	}

	/**
	 * @param collectionType original collection type for which
	 *                       {@link LockableCollectionFactory} should be found
	 * @return either supporting {@link LockableCollectionFactory} or optional empty
	 *         in terms type is not supported
	 * 
	 * @author adam-wypych
	 * @since 1.0.0
	 * @version %I%, %G%
	 */
	public Optional<LockableCollectionFactory> getFactory(@SuppressWarnings("rawtypes") final Class<? extends Collection> collectionType) {
		return Optional.ofNullable(mappedCollectionIntoLockableFactoryProvider.get(collectionType));
	}
}
