package drawit;

public class DoublePoint {
	
	private final double x;
	private final double y;
	
	public DoublePoint(double x, double y) {
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
		DoublePoint other = (DoublePoint) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DoublePoint [x=" + x + ", y=" + y + "]";
	}

	public DoublePoint plus(DoubleVector other) {
		return new DoublePoint(this.x + other.getX(), this.y + other.getY());
	}

	public DoubleVector minus(DoublePoint other) {
		return new DoubleVector(this.x - other.x, this.y - other.y);
	}

	/**
	 * Returns an {@code IntPoint} object whose coordinates are obtained by rounding the coordinates of this point to the nearest integer.
	 */
	public IntPoint round() {
		return new IntPoint((int)Math.round(this.x), (int)Math.round(this.y));
	}

}
