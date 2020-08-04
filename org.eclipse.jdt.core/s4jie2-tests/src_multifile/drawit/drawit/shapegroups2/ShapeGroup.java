package drawit.shapegroups2;

import java.util.List;
import java.util.Set;

import drawit.IntPoint;
import drawit.IntVector;
import drawit.RoundedPolygon;

import logicalcollections.LogicalList;
import logicalcollections.LogicalSet;

/**
 * Each instance of this class represents a shape group. A shape group is either a leaf group,
 * in which case it directly contains a single shape, or it is a non-leaf group, in which case it directly contains
 * two or more subgroups, which are themselves shape groups.
 * 
 * Besides directly or indirectly grouping one or more shapes, a shape group defines a transformation 
 * (i.e. a displacement and/or a horizontal and/or vertical scaling) of the shapes it contains.
 * 
 * @invar | getParentGroup() == null ||
 *        | getParentGroup().getSubgroups() != null && getParentGroup().getSubgroups().contains(this)
 * @invar | !getAncestors().contains(this)
 */
public abstract class ShapeGroup {
	
	/**
	 * @invar | (parent == null) == (nextSibling == null)
	 * @invar A non-leaf shape group has at least two subgroups
	 *    | nextSibling != this
	 * @invar | (parent == null) == (previousSibling == null)
	 * @invar | parent == null || nextSibling.parent == parent && nextSibling.previousSibling == this
	 * @invar | parent == null || previousSibling.parent == parent && previousSibling.nextSibling == this
	 * @invar | parent == null || parent.getSubgroupsPrivate().contains(this)
	 * @invar | !getAncestorsPrivate().contains(this)
	 */
	/** @peerObject */
	NonleafShapeGroup parent;
	/** @peerObject */
	ShapeGroup previousSibling;
	/** @peerObject */
	ShapeGroup nextSibling;
	Extent originalExtent;
	Extent currentExtent;
	
	/**
	 * Returns the set of the ancestors of this shape group.
	 * 
	 * @post | result != null
	 * @post | result.equals(LogicalSet.<ShapeGroup>matching(ancestors ->
	 *       |     getParentGroup() == null || ancestors.contains(getParentGroup()) &&
	 *       |     ancestors.allMatch(ancestor ->
	 *       |         ancestor.getParentGroup() == null || ancestors.contains(ancestor.getParentGroup()))))
	 */
	public Set<ShapeGroup> getAncestors() {
		return getAncestorsPrivate();
	}
	
	Set<ShapeGroup> getAncestorsPrivate() {
		return LogicalSet.<ShapeGroup>matching(ancestors ->
			parent == null || ancestors.contains(parent) &&
			ancestors.allMatch(ancestor -> ancestor.parent == null || ancestors.contains(ancestor.parent))
		);
	}
	
	/**
	 * Returns the extent of this shape group, expressed in its outer coordinate system.
	 * The extent of a shape group is the smallest axis-aligned rectangle that contained
	 * the shape group's shape (if it is a leaf shape group) or its subgroups' extents
	 * (if it is a non-leaf shape group) when the shape group was created. After the shape
	 * group is created, subsequent mutations of the shape or subgroups contained by the shape
	 * group do not affect its extent. As a result, after mutating the shape or subgroups contained
	 * by this shape group, this shape group's extent might no longer contain its shape or its subgroups
	 * or might no longer be the smallest axis-aligned rectangle that does so.
	 *
	 * @basic
	 * @post | result != null
	 */
	public Extent getExtent() {
		return currentExtent;
	}
	
	/**
	 * Returns the extent of this shape group, expressed in its inner coordinate system.
	 * This coincides with the extent expressed in outer coordinates at the time of
	 * creation of the shape group. The shape transformation defined by this shape group is the one
	 * that transforms the original extent to the current extent.
	 * 
	 * This method returns an equal result throughout the lifetime of this object.
	 * 
	 * @immutable
	 * @post | result != null
	 */
	public Extent getOriginalExtent() {
		return originalExtent;
	}
	
	/**
	 * Returns the shape group that directly contains this shape group, or {@code null}
	 * if no shape group directly contains this shape group.
	 * 
	 * @basic
	 * @peerObject
	 */
	public NonleafShapeGroup getParentGroup() { return parent; }
	
	/**
	 * Returns the list of the RoundedPolygon objects contained directly or indirectly by this shape group,
	 * in depth-first order.
	 * 
	 * (Full details are given in the subclasses.)
	 * 
	 * @post | result != null
	 * @post | result.stream().allMatch(s -> s != null)
	 */
	public abstract List<RoundedPolygon> getAllShapes();
	
