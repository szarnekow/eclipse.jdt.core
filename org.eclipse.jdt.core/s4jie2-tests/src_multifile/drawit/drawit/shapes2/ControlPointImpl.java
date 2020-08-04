package drawit.shapes2;

import drawit.IntPoint;
import drawit.IntVector;

abstract class ControlPointImpl implements ControlPoint {

	private final IntPoint location;
	
	public IntPoint getLocation() {
		return location;
	}
	
	ControlPointImpl(IntPoint location) {
		this.location = location;
	}
	
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove this vertex");
	}
	
	public abstract void move(IntVector delta);

}
