package org.jthreadutils.distribution.collection.immutable;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Interface for implement collections with possibility to switch on/off
 * possibility for modification in any time user wants. This interface allows to
 * block any modification when it shouldn't be possible, mostly for usage
 * collection with third party code.
 * 
 * @author adam-wypych
 * @since 1.0.0
 * @version %I%, %G%
 * 
 * @param <T> type of collection elements inside
 */
public interface LockableCollection<T> extends Collection<T> {
	/**
	 * Mark collection as immutable. All modifications method should either thrown
	 * exception or return <code>false</code>.
	 * 
	 * @author adam-wypych
	 * @since 1.0.0
	 * @version %I%, %G%
	 */
	void immutable();

	/**
	 * Mark collection as mutable, all modifications method should be possible.
	 * 
	 * @author adam-wypych
	 * @since 1.0.0
	 * @version %I%, %G%
	 */
	void mutable();

	/**
	 * Generic method created to check if collection is not being locked for
	 * modification (marked as immutable) before user called modification method.
	 * 
	 * @param <C>      type of result of modification method either {@link Void} or
	 *                 any other object
	 * @param action   modification action to be performed wrapped into
	 *                 {@link Callable} for result type {@link Void} please return
	 *                 <code>null</code>
	 * @param isLocked marker used for say if collection is open for modification or
	 *                 not
	 * @return result of {@link action}
	 * 
	 * @throws UnsupportedOperationException in case collection is marked as
	 *                                       immutable
	 * @throws RuntimeException              as wrapper for any {@link Exception}
	 *                                       from {@link Callable#call()} result
	 * @author adam-wypych
	 * @since 1.0.0
	 * @version %I%, %G%
	 */
	default <C> C performOperationIfNotLocked(final Callable<C> action, final AtomicBoolean isLocked) {
		synchronized (isLocked) {
			if (isLocked.compareAndSet(true, true)) {
				throw new UnsupportedOperationException(
						"Operation can't be performed given colleciton is locked for modification.");
			} else {
				try {
					return action.call();
				} catch (Exception e) {
					throw new RuntimeException("Callable action " + action + " failed with exception.", e);
				}
			}
		}
	}
}
