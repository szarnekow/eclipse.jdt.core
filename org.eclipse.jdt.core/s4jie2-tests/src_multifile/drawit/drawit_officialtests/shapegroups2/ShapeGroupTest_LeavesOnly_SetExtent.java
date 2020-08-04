package drawit_officialtests.shapegroups2;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import drawit.IntPoint;
import drawit.IntVector;
import drawit.RoundedPolygon;
import drawit.shapegroups2.Extent;
import drawit.shapegroups2.LeafShapeGroup;

class ShapeGroupTest_LeavesOnly_SetExtent {
	
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
	{
		//assertEquals(390, 240, 20, 20, leaf1.getOriginalExtent());
		leaf1.setExtent(Extent.ofLeftTopWidthHeight(400, 255, 30, 10));
		// scale by 3/2, 1/2
		// translate by 10, 15
	}
	
	IntPoint[] vertices2 = translate(400, 250, scale(5, 20, diamond));
	RoundedPolygon poly2 = new RoundedPolygon();
	{
		poly2.setVertices(vertices2);
	}
	LeafShapeGroup leaf2 = new LeafShapeGroup(poly2);
	{
		//assertEquals(395, 230, 10, 40, leaf2.getOriginalExtent());
		leaf2.setExtent(Extent.ofLeftTopWidthHeight(390, 235, 20, 40));
		// scale by 2, 1
		// translate by -5, 5
	}
	
	IntPoint[] vertices3 = translate(400, 250, scale(10, 5, pentagon));
	RoundedPolygon poly3 = new RoundedPolygon();
	{
		poly3.setVertices(vertices3);
	}
	LeafShapeGroup leaf3 = new LeafShapeGroup(poly3);
	{
		//assertEquals(380, 245, 40, 10, leaf3.getOriginalExtent());
		leaf3.setExtent(Extent.ofLeftTopWidthHeight(380, 245, 16, 20));
		// scale by 2/5, 2
		// translate by 0, 0
	}
	
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
		assertEquals(400, 255, 30, 10, leaf1.getExtent());
		assertEquals(390, 235, 20, 40, leaf2.getExtent());
		assertEquals(380, 245, 16, 20, leaf3.getExtent());
	}
	
	@Test
	void testGetParentGroup() {
		assert leaf1.getParentGroup() == null;
		assert leaf2.getParentGroup() == null;
		assert leaf3.getParentGroup() == null;
	}
	
	@Test
	void testToInnerCoordinates_IntPoint() {
		assertEquals(392, 242, leaf1.toInnerCoordinates(new IntPoint(403, 256)));
		assertEquals(393, 225, leaf2.toInnerCoordinates(new IntPoint(386, 230)));
		assertEquals(430, 240, leaf3.toInnerCoordinates(new IntPoint(400, 235)));
	}
	
	@Test
	void testToGlobalCoordinates() {
		assertEquals(403, 256, leaf1.toGlobalCoordinates(new IntPoint(392, 242)));
		assertEquals(388, 234, leaf2.toGlobalCoordinates(new IntPoint(394, 229)));
		assertEquals(360, 265, leaf3.toGlobalCoordinates(new IntPoint(330, 255)));
	}
	
	@Test
	void testToInnerCoordinates_IntVector() {
		assertEquals(2, -2, leaf1.toInnerCoordinates(new IntVector(3, -1)));
		assertEquals(-10, 0, leaf2.toInnerCoordinates(new IntVector(-20, 0)));
		assertEquals(40, 7, leaf3.toInnerCoordinates(new IntVector(16, 14)));
	}
}