	/**
	 * Returns the coordinates in this shape group's inner coordinate system of the point
	 * whose coordinates in the global coordinate system are the given coordinates.
	 * 
	 * The global coordinate system is the outer coordinate system of this shape group's root ancestor,
	 * i.e. the ancestor that has no parent.
	 * 
	 * This shape group's inner coordinate system is defined by the fact that the coordinates of its extent
	 * in its inner coordinate system are constant and given by {@code this.getOriginalExtent()}.
	 * 
	 * Its outer coordinate system is defined by the fact that the coordinates of its extent in its outer
	 * coordinate system are as given by {@code this.getExtent()}.
	 * 
	 * When a shape group is created, its inner coordinate system coincides with its outer coordinate system
	 * (and with the global coordinate system). Subsequent calls of {@code this.setExtent()} may cause the
	 * inner and outer coordinate systems to no longer coincide.
	 * 
	 * The inner coordinate system of a non-leaf shape group always coincides with the outer coordinate
	 * systems of its subgroups. Furthermore, the coordinates of the vertices of a shape contained by a leaf
	 * shape group are interpreted in the inner coordinate system of the shape group.
	 * 
	 * @throws IllegalArgumentException if {@code globalCoordinates} is null
	 *    | globalCoordinates == null
	 * @inspects | this
	 * @post | result != null
	 * @post | result.equals(outerToInnerCoordinates(globalToOuterCoordinates(globalCoordinates)))
	 */
	public IntPoint toInnerCoordinates(IntPoint globalCoordinates) {
		if (globalCoordinates == null)
			throw new IllegalArgumentException("globalCoordinates is null");
		
		return outerToInnerCoordinates(globalToOuterCoordinates(globalCoordinates));
	}

	/**
	 * Returns the coordinates in this shape group's inner coordinate system of the point whose
	 * coordinates in this shape group's outer coordinate system are the given coordinates.
	 * 
	 * @throws IllegalArgumentException if {@code outerCoordinates} is null
	 *    | outerCoordinates == null
	 * @inspects | this
	 * @post | result != null
	 * @post | result.equals(getOriginalExtent().getTopLeft().plus(
	 *       |     outerToInnerCoordinates(outerCoordinates.minus(getExtent().getTopLeft()))))
	 */
	public IntPoint outerToInnerCoordinates(IntPoint outerCoordinates) {
		if (outerCoordinates == null)
			throw new IllegalArgumentException("outerCoordinates is null");
		
		return originalExtent.getTopLeft().plus(
				outerToInnerCoordinates(outerCoordinates.minus(currentExtent.getTopLeft())));
	}
	
	/**
	 * Returns the coordinates in this shape group's inner coordinate system of the vector whose coordinates
	 * in this shape group's outer coordinate system are the given coordinates.
	 * 
	 * @throws IllegalArgumentException if {@code relativeOuterCoordinates} is null
	 * @inspects | this
	 * @post | result != null
	 * @post | result.equals(new IntVector(
	 *       |     (int)((long)relativeOuterCoordinates.getX() * getOriginalExtent().getWidth() / getExtent().getWidth()),
	 *       |     (int)((long)relativeOuterCoordinates.getY() * getOriginalExtent().getHeight() / getExtent().getHeight())))
	 */
	public IntVector outerToInnerCoordinates(IntVector relativeOuterCoordinates) {
		if (relativeOuterCoordinates == null)
			throw new IllegalArgumentException("relativeOuterCoordinates is null");
		
		return new IntVector(
				(int)((long)relativeOuterCoordinates.getX() * originalExtent.getWidth() / currentExtent.getWidth()),
				(int)((long)relativeOuterCoordinates.getY() * originalExtent.getHeight() / currentExtent.getHeight()));
	}
	
	/**
	 * Returns the coordinates in this shape group's outer coordinate system of the point whose coordinates
	 * in the global coordinate system are the given coordinates.
	 * 
	 * @throws IllegalArgumentException if {@code globalCoordinates} is null
	 *    | globalCoordinates == null
	 * @inspects | this
	 * @post | result != null
	 * @post | result.equals(
	 *       |     getParentGroup() == null ?
	 *       |         globalCoordinates
	 *       |     :
	 *       |         getParentGroup().toInnerCoordinates(globalCoordinates)
	 *       | )
	 */
	public IntPoint globalToOuterCoordinates(IntPoint globalCoordinates) {
		if (globalCoordinates == null)
			throw new IllegalArgumentException("globalCoordinates is null");
		
		return parent == null ? globalCoordinates : parent.toInnerCoordinates(globalCoordinates);
	}
	
