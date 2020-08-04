package logicalcollections;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class LogicalMap<K, V> {
	
	public class KeySet {
		
		public boolean allMatch(Predicate<K> predicate) {
			predicates.add(predicate);
			int keysCount = keysList.size();
			for (int i = 0; i < keysCount; i++)
				if (!predicate.test(keysList.get(i)))
					throw new AssertionError("Logical map constraint violated");
			return true;
		}
		
	}
	
	private Map<K, V> entries = new HashMap<K, V>();
	private final List<K> keysList = new ArrayList<K>();
	private final List<Predicate<K>> predicates = new ArrayList<>();
	private final KeySet keySet = new KeySet();
	
	public KeySet keySet() { return keySet; }
	
	private void addEntry(K key, V value) {
		entries.put(key, value);
		keysList.add(key);
		int predicatesCount = predicates.size();
		for (int i = 0; i < predicatesCount; i++)
			if (!predicates.get(i).test(key))
				throw new AssertionError("Logical map constraint violated");
	}
	
	public boolean containsKey(K key) {
		if (!entries.containsKey(key))
			addEntry(key, null);
		return true;
	}
	
	public boolean containsEntry(K key, V value) {
		if (value == null)
			throw new IllegalArgumentException("null values are not supported");
		if (!entries.containsKey(key))
			addEntry(key, value);
		else {
			V oldValue = entries.get(key);
			if (oldValue == null)
				entries.put(key, value);
			else if (!oldValue.equals(value))
				throw new AssertionError("Conflicting values for same key");
		}
		return true;
	}
	
	/**
	 * Returns a map that satisfies the given predicate.
	 * Throws an AssertionError if it fails to find such a map.
	 * This method may in some cases fail to find a map that satisfies the given predicate, even if one exists.
	 * However, if both the given predicate and all predicates given as arguments in calls of
	 * {@code keySet().allMatch} on the given predicate's argument are "monotonic", then this method
	 * is guaranteed to find and return a map that satisfies the given predicate, if one exists.
	 * A predicate is monotonic if, whenever all {@code containsKey}, {@code containsEntry}, and
	 * {@code keySet().allMatch} calls it performs on the given predicate's argument return {@code true} in more cases,
	 * the predicate returns {@code true} in more cases.
	 * 
	 * @pre | predicate != null
	 * @post | result != null // && predicate.test(result)
	 */
	public static <K, V> Map<K, V> matching(Predicate<LogicalMap<K, V>> predicate) {
		var map = new LogicalMap<K, V>();
		if (!predicate.test(map))
			throw new AssertionError("Logical map constraint violated");
		var result = map.entries;
		map.entries = null;
		return result;
	}
	
	/**
	 * Returns whether all of {@code map2}'s keys, except for {@code keys},
	 * are in {@code map1} and map to the same value.
	 * 
	 * @pre | map1 != null
	 * @pre | map2 != null
	 * @pre | keys != null
	 * @post
	 *    | result == map2.keySet().stream().allMatch(key ->
	 *    |     Set.of(keys).contains(key) || map1.containsKey(key) && Objects.equals(map1.get(key), map2.get(key)))
	 */
	@SafeVarargs
	public static <K, V> boolean extendsExcept(Map<K, V> map1, Map<K, V> map2, K... keys) {
		HashSet<K> keysSet = new HashSet<>(Arrays.asList(keys));
		for (K key : map2.keySet()) {
			if (!keysSet.contains(key)) {
				if (!map1.containsKey(key))
					return false;
				if (!Objects.equals(map1.get(key), map2.get(key)))
					return false;
			}
		}
		return true;
	}
	
	@SafeVarargs
	public static <K, V> boolean equalsExcept(Map<K, V> map1, Map<K, V> map2, K... keys) {
		return extendsExcept(map1, map2, keys) && extendsExcept(map2, map1, keys);
	}
	
	public static <K, V> boolean allEntriesMatch(Map<K, V> map, BiPredicate<K, V> predicate) {
		return map.keySet().stream().allMatch(k -> predicate.test(k, map.get(k)));
	}
	
}
