package drawit.shapes1;

import drawit.IntPoint;
import drawit.shapegroups1.ShapeGroup;

/**
 * Interface that generalizes classes {@code RoundedPolygonShape} and {@code ShapeGroupShape}.
 */
public interface Shape {

	ShapeGroup getParent();

	boolean contains(IntPoint p);

	String getDrawingCommands();

	/**
	 * Given the coordinates of a point in the global coordinate system, returns
	 * the coordinates of the point in the shape coordinate system.
	 */
	IntPoint toShapeCoordinates(IntPoint p);

	/**
	 * Given the coordinates of a point in the shape coordinate system, returns
	 * the coordinates of the point in the global coordinate system.
	 */
	IntPoint toGlobalCoordinates(IntPoint p);

	ControlPoint[] createControlPoints();

}