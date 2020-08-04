package logicalcollections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Declares a number of side-effect-free methods for
 * producing new lists from existing lists.
 */
public class LogicalList {

	/**
	 * Returns the list obtained by adding the given element to the
	 * end of the given list.
	 * 
	 * @inspects | list
	 * @creates | result
	 * @pre | list != null
	 * @post | result != null
	 * @post | result.size() == list.size() + 1
	 * @post | IntStream.range(0, list.size()).allMatch(i -> result.get(i) == list.get(i))
	 * @post | result.get(list.size()) == element
	 */
	public static <T> List<T> plus(List<T> list, T element) {
		List<T> result = new ArrayList<T>(list);
		result.add(element);
		return result;
	}

	/**
	 * Returns the list obtained by inserting the given element at the
	 * given index into the given list.
	 * 
	 * @inspects | list
	 * @creates | result
	 * @pre | list != null
	 * @pre | 0 <= index && index <= list.size()
	 * @post | result != null
	 * @post | result.size() == list.size() + 1
	 * @post | IntStream.range(0, index).allMatch(i -> result.get(i) == list.get(i))
	 * @post | IntStream.range(index, list.size()).allMatch(i -> result.get(i + 1) == list.get(i))
	 * @post | result.get(index) == element
	 */
	public static <T> List<T> plusAt(List<T> list, int index, T element) {
		List<T> result = new ArrayList<T>(list);
		result.add(index, element);
		return result;
	}

	/**
	 * Returns the list obtained by removing the first occurrence of the
	 * given element from the given list.
	 * 
	 * @inspects | list
	 * @creates | result
	 * @pre | list != null
	 * @post | result != null
	 * @post | list.contains(element) || result.equals(list)
	 * @post | !list.contains(element) ||
	 *       | result.size() == list.size() - 1 &&
	 *       | IntStream.range(0, list.indexOf(element)).allMatch(i -> result.get(i) == list.get(i)) &&
	 *       | IntStream.range(list.indexOf(element), result.size()).allMatch(i -> result.get(i) == list.get(i + 1))
	 */
	public static <T> List<T> minus(List<T> list, T element) {
		List<T> result = new ArrayList<T>(list);
		result.remove(element);
		return result;
	}
	
	/**
	 * Returns whether no two elements of the given list are equal.
	 * 
	 * @inspects | list
	 * @pre | list != null
	 * @post | result == IntStream.range(0, list.size()).allMatch(i -> !list.subList(i + 1, list.size()).contains(list.get(i)))
	 */
	public static <T> boolean distinct(List<T> list) {
		var elements = new HashSet<T>();
		for (var element : list) {
			if (elements.contains(element))
				return false;
			elements.add(element);
		}
		return true;
	}

}
