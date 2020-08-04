package drawit.shapes2;

import drawit.IntPoint;
import drawit.IntVector;
import drawit.RoundedPolygon;
import drawit.shapegroups2.ShapeGroup;

/**
 * Each instance of this class stores a reference to a RoundedPolygon object and, optionally,
 * a reference to a ShapeGroup object that contains it.
 * 
 * We define this object's shape coordinate system as the referenced ShapeGroup object's inner coordinate system,
 * if the stored ShapeGroup reference is non-null,
 * or the global coordinate system otherwise.
 * We interpret the polygon's vertex coordinates as being expressed in this object's shape coordinate system.
 */
public class RoundedPolygonShape extends ShapeImpl {

	private final ShapeGroup parent;
	private final RoundedPolygon polygon;
	
	/**
	 * Returns the {@code RoundedPolygon} reference stored by this object.
	 *  
	 * @immutable
	 */
	public RoundedPolygon getPolygon() {
		return polygon;
	}
	
	/**
	 * Returns whether this polygon contains this point, given in shape coordinates.
	 */
	public boolean contains(IntPoint p) {
		return polygon.contains(p);
	}
	
	/**
	 * Returns this polygon's drawing commands.
	 */
	public String getDrawingCommands() {
		return polygon.getDrawingCommands();
	}
	
	/**
	 * Returns the ShapeGroup reference stored by this object.
	 */
	public ShapeGroup getParent() {
		return parent;
	}
	
	/**
	 * Initializes this object to store the given ShapeGroup reference (or {@code null})
	 * and the given RoundedPolygon reference.
	 */
	public RoundedPolygonShape(ShapeGroup parent, RoundedPolygon polygon) {
		this.parent = parent;
		this.polygon = polygon;
	}
	
	/**
	 * Returns one control point for each of this polygon's vertices.
	 * 
	 * If, after calling this method, a client mutates either the polygon or the shape group graph referenced by this
	 * object, it shall no longer call any methods on the returned ControlPoint objects.
	 * 
	 * That is, any mutation of the polygon or the shape group graph referenced by this object invalidates the
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
		IntPoint[] vertices = polygon.getVertices();
		ControlPoint[] controlPoints = new ControlPoint[vertices.length];
		for (int i = 0; i < vertices.length; i++) {
			IntPoint originalVertex = vertices[i];
			int index = i;
			controlPoints[i] = new ControlPointImpl(originalVertex) {
				public void remove() {
					polygon.remove(index);
				}
				public void move(IntVector delta) {
					delta = toShapeCoordinates(delta);
					vertices[index] = originalVertex.plus(delta);
					polygon.setVertices(vertices);
				}
			};
		}
		return controlPoints;
	}
}
