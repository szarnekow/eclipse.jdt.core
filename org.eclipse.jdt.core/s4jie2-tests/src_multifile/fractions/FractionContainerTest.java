import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FractionContainerTest {
	
	FractionContainer container = new FractionContainer();

	@Test
	void testEquals() {
		assertTrue(container.equals(0, 1));
		assertTrue(container.equals(0, 2));
		assertFalse(container.equals(1, 2));
		
		container.set(42, 7);
		assertTrue(container.equals(12, 2));
		assertTrue(container.equals(-12, -2));
		assertFalse(container.equals(12, -2));
		assertFalse(container.equals(41, 7));
		
		container.set(37, -74);
		assertTrue(container.equals(-1, 2));
		assertTrue(container.equals(3, -6));
		assertFalse(container.equals(3, 6));
		assertFalse(container.equals(74, 37));
	}
	
	@Test
	void testAdd() {
		container.add(3, 7);
		container.add(4, 14);
		assertTrue(container.equals(5, 7));
	}
	
	@Test
	void testFinancial() {
		for (int i = 0; i < 10; i++)
			container.add(10, 100);
		assertTrue(container.equals(100, 100));
	}

}
