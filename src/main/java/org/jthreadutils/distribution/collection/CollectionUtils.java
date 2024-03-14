package org.jthreadutils.distribution.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.google.common.base.Objects;

/**
 * Helper class which provide functionality missing in JDK and other libraries
 * include Guava around working with {@link Collection}.
 * 
 * @author adam-wypych
 * @since 1.0.0
 * @version %I%, %G%
 */
public class CollectionUtils {

	/**
	 * @param <T> type of data inside collection
	 * @param collection original collection
	 * @param indexes of element to collect from collection
	 * @return sorted by index list of elements from collection
	 * 
	 * @see #getElementsByIndexes(Collection, Collection)
	 * @throws IndexOutOfBoundsException
	 * @since 1.0.0
	 */
	public static <T> List<T> getElementsByIndexes(final Collection<T> collection, final Integer... indexes) {
		return getElementsByIndexes(collection, Arrays.asList(indexes));
	}

	/**
	 * @param <T> type of data inside collection
	 * @param collection original collection
	 * @param indexes of element to collect from collection
	 * @return sorted by index list of elements from collection
	 * 
	 * @throws IndexOutOfBoundsException
	 * @since 1.0.0
	 */
	public static <T> List<T> getElementsByIndexes(final Collection<T> collection,
			final Collection<Integer> indexes) {
		final List<Integer> indexesDeduplicatedAndSorted = new ArrayList<>(new TreeSet<>(indexes));

		validateIndexes(collection, indexesDeduplicatedAndSorted);
		final List<T> elementsToReturn = new ArrayList<>(indexesDeduplicatedAndSorted.size());

		final Iterator<T> iter = collection.iterator();
		for (int currentIndexInsideCollection = 0, currentIndexWithinIndexes = 0; iter.hasNext()
				&& currentIndexWithinIndexes < indexesDeduplicatedAndSorted.size(); currentIndexInsideCollection++) {
			T element = iter.next();
			if (currentIndexInsideCollection == indexesDeduplicatedAndSorted.get(currentIndexWithinIndexes)) {
				elementsToReturn.add(element);
				++currentIndexWithinIndexes;
			}
		}

		return elementsToReturn;
	}

	private static <T> void validateIndexes(final Collection<T> collection, final List<Integer> indexes) {
		final int collectionSize = collection.size();
		for (int index : indexes) {
			if (index < 0 || index >= collectionSize) {
				throw new IndexOutOfBoundsException("Index " + index + " doesn't exists in collection");
			}
		}
	}
	
	public static <T> boolean areEqualByContent(final Collection<T> theFirst, final Collection<T> theSecond) {
		if (theFirst.size() != theSecond.size()) {
			return false;
		}
		
		Iterator<T> theFirstCollectionIter = theFirst.iterator();
		Iterator<T> theSecondCollectionIter = theSecond.iterator();
		
		while (theFirstCollectionIter.hasNext() && theSecondCollectionIter.hasNext()) {
			if (!Objects.equal(theFirstCollectionIter.next(), theSecondCollectionIter.next())) {
				return false;
			}
		}
		
		return true;
	}
}
