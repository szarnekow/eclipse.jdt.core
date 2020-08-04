package drawit.shapes2;

import drawit.IntPoint;
import drawit.IntVector;
import drawit.shapegroups2.Extent;
import drawit.shapegroups2.ShapeGroup;

/**
 * Each instance of this class stores a reference to a ShapeGroup object.
 * 
 * We define this object's shape coordinate system as its shape group's outer coordinate system.
 */
public class ShapeGroupShape extends ShapeImpl {

	private final ShapeGroup group;
	
	/**
	 * Returns the ShapeGroup reference stored by this object.
	 * 
	 * @immutable
	 */
	public ShapeGroup getShapeGroup() {
		return group;
	}
	
	/**
	 * Returns this shape group's parent, or {@code null} if it has no parent.
	 * 
	 * @immutable
	 */
	public ShapeGroup getParent() {
		return group.getParentGroup();
	}
	
	/** Returns whether this shape group's extent contains the given point, expressed in shape coordinates. */
	public boolean contains(IntPoint p) {
		return group.getExtent().contains(p);
	}
	
	/** Returns this shape group's drawing commands. */
	public String getDrawingCommands() {
		return group.getDrawingCommands();
	}
	
	/** Initializes this object to store the given ShapeGroup reference. */
	public ShapeGroupShape(ShapeGroup group) {
		this.group = group;
	}
	
	/**
	 * Returns one control point for this shape group's upper-left corner, and one control point for
	 * its lower-right corner.
	 * 
	 * If, after calling this method, a client mutates the shape group graph referenced by this
	 * object, it shall no longer call any methods on the returned ControlPoint objects.
	 * 
	 * That is, any mutation of the shape group graph referenced by this object invalidates the
	 * ControlPoint objects returned by any preceding getControlPoints call. This is true even if the mutation occurred
	 * through the returned ControlPoint objects themselves. For example, after calling {@code move} on one of the
	 * returned ControlPoint objects, a client is
	 * no longer allowed to call {@code getLocation} or {@code remove} on any of the returned ControlPoint objects,
	 * and after calling {@code remove} on one of the returned ControlPoint objects, a client
	 * is no longer allowed to call {@code getLocation} or {@code move} on any of the returned ControlPoint objects.
	 * 
	 * There is one exception: a client can perform any number of consecutive {@code move} calls on the same ControlPoint
	 * object.
	 * 
	 * @creates
	 *    This method creates the returned array, as well as its elements.
	 *    | result, ...result
	 */
	public ControlPoint[] createControlPoints() {
		Extent extent = group.getExtent();
		return new ControlPoint[] {
			new ControlPointImpl(new IntPoint(extent.getLeft(), extent.getTop())) {
				public void move(IntVector delta) {
					delta = toShapeCoordinates(delta);
					group.setExtent(
							extent
							.withLeft(extent.getLeft() + delta.getX())
							.withTop(extent.getTop() + delta.getY()));
				}
			},
			new ControlPointImpl(new IntPoint(extent.getRight(), extent.getBottom())) {
				public void move(IntVector delta) {
					delta = toShapeCoordinates(delta);
					group.setExtent(
							extent
							.withRight(extent.getRight() + delta.getX())
							.withBottom(extent.getBottom() + delta.getY()));
				}
			}
		};
	}
}
