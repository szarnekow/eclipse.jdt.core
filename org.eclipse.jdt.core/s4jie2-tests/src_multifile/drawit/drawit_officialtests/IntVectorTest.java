package drawit_officialtests;

import org.junit.jupiter.api.Test;

import drawit.IntVector;

class IntVectorTest {

	IntVector v = new IntVector(3, -9);
	
	@Test
	void testConstructorAndGetters() {
		assert 3 == v.getX();
		assert -9 == v.getY();
	}
	
	@Test
	void testAsDoubleVector() {
		assert 3.0 == v.asDoubleVector().getX();
		assert -9.0 == v.asDoubleVector().getY();
	}
	
	@Test
	void testCrossProduct() {
		assert v.crossProduct(v) == 0;
		assert v.crossProduct(new IntVector(1, 0)) == 9;
		assert v.crossProduct(new IntVector(0, 1)) == 3;
		assert v.crossProduct(new IntVector(2, 5)) == 3*5 + 9*2;
		assert v.getX() == 3;
		assert v.getY() == -9;
	}
	
	@Test
	void testDotProduct() {
		assert v.dotProduct(v) == 3*3 + 9*9;
		assert v.dotProduct(new IntVector(9, 3)) == 0;
		assert v.dotProduct(new IntVector(-4, -11)) == -12 + 99;
	}
	
	@Test
	void testIsCollinearWith() {
		assert v.isCollinearWith(new IntVector(1, -3));
		assert v.isCollinearWith(new IntVector(-1, 3));
		assert !v.isCollinearWith(new IntVector(-1, -3));
	}

}
