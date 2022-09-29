package drawit;

import java.awt.Color;
import java.util.Arrays;

import drawit.shapegroups1.Extent;

/**
 * An instance of this class is a mutable abstraction storing a rounded polygon defined by a set of 2D points with integer coordinates
 * and a nonnegative corner radius.
 * 
 * @invar | getVertices() != null
 * @invar | Arrays.stream(getVertices()).allMatch(v -> v != null)
 * @invar | PointArrays.checkDefinesProperPolygon(getVertices()) == null
 * @invar | 0 <= getRadius()
 * @invar | getColor() != null
 */
public class RoundedPolygon {
	
	/**
	 * @representationObject
	 * @invar | vertices != null
	 * @invar | Arrays.stream(vertices).allMatch(v -> v != null)
	 * @invar | PointArrays.checkDefinesProperPolygon(vertices) == null
	 * @invar | 0 <= radius
	 * @invar | color != null
	 * @invar | boundingBox != null
	 * @invar | PointArrays.isBoundingBoxFor(boundingBox, vertices)
	 */
	private IntPoint[] vertices = new IntPoint[0];
	private int radius;
	private Color color = Color.yellow;
	private Extent boundingBox = Extent.ofLeftTopRightBottom(0, 0, 1, 1);
	
	/**
	 * Returns a new array whose elements are the vertices of this rounded polygon.
	 * 
	 * @creates | result
	 */
	public IntPoint[] getVertices() {
		return PointArrays.copy(vertices);
	}
	
	/**
	 * Returns the radius of the corners of this rounded polygon.
	 */
	public int getRadius() { return radius; }
	
	/**
	 * Returns this rounded polygon's color.
	 */
	public Color getColor() { return color; }

	/**
	 * Returns a smallest extent containing this rounded polygon's vertices.
	 * 
	 * @post | result != null
	 * @post | PointArrays.isBoundingBoxFor(result, getVertices())
	 */
	public Extent getBoundingBox() { return boundingBox; }
	
	/**
	 * Initializes this rounded polygon with an empty list of vertices, a radius of zero, and color yellow.
	 * 
	 * @mutates | this
	 * @post | getVertices().length == 0
	 * @post | getRadius() == 0
	 * @post | getColor().equals(Color.yellow)
	 */
	public RoundedPolygon() {}
	
	/**
	 * Sets the vertices of this rounded polygon to be equal to the elements of the given array.
	 * 
	 * @inspects | newVertices
	 * @mutates | this
	 * @throws IllegalArgumentException | newVertices == null
	 * @throws IllegalArgumentException | Arrays.stream(newVertices).anyMatch(v -> v == null)
	 * @throws IllegalArgumentException if the given vertices do not define a proper polygon.
	 *     | PointArrays.checkDefinesProperPolygon(newVertices) != null
	 * @post | Arrays.equals(getVertices(), newVertices)
	 * @post | getRadius() == old(getRadius())
	 * @post | getColor().equals(old(getColor()))
	 */
	public void setVertices(IntPoint[] newVertices) {
		if (newVertices == null)
			throw new IllegalArgumentException("newVertices is null");
		if (Arrays.stream(newVertices).anyMatch(v -> v == null))
			throw new IllegalArgumentException("An element of newVertices is null");
		IntPoint[] copy = PointArrays.copy(newVertices);
		String msg = PointArrays.checkDefinesProperPolygon(copy);
		if (msg != null)
			throw new IllegalArgumentException(msg);
		vertices = copy;
		boundingBox = PointArrays.getBoundingBoxFor(vertices);
	}
	
	/**
	 * Sets this rounded polygon's corner radius to the given value. 
	 * 
	 * @throws IllegalArgumentException if the given radius is negative.
	 *    | radius < 0
	 * @mutates | this
	 * @post | Arrays.equals(getVertices(), old(getVertices()))
	 * @post | getRadius() == radius
	 * @post | getColor().equals(old(getColor()))
	 */
	public void setRadius(int radius) {
		if (radius < 0)
			throw new IllegalArgumentException("The given radius is negative");
		this.radius = radius;
	}
	
