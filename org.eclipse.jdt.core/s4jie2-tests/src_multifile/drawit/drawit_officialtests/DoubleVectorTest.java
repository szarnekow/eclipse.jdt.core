package drawit_officialtests;

import org.junit.jupiter.api.Test;

import drawit.DoubleVector;

class DoubleVectorTest {
	
	DoubleVector v = new DoubleVector(10.25, -7.5);

	@Test
	void testConstructorAndGetters() {
		assert v.getX() == 10.25;
		assert v.getY() == -7.5;
	}
	
	static boolean isSimilar(double a, double b) {
		return Math.abs(a - b) < 1e-5;
	}
	
	@Test
	void testAsAngle() {
		assert isSimilar(new DoubleVector(1, 0).asAngle(), 0);
		assert isSimilar(new DoubleVector(0, 1).asAngle(), Math.PI/2);
		assert isSimilar(Math.abs(new DoubleVector(-1, 0).asAngle()), Math.PI);
		assert isSimilar(new DoubleVector(0, -1).asAngle(), -Math.PI/2);
		assert isSimilar(new DoubleVector(1, 1).asAngle(), Math.PI/4);
	}
	
	@Test
	void testCrossProduct() {
		assert isSimilar(v.crossProduct(v), 0);
		assert isSimilar(v.crossProduct(new DoubleVector(1, 0)), 7.5);
		assert isSimilar(v.crossProduct(new DoubleVector(0, 1)), 10.25);
		assert isSimilar(v.crossProduct(new DoubleVector(11, 13)), 10.25 * 13 + 7.5 * 11);
	}
	
	@Test
	void testDotProduct() {
		assert isSimilar(v.dotProduct(new DoubleVector(11, 13)), 10.25 * 11 - 7.5 * 13);
	}
	
	@Test
	void testGetSize() {
		assert isSimilar(v.getSize(), Math.sqrt(10.25 * 10.25 + 7.5 * 7.5));
	}
	
	@Test
	void testPlus() {
		DoubleVector w = v.plus(new DoubleVector(-33.125, 77.75));
		assert isSimilar(w.getX(), 10.25 - 33.125);
		assert isSimilar(w.getY(), -7.5 + 77.75);
	}
	
	@Test
	void testScale() {
		DoubleVector w = v.scale(-2.5);
		assert isSimilar(w.getX(), -2.5 * 10.25);
		assert isSimilar(w.getY(), -2.5 * -7.5);
	}

}