	/**
	 * Returns the coordinates in this shape group's outer coordinate system of the vector whose coordinates
	 * in this shape group's inner coordinate system are the given coordinates.
	 * 
	 * @throws IllegalArgumentException if {@code relativeInnerCoordinates} is null
	 *    | relativeInnerCoordinates == null
	 * @inspects | this
	 * @post | result != null
	 * @post | result.equals(new IntVector(
	 *       |     (int)((long)relativeInnerCoordinates.getX() * getExtent().getWidth() / getOriginalExtent().getWidth()),
	 *       |     (int)((long)relativeInnerCoordinates.getY() * getExtent().getHeight() / getOriginalExtent().getHeight())))
	 */
	public IntVector innerToOuterCoordinates(IntVector relativeInnerCoordinates) {
		if (relativeInnerCoordinates == null)
			throw new IllegalArgumentException("relativeInnerCoordinates is null");
		
		return new IntVector(
				(int)((long)relativeInnerCoordinates.getX() * currentExtent.getWidth() / originalExtent.getWidth()),
				(int)((long)relativeInnerCoordinates.getY() * currentExtent.getHeight() / originalExtent.getHeight()));
	}
	
	/**
	 * Returns the coordinates in this shape group's outer coordinate system of the point whose coordinates
	 * in this shape group's inner coordinate system are the given coordinates.
	 * 
	 * @throws IllegalArgumentException if {@code innerCoordinates} is null
	 *    | innerCoordinates == null
	 * @inspects | this
	 * @post | result != null
	 * @post | result.equals(getExtent().getTopLeft().plus(
	 *       |     innerToOuterCoordinates(innerCoordinates.minus(getOriginalExtent().getTopLeft()))))
	 */
	public IntPoint innerToOuterCoordinates(IntPoint innerCoordinates) {
		if (innerCoordinates == null)
			throw new IllegalArgumentException("innerCoordinates is null");
		
		return getExtent().getTopLeft().plus(
				innerToOuterCoordinates(innerCoordinates.minus(getOriginalExtent().getTopLeft())));
	}
	
	/**
	 * Returns the coordinates in the global coordinate system of the point whose coordinates
	 * in this shape group's outer coordinate system are the given coordinates.
	 *  
	 * @throws IllegalArgumentException if {@code outerCoordinates} is null
	 * @inspects | this
	 * @post | result != null
	 * @post | result.equals(
	 *       |     getParentGroup() == null ?
	 *       |         outerCoordinates
	 *       |     :
	 *       |         getParentGroup().toGlobalCoordinates(outerCoordinates)
	 *       | )
	 */
	public IntPoint outerToGlobalCoordinates(IntPoint outerCoordinates) {
		if (outerCoordinates == null)
			throw new IllegalArgumentException("outerCoordinates is null");
		
		return parent == null ? outerCoordinates : parent.toGlobalCoordinates(outerCoordinates);
	}
	
	/**
	 * Returns the coordinates in the global coordinate system of the point whose coordinates
	 * in this shape group's inner coordinate system are the given coordinates.
	 * 
	 * @throws IllegalArgumentException if {@code innerCoordinates} is null
	 * @inspects | this
	 * @post | result != null
	 * @post | result.equals(outerToGlobalCoordinates(innerToOuterCoordinates(innerCoordinates)))
	 */
	public IntPoint toGlobalCoordinates(IntPoint innerCoordinates) {
		if (innerCoordinates == null)
			throw new IllegalArgumentException("innerCoordinates is null");
		
		return outerToGlobalCoordinates(innerToOuterCoordinates(innerCoordinates));
	}
	
	/**
	 * Returns the coordinates in this shape group's outer coordinate system of the vector
	 * whose coordinates in the global coordinate system are the given coordinates.
	 * 
	 * @inspects | this
	 * @post | result != null
	 * @post | result.equals(
	 *       |     getParentGroup() == null ?
	 *       |         relativeGlobalCoordinates
	 *       |     :
	 *       |         getParentGroup().toInnerCoordinates(relativeGlobalCoordinates)
	 *       | )
	 */
	public IntVector globalToOuterCoordinates(IntVector relativeGlobalCoordinates) {
		if (relativeGlobalCoordinates == null)
			throw new IllegalArgumentException("relativeGlobalCoordinates is null");
		
		return parent == null ? relativeGlobalCoordinates : parent.toInnerCoordinates(relativeGlobalCoordinates);
	}
	