	/**
	 * Sets this rounded polygon's color to the given color.
	 * 
	 * @throws IllegalArgumentException if {@code color} is null
	 *    | color == null
	 * @mutates | this
	 * @post | Arrays.equals(getVertices(), old(getVertices()))
	 * @post | getRadius() == old(getRadius())
	 * @post | getColor().equals(color)
	 */
	public void setColor(Color color) {
		if (color == null)
			throw new IllegalArgumentException("color is null");
		
		this.color = color;
	}
	
	/**
	 * @throws IllegalArgumentException | !(0 <= index && index <= getVertices().length)
	 * @throws IllegalArgumentException | point == null
	 * @throws IllegalArgumentException | PointArrays.checkDefinesProperPolygon(PointArrays.insert(getVertices(), index, point)) != null
	 * @mutates | this
	 * @post | Arrays.equals(getVertices(), PointArrays.insert(old(getVertices()), index, point))
	 * @post | getRadius() == old(getRadius())
	 * @post | getColor().equals(old(getColor()))
	 */
	public void insert(int index, IntPoint point) {
		if (!(0 <= index && index <= getVertices().length))
			throw new IllegalArgumentException("index out of range");
		if (point == null)
			throw new IllegalArgumentException("point is null");
		setVertices(PointArrays.insert(vertices, index, point));
	}
	
	/**
	 * @throws IllegalArgumentException | !(0 <= index && index < getVertices().length)
	 * @throws IllegalArgumentException | PointArrays.checkDefinesProperPolygon(PointArrays.remove(getVertices(), index)) != null
	 * @mutates | this
	 * @post | Arrays.equals(getVertices(), PointArrays.remove(old(getVertices()), index))
	 * @post | getRadius() == old(getRadius())
	 * @post | getColor().equals(old(getColor()))
	 */
	public void remove(int index) {
		if (!(0 <= index && index < getVertices().length))
			throw new IllegalArgumentException("index out of range");
		setVertices(PointArrays.remove(vertices, index));
	}
	
	/**
	 * @throws IllegalArgumentException | !(0 <= index && index < getVertices().length)
	 * @throws IllegalArgumentException | point == null
	 * @throws IllegalArgumentException | PointArrays.checkDefinesProperPolygon(PointArrays.update(getVertices(), index, point)) != null
	 * @mutates | this
	 * @post | Arrays.equals(getVertices(), PointArrays.update(old(getVertices()), index, point))
	 * @post | getRadius() == old(getRadius())
	 * @post | getColor().equals(old(getColor()))
	 */
	public void update(int index, IntPoint point) {
		if (!(0 <= index && index < getVertices().length))
			throw new IllegalArgumentException("index out of range");
		if (point == null)
			throw new IllegalArgumentException("point is null");
		setVertices(PointArrays.update(vertices, index, point));
	}
	
	/**
	 * Returns {@code true} iff the given point is contained by the (non-rounded) polygon defined by this rounded polygon's vertices.
	 * This method does not take into account this rounded polygon's corner radius; it assumes a corner radius of zero.
	 * 
	 * <p>A point is contained by a polygon if it coincides with one of its vertices, or if it is on one of its edges, or if it is in the polygon's interior.
	 * 
	 * @pre | point != null
	 * @inspects | this
	 * @mutates nothing |
	 */
	public boolean contains(IntPoint point) {
		// We call the half-line extending from `point` to the right the "exit path"
		// Find first vertex that is not on the exit path
		int firstVertex;
		{
			int i = 0;
			for (;;) {
				if (i == vertices.length) // Zero or one vertices
					return false;
				if (vertices[i].equals(point))
					return true;
				if (!(vertices[i].getY() == point.getY() && vertices[i].getX() > point.getX()))
					break;
				i++;
			}
			firstVertex = i;
		}
		IntVector exitVector = new IntVector(1, 0);
		// Count how many times the exit path crosses the polygon
		int nbEdgeCrossings = 0;
		for (int index = firstVertex; ; ) {
			IntPoint a = vertices[index];
			// Find the next vertex that is not on the exit path
			boolean onExitPath = false;
			int nextIndex = index;
			IntPoint b;
			for (;;) {
				int nextNextIndex = (nextIndex + 1) % vertices.length;
				if (point.isOnLineSegment(vertices[nextIndex], vertices[nextNextIndex]))
					return true;
				nextIndex = nextNextIndex;
				b = vertices[nextIndex];
				if (b.equals(point))
					return true;
				if (b.getY() == point.getY() && b.getX() > point.getX()) {
					onExitPath = true;
					continue;
				}
				break;
			}
			if (onExitPath) {
				if ((b.getY() < point.getY()) != (a.getY() < point.getY()))
					nbEdgeCrossings++;
			} else {
				// Does `ab` straddle the exit path's carrier?
				if (Math.signum(a.getY() - point.getY()) * Math.signum(b.getY() - point.getY()) < 0) {
					// Does the exit path straddle `ab`'s carrier?
					IntVector ab = b.minus(a);
					if (Math.signum(point.minus(a).crossProduct(ab)) * Math.signum(exitVector.crossProduct(ab)) < 0)
						nbEdgeCrossings++;
				}
			}
			if (nextIndex == firstVertex)
				break;
			index = nextIndex;
		}
		return nbEdgeCrossings % 2 == 1;
	}
		
