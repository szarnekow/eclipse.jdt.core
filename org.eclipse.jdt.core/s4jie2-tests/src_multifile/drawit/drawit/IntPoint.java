package drawit;

import java.util.Objects;

/**
 * An immutable abstraction for a point in the two-dimensional plane with {@code int} coordinates.
 * 
 * @immutable
 */
public class IntPoint {
	
	private final int x;
	private final int y;

	/** Returns this point's X coordinate. */
	public int getX() { return x; }
	/** Returns this point's Y coordinate. */
	public int getY() { return y; }
	
	/** Returns {@code true} if this point has the same coordinates as the given point; returns {@code false} otherwise.
	 * 
	 * @pre | other != null
	 * @post | result == (this.getX() == other.getX() && this.getY() == other.getY()) 
	 */
	public boolean equals(IntPoint other) {
		return other.x == x && other.y == y;
	}
	
	/**
	 * Returns a number that depends only on this point's coordinates.
	 * 
	 * @post | result == 31 * (31 + getX()) + getY() 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	
	/**
	 * Returns {@code true} if the given object's class equals this object's class and
	 * this point has the same coordinates as the given point.
	 * 
	 * @post | result == (object != null && object.getClass() == this.getClass() && this.equals((IntPoint)object))
	 */
	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;
		IntPoint other = (IntPoint) object;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	/**
	 * Returns a textual representation of this object.
	 * 
	 * @post | Objects.equals(result, "IntPoint [x=" + getX() + ", y=" + getY() + "]")
	 */
	@Override
	public String toString() {
		return "IntPoint [x=" + x + ", y=" + y + "]";
	}
	
	/** Initializes this point with the given coordinates.
	 * 
	 * @mutates | this
	 * @post | getX() == x
	 * @post | getY() == y
	 */
	public IntPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/** Returns an {@code IntVector} object representing the displacement from {@code other} to {@code this}.
	 * 
	 * @pre | other != null
	 * @post | result != null
	 * @post | result.getX() == this.getX() - other.getX()
	 * @post | result.getY() == this.getY() - other.getY()
	 */ 
	public IntVector minus(IntPoint other) {
		return new IntVector(x - other.x, y - other.y);
	}
	
	/**
	 * Returns true iff this point is on open line segment {@code bc}.
	 * An open line segment does not include its endpoints.
	 * 
	 * <p><b>Implementation hints:</b> Call this point {@code a}. First check if {@code ba} is collinear with {@code bc}. If not, return {@code false}.
	 * Then check that the dot product of {@code ba} and {@code bc} is between zero and the dot product of {@code bc} and {@code bc}.
	 * 
	 * @pre | b != null
	 * @pre | 0 <= b.getX() && b.getX() <= 10000 && 0 <= b.getY() && b.getY() <= 10000 
	 * @pre | c != null
	 * @pre | 0 <= c.getX() && c.getX() <= 10000 && 0 <= c.getY() && c.getY() <= 10000
	 * @post
	 *    | result == (
	 *    |   this.minus(b).isCollinearWith(c.minus(b)) &&
	 *    |   0 < this.minus(b).dotProduct(c.minus(b)) &&
	 *    |   this.minus(b).dotProduct(c.minus(b)) < c.minus(b).dotProduct(c.minus(b))
	 *    | ) 
	 */
	public boolean isOnLineSegment(IntPoint b, IntPoint c) {
		IntPoint a = this;
		// Is it on the carrier?
		IntVector bc = c.minus(b);
		IntVector ba = a.minus(b);
		if (!ba.isCollinearWith(bc))
			return false;
		long dotProduct = ba.dotProduct(bc);
		return 0 < dotProduct && dotProduct < bc.dotProduct(bc);
	}
	
	/**
	 * Returns a {@code DoublePoint} object that represents the same 2D point represented by this {@code IntPoint} object.
	 * 
	 * @post | result != null
	 * @post | result.getX() == this.getX()
	 * @post | result.getY() == this.getY()
	 */
	public DoublePoint asDoublePoint() {
		return new DoublePoint(this.x, this.y);
	}

	/** Returns an {@code IntPoint} object representing the point obtained by displacing this point by the given vector.
	 * 
	 * @pre | vector != null
	 * @post | result != null
	 * @post | result.getX() == this.getX() + vector.getX()
	 * @post | result.getY() == this.getY() + vector.getY() 
	 */
	public IntPoint plus(IntVector vector) {
		return new IntPoint(this.x + vector.getX(), this.y + vector.getY());
	}
	
	/**
	 * Returns true iff the open line segment {@code ab} intersects the open line segment {@code cd}.
	 * 
	 * <p><b>Implementation Hints:</b> Assume the precondition holds. Then {@code ab} intersects {@code cd} if and only if {@code ab} straddles the carrier of {@code cd} and
	 * {@code cd} straddles the carrier of {@code ab}. Two points straddle a line if they are on opposite sides of the line. 
	 * 
	 * <p>Specifically, {@code cd} straddles the carrier of {@code ab} iff (the signum of the cross product of {@code ac} and {@code ab}) times
     * (the signum of the cross product of {@code ad} and {@code ab}) is negative. 
     * 
     * <p>The signum of a number {@code x} is -1 if {@code x} is negative, 0 if {@code x} is zero, and {@code 1} otherwise. See {@link Math#signum(double)}.
	 * 
	 * @pre | a != null
	 * @pre | b != null
	 * @pre | c != null
	 * @pre | d != null
	 * @pre The line segments have at most one point in common.
	 */
	public static boolean lineSegmentsIntersect(IntPoint a, IntPoint b, IntPoint c, IntPoint d) {
		// Check if cd straddles the carrier of ab and ab straddles the carrier of cd
		IntVector ab = b.minus(a);
		if (Math.signum(c.minus(a).crossProduct(ab)) * Math.signum(d.minus(a).crossProduct(ab)) < 0) {
			// cd straddles the carrier of ab
			
			IntVector cd = d.minus(c);
			return Math.signum(a.minus(c).crossProduct(cd)) * Math.signum(b.minus(c).crossProduct(cd)) < 0;
			
		} else
			return false;
	}
	
}