	/**
	 * Returns the coordinates in this shape group's inner coordinate system of the vector
	 * whose coordinates in the global coordinate system are the given coordinates.
	 * 
	 * This transformation is affected only by mutations of the width or height of this shape group's
	 * extent, not by mutations of this shape group's extent that preserve its width and height.
	 * 
	 * @inspects | this
	 * @post | result != null
	 * @post | result.equals(outerToInnerCoordinates(globalToOuterCoordinates(relativeGlobalCoordinates)))
	 */
	public IntVector toInnerCoordinates(IntVector relativeGlobalCoordinates) {
		if (relativeGlobalCoordinates == null)
			throw new IllegalArgumentException("relativeGlobalCoordinates is null");

		return outerToInnerCoordinates(globalToOuterCoordinates(relativeGlobalCoordinates));
	}
	
	/**
	 * Registers the given extent as this shape group's extent, expressed in this
	 * shape group's outer coordinate system. As a consequence, by definition this shape group's
	 * inner coordinate system changes so that the new extent's coordinates in the new inner
	 * coordinate system are equal to the old extent's coordinates in the old inner
	 * coordinate system. Note: this method does not mutate the coordinates of the vertices
	 * stored by the shape or the extents stored by the subgroups contained by this shape group.
	 * However, since these are interpreted in this shape group's inner coordinate system, this
	 * method effectively defines a transformation of this shape or these subgroups.
	 * 
	 * @throws IllegalArgumentException if {@code newExtent} is null
	 *    | newExtent == null
	 * @mutates_properties | getExtent()
	 * @post | getExtent().equals(newExtent)
	 */
	public void setExtent(Extent newExtent) {
		if (newExtent == null)
			throw new IllegalArgumentException("newExtent is null");
		
		currentExtent = newExtent;
	}
	
	void remove() {
		nextSibling.previousSibling = previousSibling;
		previousSibling.nextSibling = nextSibling;
		if (this == parent.firstChild)
			parent.firstChild = nextSibling;
	}
	
	void insertBeforeFirstSibling() {
		nextSibling = parent.firstChild;
		previousSibling = nextSibling.previousSibling;
		nextSibling.previousSibling = this;
		previousSibling.nextSibling = this;
	}
	
	/**
	 * Moves this shape group to the front of its parent's list of subgroups.
	 * 
	 * @throws UnsupportedOperationException if this shape group has no parent
	 *    | getParentGroup() == null
	 * @mutates_properties | getParentGroup().getSubgroups()
	 * @post | getParentGroup().getSubgroups().equals(
	 *       |     LogicalList.plusAt(LogicalList.minus(old(getParentGroup().getSubgroups()), this), 0, this))
	 */
	public void bringToFront() {
		if (parent == null)
			throw new UnsupportedOperationException("no parent");
		
		remove();
		insertBeforeFirstSibling();
		parent.firstChild = this;
	}
	
	/**
	 * Moves this shape group to the back of its parent's list of subgroups.
	 * 
	 * @throws UnsupportedOperationException if this shape group has no parent
	 *    | getParentGroup() == null
	 * @mutates_properties | getParentGroup().getSubgroups()
	 * @post | getParentGroup().getSubgroups().equals(
	 *       |     LogicalList.plus(LogicalList.minus(old(getParentGroup().getSubgroups()), this), this))
	 */
	public void sendToBack() {
		if (parent == null)
			throw new UnsupportedOperationException("no parent");
		
		remove();
		insertBeforeFirstSibling();
	}
	
	abstract String getInnerDrawingCommands();
	
	/**
	 * Returns a textual representation of a sequence of drawing commands for drawing
	 * the shapes contained directly or indirectly by this shape group, expressed in this
	 * shape group's outer coordinate system.
	 * 
	 * @inspects | this, ...getAllShapes()
	 * @post | result != null
	 */
	public String getDrawingCommands() {
		StringBuilder builder = new StringBuilder();
		
		double xscale = currentExtent.getWidth() * 1.0 / originalExtent.getWidth();
		double yscale = currentExtent.getHeight() * 1.0 / originalExtent.getHeight();
		builder.append("pushTranslate " + currentExtent.getLeft() + " " + currentExtent.getTop() + "\n");
		builder.append("pushScale " + xscale + " " + yscale + "\n");
		builder.append("pushTranslate " + -originalExtent.getLeft() + " " + -originalExtent.getTop() + "\n");
		
		builder.append(getInnerDrawingCommands());
		
		builder.append("popTransform popTransform popTransform\n");
		return builder.toString();
	}
}
