package networks;

import java.util.HashSet;
import java.util.Set;

import logicalcollections.LogicalSet;

/**
 * @invar | getNeighbors() != null
 * @invar | getNeighbors().stream().allMatch(neighbor -> neighbor != null && neighbor.getNeighbors().contains(this))
 */
public class Node {
	
	/**
	 * @invar | neighbors != null
	 * @invar | neighbors.stream().allMatch(neighbor -> neighbor != null && neighbor.neighbors.contains(this))
	 * 
	 * @representationObject
	 * @peerObjects
	 */
	private Set<Node> neighbors = new HashSet<>();
	
	/**
	 * @peerObjects
	 */
	public Set<Node> getNeighbors() { return Set.copyOf(neighbors); }

	/**
	 * @mutates | this
	 * @post | getNeighbors().isEmpty()
	 */
	public Node() {}
	
	/**
	 * @throws IllegalArgumentException | other == null
	 * @mutates_properties | this.getNeighbors(), other.getNeighbors()
	 * @post | getNeighbors().equals(LogicalSet.plus(old(getNeighbors()), other))
	 * @post | other.getNeighbors().equals(LogicalSet.plus(old(other.getNeighbors()), this))
	 */
	public void linkTo(Node other) {
		if (other == null)
			throw new IllegalArgumentException("other is null");
		neighbors.add(other);
		other.neighbors.add(this);
	}
	
	/**
	 * @pre | other != null
	 * @mutates_properties | this.getNeighbors(), other.getNeighbors()
	 * @post | getNeighbors().equals(LogicalSet.minus(old(getNeighbors()), other))
	 * @post | other.getNeighbors().equals(LogicalSet.minus(old(other.getNeighbors()), this))
	 */
	public void unlinkFrom(Node other) {
		if (other == null)
			throw new IllegalArgumentException("other is null");
		neighbors.remove(other);
		other.neighbors.remove(this);
	}

}
