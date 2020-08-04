package drawit.shapegroups1;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import drawit.IntPoint;
import drawit.RoundedPolygon;

/**
 * Each instance of this class represents a leaf shape group in a shape group graph.
 */
public class LeafShapeGroup extends ShapeGroup {

	/**
	 * @invar | shape != null 
	 */
	final RoundedPolygon shape;

	/**
	 * Returns the shape directly contained by this shape group, or {@code null} if this
	 * is a non-leaf shape group.
	 * 
	 * @immutable
	 * 
	 * @post | result != null
	 */
	public RoundedPolygon getShape() { return shape; }
	
	/**
	 * Returns the list of all RoundedPolygon objects contained directly or
	 * indirectly by this shape group, in depth-first order.
	 * 
	 * @post | Objects.equals(result, List.of(getShape())) 
	 */
	@Override
	public List<RoundedPolygon> getAllShapes() { return List.of(shape); }
	
	private static Extent getShapeExtent(RoundedPolygon shape) {
		if (shape == null)
			throw new IllegalArgumentException("shape is null");
		if (shape.getVertices().length < 3)
			throw new IllegalArgumentException("shape has less than three vertices");
		
		IntPoint[] vertices = shape.getVertices();
		int left = Integer.MAX_VALUE;
		int top = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		int bottom = Integer.MIN_VALUE;
		for (int i = 0; i < vertices.length; i++) {
			IntPoint vertex = vertices[i];
			left = Math.min(left, vertex.getX());
			right = Math.max(right, vertex.getX());
			top = Math.min(top, vertex.getY());
			bottom = Math.max(bottom, vertex.getY());
		}
		return Extent.ofLeftTopRightBottom(left, top, right, bottom);
	}
	
	/**
	 * Initializes this object to represent a leaf shape group that directly contains the given shape.
	 * 
	 * @throws IllegalArgumentException if {@code shape} is null
	 *    | shape == null
	 * @throws IllegalArgumentException if {@code shape} has less than three vertices
	 *    | shape.getVertices().length < 3
	 * @inspects | shape
	 * @mutates | this
	 * @post | getShape() == shape
	 * @post | getParentGroup() == null
	 * @post | getOriginalExtent().getLeft() == Arrays.stream(shape.getVertices()).mapToInt(p -> p.getX()).min().getAsInt()
	 * @post | getOriginalExtent().getTop() == Arrays.stream(shape.getVertices()).mapToInt(p -> p.getY()).min().getAsInt()
	 * @post | getOriginalExtent().getRight() == Arrays.stream(shape.getVertices()).mapToInt(p -> p.getX()).max().getAsInt()
	 * @post | getOriginalExtent().getBottom() == Arrays.stream(shape.getVertices()).mapToInt(p -> p.getY()).max().getAsInt()
	 * @post | getExtent().equals(getOriginalExtent())
	 */
	public LeafShapeGroup(RoundedPolygon shape) {
		super(getShapeExtent(shape));
		
		this.shape = shape;
	}
	
	@Override
	String getInnerDrawingCommands() {
		return shape.getDrawingCommands();
	}
	
}
