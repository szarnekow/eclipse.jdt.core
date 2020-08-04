package drawit_officialtests.shapegroups2;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import drawit.IntPoint;
import drawit.IntVector;
import drawit.RoundedPolygon;
import drawit.shapegroups2.LeafShapeGroup;
import drawit.shapegroups2.Extent;

class ShapeGroupTest_LeavesOnly_NoSetExtent {
	
	static IntPoint p(int x, int y) { return new IntPoint(x, y); }
	
	static IntPoint[] scale(int sx, int sy, IntPoint[] ps) {
		return Arrays.stream(ps).map(p -> new IntPoint(p.getX() * sx, p.getY() * sy)).toArray(n -> new IntPoint[n]);
	}
	
	static IntPoint[] translate(int dx, int dy, IntPoint[] ps) {
		IntVector v = new IntVector(dx, dy);
		return Arrays.stream(ps).map(p -> p.plus(v)).toArray(n -> new IntPoint[n]);
	}
	
	IntPoint[] triangleRight = {p(-1, -1), p(-1, 1), p(1, 0)};
	IntPoint[] diamond = {p(-1, 0), p(0, -1), p(1, 0), p(0, 1)};
	IntPoint[] pentagon = {p(-1, -1), p(-2, 0), p(0, 1), p(2, 0), p(1, -1)};
	
	IntPoint[] vertices1 = translate(400, 250, scale(10, 10, triangleRight));
	RoundedPolygon poly1 = new RoundedPolygon();
	{
		poly1.setVertices(vertices1);
	}
	LeafShapeGroup leaf1 = new LeafShapeGroup(poly1);
	
	IntPoint[] vertices2 = translate(400, 250, scale(5, 20, diamond));
	RoundedPolygon poly2 = new RoundedPolygon();
	{
		poly2.setVertices(vertices2);
	}
	LeafShapeGroup leaf2 = new LeafShapeGroup(poly2);
	
	IntPoint[] vertices3 = translate(400, 250, scale(10, 5, pentagon));
	RoundedPolygon poly3 = new RoundedPolygon();
	{
		poly3.setVertices(vertices3);
	}
	LeafShapeGroup leaf3 = new LeafShapeGroup(poly3);
	
	IntPoint[] vertices4 = translate(200, 100, scale(-5, 5, triangleRight));
	RoundedPolygon poly4 = new RoundedPolygon();
	{
		poly4.setVertices(vertices4);
	}
	LeafShapeGroup leaf4 = new LeafShapeGroup(poly4);
	
	IntPoint[] vertices5 = translate(200, 200, scale(10, -10, pentagon));
	RoundedPolygon poly5 = new RoundedPolygon();
	{
		poly5.setVertices(vertices5);
	}
	
	static void assertEquals(int left, int top, int width, int height, Extent actual) {
		assert actual.getLeft() == left;
		assert actual.getTop() == top;
		assert actual.getWidth() == width;
		assert actual.getHeight() == height;
	}
	
	static void assertEquals(int x, int y, IntPoint p) {
		assert p.getX() == x;
		assert p.getY() == y;
	}
	
	static void assertEquals(int x, int y, IntVector v) {
		assert v.getX() == x;
		assert v.getY() == y;
	}
	
	@Test
	void testGetShape() {
		assert leaf1.getShape() == poly1;
		assert leaf2.getShape() == poly2;
		assert leaf3.getShape() == poly3;
	}
	
	@Test
	void testGetOriginalExtent() {
		assertEquals(390, 240, 20, 20, leaf1.getOriginalExtent());
		assertEquals(395, 230, 10, 40, leaf2.getOriginalExtent());
		assertEquals(380, 245, 40, 10, leaf3.getOriginalExtent());
	}
	
	@Test
	void testGetExtent() {
		assertEquals(390, 240, 20, 20, leaf1.getExtent());
		assertEquals(395, 230, 10, 40, leaf2.getExtent());
		assertEquals(380, 245, 40, 10, leaf3.getExtent());
	}
	
	@Test
	void testGetParentGroup() {
		assert leaf1.getParentGroup() == null;
		assert leaf2.getParentGroup() == null;
		assert leaf3.getParentGroup() == null;
	}
	
	@Test
	void testToInnerCoordinates_IntPoint() {
		assertEquals(93, 77, leaf1.toInnerCoordinates(new IntPoint(93, 77)));
	}
	
	@Test
	void testToGlobalCoordinates() {
		assertEquals(93, 77, leaf2.toGlobalCoordinates(new IntPoint(93, 77)));
	}
	
	@Test
	void testToInnerCoordinates_IntVector() {
		assertEquals(93, 77, leaf3.toInnerCoordinates(new IntVector(93, 77)));
	}
}
