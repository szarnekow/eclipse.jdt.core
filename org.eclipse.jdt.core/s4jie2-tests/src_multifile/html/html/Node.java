package html;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import logicalcollections.LogicalList;
import logicalcollections.LogicalSet;

/**
 * Each instance of this class represents an element node or a text node in an HTML document.
 * 
 * @invar This node does not have the same node as a child twice.
 *    | LogicalList.distinct(getChildren())
 * @invar This node's children have this node as their parent.
 *    | getChildren().stream().allMatch(child -> child.getParent() == this)
 * @invar This node is a root node or else it is among its parent's children.
 *    | getParent() == null || getParent().getChildren().contains(this)
 * @invar This node does not have itself as an ancestor.
 *    | !getAncestors().contains(this)
 */
public abstract class Node {

	/**
	 * @invar | children != null
	 * @invar | children.stream().allMatch(child -> child != null && child.parent == this)
	 * @invar | LogicalList.distinct(children)
	 * @invar | parent == null || parent.children.contains(this)
	 * @invar | !getAncestorsPrivate().contains(this)
	 * 
	 * @peerObject
	 */
	private Node parent;
	/**
	 * @representationObject
	 * @peerObjects
	 */
	private ArrayList<Node> children;
	
	/**
	 * Returns this node's parent node.
	 * 
	 * @peerObject
	 * @basic
	 */
	public Node getParent() { return parent; }
	
	/**
	 * Returns the list of this node's child nodes.
	 * 
	 * @post | result != null
	 * @post | result.stream().allMatch(child -> child != null)
	 * @peerObjects
	 * @basic
	 */
	public List<Node> getChildren() {
		return List.copyOf(children);
	}
	
	private Set<Node> getAncestorsPrivate() {
		return LogicalSet.<Node>matching(ancestors ->
		    (parent == null || ancestors.contains(parent)) &&
		    ancestors.allMatch(ancestor ->
		        ancestor.parent == null || ancestors.contains(ancestor.parent))
		);
	}
	
	/**
	 * Returns the set of this node's ancestor nodes.
	 * 
	 * @post | result != null
	 * @post
	 *    | result.equals(LogicalSet.<Node>matching(ancestors ->
	 *    |     (getParent() == null || ancestors.contains(getParent())) &&
	 *    |     ancestors.allMatch(ancestor ->
	 *    |         ancestor.getParent() == null || ancestors.contains(ancestor.getParent()))))
	 */
	public Set<Node> getAncestors() {
		return getAncestorsPrivate();
	}
	
	/**
	 * Initializes this HTML document node as having no children
	 * and no parent.
	 * 
	 * @post This node's list of children is empty.
	 *    | getChildren().isEmpty()
	 * @post This node is a root node.
	 *    | getParent() == null
	 */
	public Node() {
		this.children = new ArrayList<Node>();
	}
	
	/**
	 * Adds the given node to the end of this node's list of children.
	 * 
	 * @pre {@code child} is not null.
	 *    | child != null
	 * @pre The given node is a root node.
	 *    | child.getParent() == null
	 * @pre The given node is not an ancestor of this node.
	 *    | !getAncestors().contains(child)
	 * @mutates_properties | getChildren(), child.getParent()
	 * @post This node's list of children equals its old list of children with the given node added to the end.
	 *    | getChildren().equals(LogicalList.plus(old(getChildren()), child))
	 * @post The given node's parent equals this node.
	 *    | child.getParent() == this
	 */
	public void addChild(Node child) {
		children.add(child);
		child.parent = this;
	}
	
	/**
	 * Removes the given node from this node's list of children.
	 * 
	 * @pre {@code child} is not null.
	 *    | child != null
	 * @pre The given node is a child of this node.
	 *    | getChildren().contains(child)
	 * @mutates_properties | getChildren(), child.getParent()
	 * @post This node's list of children equals its old list of children with the given node removed.
	 *    | getChildren().equals(LogicalList.minus(old(getChildren()), child))
	 * @post The given node is a root node.
	 *    | child.getParent() == null
	 */
	public void removeChild(Node child) {
		children.remove(child);
		child.parent = null;
	}
	
	/**
	 * Removes this node from its parent's list of children.
	 * 
	 * @pre This node is not a root node.
	 *    | getParent() != null
	 * @mutates_properties | getParent().getChildren(), getParent()
	 * @post This node's list of children equals its old list of children with the given node removed.
	 *    | old(getParent()).getChildren().equals(LogicalList.minus(old(getParent().getChildren()), this))
	 * @post The given node is a root node.
	 *    | getParent() == null
	 */
	public void remove() {
		parent.removeChild(this);
	}
	
	/**
	 * Returns a textual representation of this HTML document node and its descendants.
	 * 
	 * @post | result != null
	 */
	public String toString() {
		if (this instanceof TextNode) {
			return ((TextNode)this).getText();
		} else {
			String result = "<" + ((ElementNode)this).getTag()+ ">";
			for (Node child : children) {
			//for (int i = 0; i < children.size(); i++) {
				//Node child = children.get(i);
				result += child.toString();
			}
			result += "</" + ((ElementNode)this).getTag() + ">";
			return result;
		}
	}
	
	public int getTotalNbCharacters() {
		throw new AssertionError("Not yet implemented");
	}

}
