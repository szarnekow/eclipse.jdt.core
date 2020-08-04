package drawit_officialtests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import drawit.FastRoundedPolygonContainsTestStrategy;
import drawit.IntPoint;
import drawit.PreciseRoundedPolygonContainsTestStrategy;
import drawit.RoundedPolygon;

import static drawit_officialtests.PointArraysTest.pt;
import static drawit_officialtests.PointArraysTest.samePoints;

class RoundedPolygonTest {
	
	RoundedPolygon poly = new RoundedPolygon();

	@Test
	void testGetters() {
		 // We did not specify an initial state; just check that the methods exist and do not crash.
		assert poly.getRadius() == poly.getRadius();
		IntPoint[] vertices = poly.getVertices();
		assert vertices != null && samePoints(vertices, poly.getVertices());
	}
	
	@Test
	void testSetRadius() {
		poly.setRadius(99);
		assert poly.getRadius() == 99;
	}
	
	@Test
	void testSetVertices_proper() {
		poly.setVertices(new IntPoint[] {pt(10, 10), pt(100, 10), pt(100, 100), pt(10, 100)});
		assert samePoints(new IntPoint[] {pt(10, 10), pt(100, 10), pt(100, 100), pt(10, 100)}, poly.getVertices());
	}

	@Test
	void testSetVertices_improper() {
		assertThrows(IllegalArgumentException.class, () ->
			poly.setVertices(new IntPoint[] {pt(100, 10), pt(10, 10), pt(100, 100), pt(10, 100)}));
	}

	@Test
	void testInsert_proper() {
		poly.setVertices(new IntPoint[] {pt(10, 10), pt(100, 10), pt(100, 100), pt(10, 100)});
		poly.insert(4, pt(10, 50));
		assert samePoints(new IntPoint[] {pt(10, 10), pt(100, 10), pt(100, 100), pt(10, 100), pt(10, 50)}, poly.getVertices());
	}
//
//	@Test
//	void testInsert_improper() {
//		assertThrows(IllegalArgumentException.class, () ->
//			poly.insert(4, pt(150, 50))
//		);
//	}
	
	@Test
	void testRemove_proper() {
		poly.setVertices(new IntPoint[] {pt(200, 100), pt(100, 200), pt(150, 300), pt(250, 300), pt(300, 200)});
		poly.remove(2);
		assert samePoints(new IntPoint[] {pt(200, 100), pt(100, 200), pt(250, 300), pt(300, 200)}, poly.getVertices());
	}
	
	@Test
	void testRemove_improper() {
		poly.setVertices(new IntPoint[] {pt(100, 100), pt(200, 200), pt(100, 150)});
		assertThrows(IllegalArgumentException.class, () ->
		 	poly.remove(2));
	}
	
	@Test
	void testUpdate_proper() {
		poly.setVertices(new IntPoint[] {pt(100, 100), pt(200, 200), pt(100, 150)});
		poly.update(2, pt(200, 150));
		assert samePoints(new IntPoint[] {pt(100, 100), pt(200, 200), pt(200, 150)}, poly.getVertices());
	}
	
	@Test
	void testUpdate_improper() {
		poly.setVertices(new IntPoint[] {pt(100, 100), pt(200, 200), pt(100, 150)});
		assertThrows(IllegalArgumentException.class, () ->
			poly.update(2, pt(150, 150)));
	}
	
	@Test
	void testContains_true_interior() {
		poly.setVertices(new IntPoint[] {pt(100, 100), pt(200, 200), pt(100, 150)});
		assert poly.contains(new IntPoint(105, 125));
		assert poly.contains(new IntPoint(124, 125));
		assert poly.contains(new IntPoint(174, 175));
		
		poly.setVertices(new IntPoint[] {pt(100, 100), pt(200, 200), pt(300, 100), pt(200, 400)});
		assert poly.contains(new IntPoint(150, 175));
		assert poly.contains(new IntPoint(250, 175));
		assert poly.contains(new IntPoint(200, 250));
	}
	
	@Test
	void testContains_true_on_edge() {
		poly.setVertices(new IntPoint[] {pt(100, 100), pt(200, 200), pt(100, 150)});
		assert poly.contains(new IntPoint(100, 125));
		assert poly.contains(new IntPoint(125, 125));
		assert poly.contains(new IntPoint(175, 175));
		
		poly.setVertices(new IntPoint[] {pt(100, 100), pt(200, 200), pt(300, 100), pt(200, 400)});
		assert poly.contains(new IntPoint(150, 150));
		assert poly.contains(new IntPoint(250, 150));
		assert poly.contains(new IntPoint(175, 325));
	}
	
	
	@Test
	void testContains_true_vertex() {
		poly.setVertices(new IntPoint[] {pt(100, 100), pt(200, 200), pt(100, 150)});
		assert poly.contains(new IntPoint(100, 100));
		assert poly.contains(new IntPoint(200, 200));
		assert poly.contains(new IntPoint(100, 150));
		
		poly.setVertices(new IntPoint[] {pt(100, 100), pt(200, 200), pt(300, 100), pt(200, 400)});
		assert poly.contains(new IntPoint(200, 200));
		assert poly.contains(new IntPoint(300, 100));
		assert poly.contains(new IntPoint(200, 400));
	}
	
	@Test
	void testContains_false() {
		poly.setVertices(new IntPoint[] {pt(100, 100), pt(200, 200), pt(100, 150)});
		assert !poly.contains(new IntPoint(200, 90));
		assert !poly.contains(new IntPoint(105, 100));
		assert !poly.contains(new IntPoint(90, 125));
		assert !poly.contains(new IntPoint(126, 125));
		assert !poly.contains(new IntPoint(175, 174));
		assert !poly.contains(new IntPoint(190, 210));
		
		poly.setVertices(new IntPoint[] {pt(100, 100), pt(200, 200), pt(300, 100), pt(200, 400)});
		assert !poly.contains(new IntPoint(190, 175));
		assert !poly.contains(new IntPoint(220, 175));
		assert !poly.contains(new IntPoint(105, 120));
		assert !poly.contains(new IntPoint(295, 120));
	}
	
	@Test
	void testFastRoundedPolygonContainsTestStrategy() {
		poly.setVertices(new IntPoint[] {pt(100, 100), pt(200, 200), pt(100, 150)});
		
		assert new FastRoundedPolygonContainsTestStrategy().contains(poly, pt(105, 125));
		assert new FastRoundedPolygonContainsTestStrategy().contains(poly, pt(110, 190));
		assert !new FastRoundedPolygonContainsTestStrategy().contains(poly, pt(90, 90));
	}
	
	@Test
	void testPreciseRoundedPolygonContainsTestStrategy() {
		poly.setVertices(new IntPoint[] {pt(100, 100), pt(200, 200), pt(100, 150)});

		assert new PreciseRoundedPolygonContainsTestStrategy().contains(poly, pt(105, 125));
		assert !new PreciseRoundedPolygonContainsTestStrategy().contains(poly, pt(110, 190));
		assert !new PreciseRoundedPolygonContainsTestStrategy().contains(poly, pt(90, 90));
	}

}
