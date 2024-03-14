package org.jthreadutils.distribution.collection.immutable.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.jthreadutils.distribution.collection.immutable.LockableCollection;

/**
 * Lockable version of {@link ArrayList}.
 * 
 * @author adam-wypych
 * @since 1.0.0
 * @version %I%, %G%
 *
 * @param <T> type stored inside collection
 */
public class ArrayListLockableCollection<T> extends ArrayList<T> implements LockableCollection<T> {

	private static final long serialVersionUID = 1L;
	private final AtomicBoolean isLocked = new AtomicBoolean(false);

	/**
	 * Marks collection as immutable.
	 * @since 1.0.0
	 */
	@Override
	public void immutable() {
		isLocked.set(true);
	}

	/**
	 * Marks collection as mutable
	 * @since 1.0.0
	 */
	@Override
	public void mutable() {
		isLocked.set(false);
	}

	@Override
	public T set(final int index, final T element) {
		return performOperationIfNotLocked(() -> {
			return super.set(index, element);
		}, isLocked);
	}

	@Override
	public boolean add(final T e) {
		return performOperationIfNotLocked(() -> {
			return super.add(e);
		}, isLocked);
	}

	@Override
	public void add(final int index, final T element) {
		performOperationIfNotLocked(() -> {
			super.add(index, element);
			return null;
		}, isLocked);
	}

	@Override
	public T remove(final int index) {
		return performOperationIfNotLocked(() -> {
			return super.remove(index);
		}, isLocked);
	}

	@Override
	public boolean remove(final Object o) {
		return performOperationIfNotLocked(() -> {
			return super.remove(o);
		}, isLocked);
	}

	@Override
	public void clear() {
		performOperationIfNotLocked(() -> {
			super.clear();
			return null;
		}, isLocked);
	}

	@Override
	public boolean addAll(final Collection<? extends T> c) {
		return performOperationIfNotLocked(() -> {
			return super.addAll(c);
		}, isLocked);
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends T> c) {
		return performOperationIfNotLocked(() -> {
			return super.addAll(index, c);
		}, isLocked);
	}

	@Override
	protected void removeRange(final int fromIndex, final int toIndex) {
		performOperationIfNotLocked(() -> {
			super.removeRange(fromIndex, toIndex);
			return null;
		}, isLocked);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return performOperationIfNotLocked(() -> {
			return super.removeAll(c);
		}, isLocked);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return performOperationIfNotLocked(() -> {
			return super.retainAll(c);
		}, isLocked);
	}

	@Override
	public boolean removeIf(final Predicate<? super T> filter) {
		return performOperationIfNotLocked(() -> {
			return super.removeIf(filter);
		}, isLocked);
	}

	@Override
	public void replaceAll(final UnaryOperator<T> operator) {
		performOperationIfNotLocked(() -> {
			super.replaceAll(operator);
			return null;
		}, isLocked);
	}

	@Override
	public void sort(final Comparator<? super T> c) {
		performOperationIfNotLocked(() -> {
			super.sort(c);
			return null;
		}, isLocked);
	}
}
