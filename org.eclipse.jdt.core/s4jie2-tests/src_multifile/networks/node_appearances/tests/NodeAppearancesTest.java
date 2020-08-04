package node_appearances.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import node_appearances.CircularNodeAppearance;
import node_appearances.SquareNodeAppearance;

class NodeAppearancesTest {

	@Test
	void test() {
		SquareNodeAppearance a1 = new SquareNodeAppearance(Color.red, 5);
		assertEquals(Color.red, a1.getColor());
		assertEquals(5, a1.getWidth());
		
		assertEquals(a1, new SquareNodeAppearance(Color.red, 5));
		assertNotEquals(a1, new SquareNodeAppearance(Color.green, 5));
		assertNotEquals(a1, new SquareNodeAppearance(Color.red, 7));
		
		// Test hashCode()
		assertEquals(a1.hashCode(), new SquareNodeAppearance(Color.red, 5).hashCode());
		
		CircularNodeAppearance a2 = new CircularNodeAppearance(Color.blue, 9);
		assertEquals(Color.blue, a2.getColor());
		assertEquals(9, a2.getRadius());
		
		assertEquals(a2, new CircularNodeAppearance(Color.blue, 9));
		assertNotEquals(a2, new CircularNodeAppearance(Color.cyan, 9));
		assertNotEquals(a2, new CircularNodeAppearance(Color.blue, 11));
		
		// Test hashCode()
		assertEquals(a2.hashCode(), new CircularNodeAppearance(Color.blue, 9).hashCode());
		
		assertNotEquals(a1, a2);
		assertNotEquals(a2, a1);
	}

}
