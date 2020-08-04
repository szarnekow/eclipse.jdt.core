package drawit_officialtests.shapegroups2;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import drawit.IntPoint;
import drawit.IntVector;
import drawit.RoundedPolygon;
import drawit.shapegroups2.Extent;
import drawit.shapegroups2.LeafShapeGroup;
import drawit.shapegroups2.NonleafShapeGroup;
import drawit.shapegroups2.ShapeGroup;
import drawit.shapes2.ControlPoint;
import drawit.shapes2.RoundedPolygonShape;
import drawit.shapes2.ShapeGroupShape;

class ShapeGroupTest_Nonleaves_2Levels {
	
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
		poly1.setRadius(2);
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
		poly2.setRadius(1);
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
		poly3.setRadius(2);
	}
	LeafShapeGroup leaf3 = new LeafShapeGroup(poly3);
	{
		//assertEquals(380, 245, 40, 10, leaf3.getOriginalExtent());
		leaf3.setExtent(Extent.ofLeftTopWidthHeight(380, 245, 16, 20));
		// scale by 2/5, 2
		// translate by 0, 0
	}
	
	NonleafShapeGroup group1 = new NonleafShapeGroup(new ShapeGroup[] {leaf1, leaf2, leaf3});
	{
		//assertEquals(380, 235, 50, 40, group1.getOriginalExtent());
		group1.setExtent(Extent.ofLeftTopWidthHeight(1380, 735, 1000, 400));
		// scale by 20, 10
		// translate by 1000, 500
	}
	
	IntPoint[] vertices4 = translate(200, 100, scale(-5, 5, triangleRight));
	RoundedPolygon poly4 = new RoundedPolygon();
	{
		poly4.setVertices(vertices4);
		poly4.setRadius(1);
	}
	LeafShapeGroup leaf4 = new LeafShapeGroup(poly4);
	
	IntPoint[] vertices5 = translate(200, 200, scale(10, -10, pentagon));
	RoundedPolygon poly5 = new RoundedPolygon();
	{
		poly5.setVertices(vertices5);
		poly5.setRadius(3);
	}
	LeafShapeGroup leaf5 = new LeafShapeGroup(poly5);
	
	NonleafShapeGroup group2 = new NonleafShapeGroup(new ShapeGroup[] {leaf4, leaf5});
	
	NonleafShapeGroup group3 = new NonleafShapeGroup(new ShapeGroup[] {group1, group2});
	
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

		assertEquals(380, 235, 50, 40, group1.getOriginalExtent());
		
		assertEquals(195, 95, 10, 10, leaf4.getOriginalExtent());
		assertEquals(180, 190, 40, 20, leaf5.getOriginalExtent());
		
		assertEquals(180, 95, 40, 115, group2.getOriginalExtent());
		
		assertEquals(180, 95, 2200, 1040, group3.getOriginalExtent());
	}
	
	@Test
	void testGetExtent() {
		assertEquals(400, 255, 30, 10, leaf1.getExtent());
		assertEquals(390, 235, 20, 40, leaf2.getExtent());
		assertEquals(380, 245, 16, 20, leaf3.getExtent());
		
		assertEquals(1380, 735, 1000, 400, group1.getExtent());
		
		assertEquals(195, 95, 10, 10, leaf4.getExtent());
		assertEquals(180, 190, 40, 20, leaf5.getExtent());
		
		assertEquals(180, 95, 40, 115, group2.getExtent());
		
		assertEquals(180, 95, 2200, 1040, group3.getExtent());
	}
	
	@Test
	void testGetParentGroup() {
		assert leaf1.getParentGroup() == group1;
		assert leaf2.getParentGroup() == group1;
		assert leaf3.getParentGroup() == group1;
		assert group1.getParentGroup() == group3;
		
		assert leaf4.getParentGroup() == group2;
		assert leaf5.getParentGroup() == group2;
		assert group2.getParentGroup() == group3;
		
		assert group3.getParentGroup() == null;
	}
	
	@Test
	void testGetSubgroups() {
		assert List.of(leaf1, leaf2, leaf3).equals(group1.getSubgroups());
		assert List.of(leaf4, leaf5).equals(group2.getSubgroups());
		
		assert List.of(group1, group2).equals(group3.getSubgroups());
	}
	
	@Test
	void testGetSubgroupCount() {
		assert group1.getSubgroupCount() == 3;
		assert group2.getSubgroupCount() == 2;
		assert group3.getSubgroupCount() == 2;
	}
	
	@Test
	void testGetSubgroup() {
		assert group1.getSubgroup(0) == leaf1;
		assert group1.getSubgroup(1) == leaf2;
		assert group1.getSubgroup(2) == leaf3;
		assert group2.getSubgroup(0) == leaf4;
		assert group2.getSubgroup(1) == leaf5;
		assert group3.getSubgroup(0) == group1;
		assert group3.getSubgroup(1) == group2;
	}
	
	@Test
	void testGetSubgroupAt() {
		assert group1.getSubgroupAt(new IntPoint(429, 260)) == leaf1;
		assert group1.getSubgroupAt(new IntPoint(395, 237)) == leaf2;
		assert group1.getSubgroupAt(new IntPoint(385, 250)) == leaf3;
		assert group1.getSubgroupAt(new IntPoint(431, 250)) == null;
		assert group3.getSubgroupAt(new IntPoint(2000, 1000)) == group1;
		assert group3.getSubgroupAt(new IntPoint(200, 100)) == group2;
	}
	
	@Test
	void testBringToFront1() {
		group1.bringToFront();
		assert List.of(group1, group2).equals(group3.getSubgroups());
		group2.bringToFront();
		assert List.of(group2, group1).equals(group3.getSubgroups());
		
		leaf1.bringToFront();
		assert List.of(leaf1, leaf2, leaf3).equals(group1.getSubgroups());
		leaf1.bringToFront();
		assert List.of(leaf1, leaf2, leaf3).equals(group1.getSubgroups());
		leaf2.bringToFront();
		assert List.of(leaf2, leaf1, leaf3).equals(group1.getSubgroups());
		leaf1.bringToFront();
		assert List.of(leaf1, leaf2, leaf3).equals(group1.getSubgroups());
		leaf3.bringToFront();
		assert List.of(leaf3, leaf1, leaf2).equals(group1.getSubgroups());
		leaf3.bringToFront();
		assert List.of(leaf3, leaf1, leaf2).equals(group1.getSubgroups());
		leaf2.bringToFront();
		assert List.of(leaf2, leaf3, leaf1).equals(group1.getSubgroups());
	}
	
	@Test
	void testBringToFront2() {
		leaf4.bringToFront();
		assert List.of(leaf4, leaf5).equals(group2.getSubgroups());
		leaf4.bringToFront();
		assert List.of(leaf4, leaf5).equals(group2.getSubgroups());
		leaf5.bringToFront();
		assert List.of(leaf5, leaf4).equals(group2.getSubgroups());
		leaf5.bringToFront();
		assert List.of(leaf5, leaf4).equals(group2.getSubgroups());
		leaf4.bringToFront();
		assert List.of(leaf4, leaf5).equals(group2.getSubgroups());
	}
	
	@Test
	void testSendToBack1() {
		group1.sendToBack();
		assert List.of(group2, group1).equals(group3.getSubgroups());
		group1.sendToBack();
		assert List.of(group2, group1).equals(group3.getSubgroups());
		group2.sendToBack();
		assert List.of(group1, group2).equals(group3.getSubgroups());
		
		leaf1.sendToBack();
		assert List.of(leaf2, leaf3, leaf1).equals(group1.getSubgroups());
		leaf1.sendToBack();
		assert List.of(leaf2, leaf3, leaf1).equals(group1.getSubgroups());
		leaf3.sendToBack();
		assert List.of(leaf2, leaf1, leaf3).equals(group1.getSubgroups());
		leaf1.sendToBack();
		assert List.of(leaf2, leaf3, leaf1).equals(group1.getSubgroups());
	}
	
	@Test
	void testSendToBack2() {
		leaf4.sendToBack();
		assert List.of(leaf5, leaf4).equals(group2.getSubgroups());
		leaf4.sendToBack();
		assert List.of(leaf5, leaf4).equals(group2.getSubgroups());
		leaf5.sendToBack();
		assert List.of(leaf4, leaf5).equals(group2.getSubgroups());
	}
	
	@Test
	void testSendToBack_bringToFront() {
		leaf1.sendToBack();
		assert List.of(leaf2, leaf3, leaf1).equals(group1.getSubgroups());
		leaf3.bringToFront();
		assert List.of(leaf3, leaf2, leaf1).equals(group1.getSubgroups());
	}
	
	@Test
	void testToInnerCoordinates_IntPoint() {
		assertEquals(392, 242, leaf1.toInnerCoordinates(new IntPoint(1840, 945)));
		assertEquals(393, 225, leaf2.toInnerCoordinates(new IntPoint(1500, 685)));
		assertEquals(430, 240, leaf3.toInnerCoordinates(new IntPoint(1780, 735)));
		assertEquals(400, 245, group1.toInnerCoordinates(new IntPoint(1780, 835)));
		assertEquals(123, 456, group2.toInnerCoordinates(new IntPoint(123, 456)));
	}
	
	@Test
	void testToGlobalCoordinates() {
		assertEquals(1840, 945, leaf1.toGlobalCoordinates(new IntPoint(392, 242)));
		assertEquals(1500, 685, leaf2.toGlobalCoordinates(new IntPoint(393, 225)));
		assertEquals(1780, 735, leaf3.toGlobalCoordinates(new IntPoint(430, 240)));
		assertEquals(1780, 835, group1.toGlobalCoordinates(new IntPoint(400, 245)));
	}
	
	@Test
	void testToInnerCoordinates_IntVector() {
		assertEquals(2, -2, leaf1.toInnerCoordinates(new IntVector(60, -10)));
		assertEquals(-10, 0, leaf2.toInnerCoordinates(new IntVector(-400, 0)));
		assertEquals(40, 7, leaf3.toInnerCoordinates(new IntVector(320, 140)));
		assertEquals(123, 456, group1.toInnerCoordinates(new IntVector(2460, 4560)));
	}
	
	@Test
	void testGetDrawingCommands() {
		poly1.setColor(Color.red);
		poly2.setColor(Color.green);
		poly3.setColor(Color.blue);
		poly4.setColor(Color.red);
		
		String cmds = group3.getDrawingCommands();

		class DrawingCommandsSyntaxChecker {
			String[] tokens = cmds.split("\\s+");
			int nbLines = 0;
			int nbArcs = 0;
			int nbFills = 0;
			int stackSize = 0;
			int i = 0;
			
			double parseArgument() {
				return Double.parseDouble(tokens[i++]);
			}
			
			int parseIntArgument() {
				return Integer.parseInt(tokens[i++]);
			}
			
			int parseColorComponent() {
				int result = parseIntArgument();
				assert 0 <= result && result <= 255;
				return result;
			}
			
			void parseColor() {
				parseColorComponent();
				parseColorComponent();
				parseColorComponent();
			}
			
			void parseAndCheckCoordinates() {
				parseArgument();
				parseArgument();
			}
			
			void parseAndCheckArcRadius() {
				parseArgument();
			}
			
			void checkPopTransform() {
				assert stackSize > 0;
				stackSize--;
			}
			
			void check() {
				while (i < tokens.length) {
					String operator = tokens[i++];
					switch (operator) {
					case "":
						break;
					case "line":
						parseAndCheckCoordinates(); parseAndCheckCoordinates(); nbLines++; break;
					case "arc":
						parseAndCheckCoordinates(); parseAndCheckArcRadius(); parseArgument(); parseArgument(); nbArcs++; break;
					case "fill":
						parseColor(); nbFills++; break;
					case "pushTranslate":
						parseArgument(); parseArgument(); stackSize++; break;
					case "pushScale":
						parseArgument(); parseArgument(); stackSize++; break;
					case "popTransform":
						checkPopTransform(); break;
					default:
						throw new AssertionError("No such drawing operator: '" + operator + "'");
					}
				}
			}
			
		}
		
		assert cmds != null;
		var checker = new DrawingCommandsSyntaxChecker();
		checker.check();
		assert checker.nbLines >= 3 + 4 + 5 + 3 + 5 : "Result of getDrawingCommands() should have at least one line per polygon edge.";
		assert checker.nbArcs >= 3 + 4 + 5 + 3 + 5 : "Result of getDrawingCommands() should have at least one arc per polygon corner.";
		assert checker.nbFills >= 5 : "Result of getDrawingCommands() should have at least one fill per polygon.";
		
		// Obviously, this is a very incomplete test and should be complemented by interactive testing through the GUI.
	}
	
	@Test
	void testRoundedPolygonShape_getters() {
		RoundedPolygonShape shape = new RoundedPolygonShape(leaf1, poly1);
		assert shape.getPolygon() == poly1;
		assert shape.getParent() == leaf1;
	}
	
	@Test
	void testRoundedPolygonShape_contains() {
		RoundedPolygonShape shape = new RoundedPolygonShape(leaf1, poly1);
		assert shape.contains(p(400, 250));
		assert !shape.contains(p(350, 200));
	}
	
	@Test
	void testRoundedPolygonShape_toShapeCoordinates() {
		RoundedPolygonShape shape = new RoundedPolygonShape(leaf1, poly1);
		assertEquals(392, 242, shape.toShapeCoordinates(p(1840, 945)));
	}

	@Test
	void testRoundedPolygonShape_toGlobalCoordinates() {
		RoundedPolygonShape shape = new RoundedPolygonShape(leaf1, poly1);
		assertEquals(1840, 945, shape.toGlobalCoordinates(p(392, 242)));
	}
	
	@Test
	void testRoundedPolygonShape_createControlPoints_getLocation() {
		RoundedPolygonShape shape = new RoundedPolygonShape(leaf1, poly1);
		ControlPoint[] controlPoints = shape.createControlPoints();
		assert controlPoints != null;
		assert controlPoints.length == 3;
		assert Arrays.stream(controlPoints).allMatch(p -> p != null);
		assertEquals(390, 240, controlPoints[0].getLocation());
		assertEquals(390, 260, controlPoints[1].getLocation());
		assertEquals(410, 250, controlPoints[2].getLocation());
	}
	
	@Test
	void testRoundedPolygonShape_createControlPoints_move() {
		RoundedPolygonShape shape = new RoundedPolygonShape(leaf1, poly1);
		IntPoint p10_shape = poly1.getVertices()[1];
		IntPoint p10_global = leaf1.toGlobalCoordinates(p10_shape);
		ControlPoint[] controlPoints = shape.createControlPoints();
		controlPoints[1].move(new IntVector(60, 40));
		assertEquals(390, 240, poly1.getVertices()[0]);
		assertEquals(410, 250, poly1.getVertices()[2]);
		IntPoint p11_shape = poly1.getVertices()[1];
		IntPoint p11_global = leaf1.toGlobalCoordinates(p11_shape);
		assert p11_global.getX() == p10_global.getX() + 60;
		assert p11_global.getY() == p10_global.getY() + 40;
	}
	
	@Test
	void testRoundedPolygonShape_createControlPoints_remove() {
		RoundedPolygonShape shape = new RoundedPolygonShape(leaf2, poly2);
		ControlPoint[] controlPoints = shape.createControlPoints();
		controlPoints[2].remove();
		IntPoint[] vertices = poly2.getVertices();
		assert vertices.length == 3;
		assertEquals(395, 250, vertices[0]);
		assertEquals(400, 230, vertices[1]);
		assertEquals(400, 270, vertices[2]);
	}
	
	@Test
	void testShapeGroupShape_getters() {
		ShapeGroupShape shape = new ShapeGroupShape(leaf1);
		assert shape.getShapeGroup() == leaf1;
		assert shape.getParent() == group1;
	}
	
	@Test
	void testShapeGroupShape_contains() {
		ShapeGroupShape shape = new ShapeGroupShape(leaf1);
		assert shape.contains(p(410, 260));
		assert !shape.contains(p(390, 250));
	}
	
	@Test
	void testShapeGroupShape_toShapeCoordinates() {
		ShapeGroupShape shape = new ShapeGroupShape(leaf1);
		assertEquals(400, 245, shape.toShapeCoordinates(p(1780, 835)));
	}
	
	@Test
	void testShapeGroupShape_toGlobalCoordinates() {
		ShapeGroupShape shape = new ShapeGroupShape(leaf1);
		assertEquals(1780, 835, shape.toGlobalCoordinates(p(400, 245)));
	}
	
	@Test
	void testShapeGroupShape_createControlPoints_getLocation() {
		ShapeGroupShape shape = new ShapeGroupShape(leaf1);
		ControlPoint[] controlPoints = shape.createControlPoints();
		assert controlPoints.length == 2;
		assertEquals(400, 255, controlPoints[0].getLocation());
		assertEquals(430, 265, controlPoints[1].getLocation());
	}
	
	@Test
	void testShapeGroupShape_createControlPoints_move_upperLeft() {
		ShapeGroupShape shape = new ShapeGroupShape(leaf1);
		ControlPoint[] controlPoints = shape.createControlPoints();
		controlPoints[0].move(new IntVector(-140, -30));
		assertEquals(393, 252, leaf1.getExtent().getTopLeft());
		assertEquals(430, 265, leaf1.getExtent().getBottomRight());
	}
	
	@Test
	void testShapeGroupShape_createControlPoints_move_bottomRight() {
		ShapeGroupShape shape = new ShapeGroupShape(leaf1);
		ControlPoint[] controlPoints = shape.createControlPoints();
		controlPoints[1].move(new IntVector(140, 30));
		assertEquals(400, 255, leaf1.getExtent().getTopLeft());
		assertEquals(437, 268, leaf1.getExtent().getBottomRight());
	}
}
