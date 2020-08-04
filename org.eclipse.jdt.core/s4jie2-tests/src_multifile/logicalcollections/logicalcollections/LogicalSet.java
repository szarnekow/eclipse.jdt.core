package logicalcollections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class LogicalSet<T> {
	
	private Set<T> elements = new HashSet<T>();
	private List<T> elementsList = new ArrayList<T>();
	private List<Predicate<T>> predicates = new ArrayList<Predicate<T>>();
	
	public boolean contains(T element) {
		if (!elements.contains(element)) {
			elements.add(element);
			elementsList.add(element);
			int predicatesCount = predicates.size();
			for (int i = 0; i < predicatesCount; i++)
				if (!predicates.get(i).test(element))
					throw new AssertionError("Logical set constraint violated");
		}
		return true;
	}
	
	public boolean containsAll(Iterable<T> objects) {
		for (var object : objects)
			contains(object);
		return true;
	}
	
	public boolean allMatch(Predicate<T> predicate) {
		predicates.add(predicate);
		int elementsCount = elementsList.size();
		for (int i = 0; i < elementsCount; i++)
			if (!predicate.test(elementsList.get(i)))
				throw new AssertionError("Logical set constraint violated");
		return true;
	}
	
	/**
	 * Returns a set that satisfies the given predicate.
	 * 
	 * @pre | predicate != null
	 * @post | result != null
	 */
	public static <T> Set<T> matching(Predicate<LogicalSet<T> > predicate) {
		LogicalSet<T> set = new LogicalSet<T>();
		if (!predicate.test(set))
			throw new AssertionError("Logical set constraint violated");
		var elements = set.elements;
		set.elements = null;
		return elements;
	}
	
	/**
	 * Returns the set obtained by adding the given element to the given set.
	 * 
	 * @pre | set != null
	 * @inspects | set
	 * @creates | result
	 * @post | result.containsAll(set)
	 * @post | result.contains(element)
	 * @post | result.stream().allMatch(e -> set.contains(e) || e == element)
	 */
	public static <T> Set<T> plus(Set<T> set, T element) {
		HashSet<T> result = new HashSet<T>(set);
		result.add(element);
		return result;
	}
	
	/**
	 * Returns the set obtained by removing the given element from the given set.
	 * 
	 * @pre | set != null
	 * @inspects | set
	 * @creates | result
	 * @post | set.stream().allMatch(e -> e == element || result.contains(e))
	 * @post | set.containsAll(result)
	 * @post | !result.contains(element)
	 */
	public static <T> Set<T> minus(Set<T> set, T element) {
		HashSet<T> result = new HashSet<T>(set);
		result.remove(element);
		return result;
	}
}
