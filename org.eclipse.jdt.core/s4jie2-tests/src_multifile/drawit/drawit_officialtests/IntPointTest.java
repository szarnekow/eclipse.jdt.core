package drawit_officialtests;

import org.junit.jupiter.api.Test;

import drawit.IntPoint;
import drawit.IntVector;

class IntPointTest {
	
	IntPoint p = new IntPoint(5, 7);

	@Test
	void testConstructorAndGetters() {
		assert 5 == p.getX();
		assert 7 == p.getY();
	}
	
	@Test
	void testAsDoublePoint() {
		assert 5.0 == p.asDoublePoint().getX();
		assert 7.0 == p.asDoublePoint().getY();
	}
	
	@Test
	void testEquals() {
		assert p.equals(new IntPoint(5, 7));
		assert !p.equals(new IntPoint(7, 5));
	}
	
	@Test
	void testIsOnLineSegment() {
		assert p.isOnLineSegment(new IntPoint(5, 3), new IntPoint(5, 9));
		assert p.isOnLineSegment(new IntPoint(2, 4), new IntPoint(8, 10));
		assert p.isOnLineSegment(new IntPoint(8, 4), new IntPoint(2, 10));
		assert !p.isOnLineSegment(new IntPoint(7, 4), new IntPoint(1, 10));
	}
	
	@Test
	void testMinus() {
		IntVector v = p.minus(new IntPoint(6, 6));
		assert v.getX() == -1;
		assert v.getY() == 1;
		assert p.getX() == 5;
		assert p.getY() == 7;
	}
	
	@Test
	void testPlus() {
		assert new IntPoint(55, 77).equals(p.plus(new IntVector(50, 70)));
		assert p.getX() == 5;
		assert p.getY() == 7;
	}
	
	@Test
	void testLineSegmentsIntersect() {
		IntPoint q = new IntPoint(7, 5);
		IntPoint r = new IntPoint(5, 5);
		IntPoint s = new IntPoint(7, 7);
		assert IntPoint.lineSegmentsIntersect(p, q, r, s);
		assert IntPoint.lineSegmentsIntersect(s, r, q, p);
		assert IntPoint.lineSegmentsIntersect(r, s, p, q);
		assert !IntPoint.lineSegmentsIntersect(p, r, q, s);
	}
	
}