	/**
	 * Returns a textual representation of a set of drawing commands for drawing this rounded polygon.
	 * 
	 * @inspects | this
	 * @mutates nothing |
	 * @post | result != null
	 */
	public String getDrawingCommands() {
		if (vertices.length < 3)
			return "";
		StringBuilder commands = new StringBuilder();
		for (int index = 0; index < vertices.length; index++) {
			IntPoint a = vertices[(index + vertices.length - 1) % vertices.length];
			IntPoint b = vertices[index];
			IntPoint c = vertices[(index + 1) % vertices.length];
			DoubleVector ba = a.minus(b).asDoubleVector();
			DoubleVector bc = c.minus(b).asDoubleVector();
			DoublePoint baCenter = b.asDoublePoint().plus(ba.scale(0.5));
			DoublePoint bcCenter = b.asDoublePoint().plus(bc.scale(0.5));
			double baSize = ba.getSize();
			double bcSize = bc.getSize();
			if (ba.crossProduct(bc) == 0) {
				commands.append("line " + bcCenter.getX() + " " + bcCenter.getY() + " " + b.getX() + " " + b.getY() + "\n");
				commands.append("line " + b.getX() + " " + b.getY() + " " + baCenter.getX() + " " + baCenter.getY() + "\n");
			} else {
				DoubleVector baUnit = ba.scale(1/baSize);
				DoubleVector bcUnit = bc.scale(1/bcSize);
				DoubleVector bisector = baUnit.plus(bcUnit);
				bisector = bisector.scale(1/bisector.getSize());
				double unitEdgeDistance = baUnit.dotProduct(bisector);
				double unitRadius = Math.abs(bisector.crossProduct(baUnit));
				double scaleFactor = Math.min(this.radius / unitRadius, Math.min(baSize, bcSize) / 2 / unitEdgeDistance);
				DoublePoint center = b.asDoublePoint().plus(bisector.scale(scaleFactor));
				double radius = unitRadius * scaleFactor;
				DoublePoint bcCornerStart = b.asDoublePoint().plus(bcUnit.scale(unitEdgeDistance * scaleFactor));
				DoublePoint baCornerStart = b.asDoublePoint().plus(baUnit.scale(unitEdgeDistance * scaleFactor));
				double baAngle = baCornerStart.minus(center).asAngle();
				double bcAngle = bcCornerStart.minus(center).asAngle();
				double angleExtent = bcAngle - baAngle;
				if (angleExtent < -Math.PI)
					angleExtent += 2 * Math.PI;
				else if (Math.PI < angleExtent)
					angleExtent -= 2 * Math.PI;
				commands.append("line " + baCenter.getX() + " " + baCenter.getY() + " " + baCornerStart.getX() + " " + baCornerStart.getY() + "\n");
				commands.append("arc " + center.getX() + " " + center.getY() + " " + radius + " " + baAngle + " " + angleExtent + "\n");
				commands.append("line " + bcCornerStart.getX() + " " + bcCornerStart.getY() + " " + bcCenter.getX() + " " + bcCenter.getY() + "\n");
			}
		}
		commands.append("fill " + color.getRed() + " " + color.getGreen() + " " + color.getBlue() + "\n");
		return commands.toString();
	}
	
}
