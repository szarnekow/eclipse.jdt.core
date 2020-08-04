package drawit.shapes1;

import drawit.IntPoint;
import drawit.IntVector;
import drawit.shapegroups1.ShapeGroup;

abstract class ShapeImpl implements Shape {
	
	public abstract ShapeGroup getParent();
	
	public abstract boolean contains(IntPoint p);
	
	public abstract String getDrawingCommands();
	
	public IntPoint toShapeCoordinates(IntPoint p) {
		if (getParent() == null)
			return p;
		return getParent().toInnerCoordinates(p);
	}
	
	public IntPoint toGlobalCoordinates(IntPoint p) {
		if (getParent() == null)
			return p;
		return getParent().toGlobalCoordinates(p);
	}
	
	IntVector toShapeCoordinates(IntVector v) {
		if (getParent() == null)
			return v;
		return getParent().toInnerCoordinates(v);
	}
	
	public abstract ControlPoint[] createControlPoints();
	
}
