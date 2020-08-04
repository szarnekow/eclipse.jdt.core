package drawit;

public class DoubleVector {
	
	private final double x;
	private final double y;
	
	public DoubleVector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoubleVector other = (DoubleVector) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DoubleVector [x=" + x + ", y=" + y + "]";
	}

	/** Returns a {@code DoubleVector} object whose coordinates are obtained by multiplying this vector's coordinates by the given scale factor. */
	public DoubleVector scale(double d) {
		return new DoubleVector(this.x * d, this.y * d);
	}

	public DoubleVector plus(DoubleVector other) {
		return new DoubleVector(this.x + other.x, this.y + other.y);
	}

	public double getSize() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	/**
	 * Returns the dot product of this vector and the given vector.
	 * 
	 * @post | result == this.getX() * other.getX() + this.getY() * other.getY()
	 */
	public double dotProduct(DoubleVector other) {
		return this.x * other.x + this.y * other.y;
	}

	/**
	 * Returns the cross product of this vector and the given vector.
	 * 
	 * @post | result == this.getX() * other.getY() - this.getY() * other.getX()
	 */
	public double crossProduct(DoubleVector other) {
		return this.x * other.y - this.y * other.x;
	}
	
	/**
	 * Returns the angle from positive X to this vector, in radians.
	 * 
	 * The angle from positive X to positive Y is {@code Math.PI / 2}; the angle from positive X to negative Y is {@code -Math.PI / 2}.
	 * 
	 * <p><b>Implementation Hint:</b> See {@link Math#atan2(double, double)}.
	 */
	public double asAngle() {
		return Math.atan2(y, x);
	}

}
