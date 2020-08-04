package drawit_officialtests;

import org.junit.jupiter.api.Test;

import drawit.DoublePoint;
import drawit.DoubleVector;
import drawit.IntPoint;

class DoublePointTest {
	
	DoublePoint p = new DoublePoint(10.5, 6.25);

	@Test
	void testConstructorAndGetters() {
		assert p.getX() == 10.5;
		assert p.getY() == 6.25;
	}
	
	@Test
	void testPlus() {
		DoublePoint q = p.plus(new DoubleVector(-9.75, 3.125));
		assert q.getX() == 10.5 - 9.75;
		assert q.getY() == 6.25 + 3.125;
	}
	
	@Test
	void testMinus() {
		DoubleVector q = p.minus(new DoublePoint(9.75, 3.125));
		assert q.getX() == 10.5 - 9.75;
		assert q.getY() == 6.25 - 3.125;
	}
	
	@Test
	void testRound() {
		IntPoint q = new DoublePoint(1.25, 9.75).round();
		assert q.getX() == 1;
		assert q.getY() == 10;
	}

}
