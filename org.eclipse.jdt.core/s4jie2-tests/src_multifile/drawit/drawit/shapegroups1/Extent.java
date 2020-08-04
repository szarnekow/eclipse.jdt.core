package drawit.shapegroups1;

import drawit.IntPoint;

/**
 * Each instance of this class represents a nonempty rectangular area in a 2D coordinate
 * system, whose edges are parallel to the coordinate axes.
 * 
 * This class must deal with illegal arguments defensively.
 * 
 * @immutable
 * 
 * @invar | getLeft() < getRight()
 * @invar | getTop() < getBottom()
 * @invar | getWidthAsLong() == (long)getRight() - getLeft()
 * @invar | getHeightAsLong() == (long)getBottom() - getTop()
 */
public class Extent {
	
	/**
	 * @invar | left < right
	 * @invar | top < bottom
	 */
	private final int left;
	private final int top;
	private final int right;
	private final int bottom;

	/**
	 * Returns the X coordinate of the edge parallel to the Y axis
	 * with the smallest X coordinate.
	 */
	public int getLeft() { return left; }
	/**
	 * Returns the Y coordinate of the edge parallel to the X axis
	 * with the smallest Y coordinate.
	 */
	public int getTop() { return top; }
	/**
	 * Returns the X coordinate of the edge parallel to the Y axis
	 * with the largest X coordinate.
	 */
	public int getRight() { return right; }
	/**
	 * Returns the Y coordinate of the edge parallel to the X axis
	 * with the largest Y coordinate.
	 */
	public int getBottom() { return bottom; }
	/**
	 * Returns the distance between the edges that are parallel to the Y axis.
	 */
	public long getWidthAsLong() { return (long)right - left; }
	/**
	 * Returns the distance between the edges that are parallel to the Y axis.
	 * 
	 * @throws UnsupportedOperationException if the width is greater than the maximum int value
	 *    | getWidthAsLong() > Integer.MAX_VALUE
	 * @post | result == getWidthAsLong()
	 */
	public int getWidth() {
		long width = (long)right - left;
		if (width > Integer.MAX_VALUE)
			throw new UnsupportedOperationException("width too big");
		return (int)width;
	}
	/**
	 * Returns the distance between the edges that are parallel to the X axis.
	 */
	public long getHeightAsLong() { return (long)bottom - top; }
	/**
	 * Returns the distance between the edges that are parallel to the X axis.
	 * 
	 * @throws UnsupportedOperationException if the height is greater than the maximum int value
	 *    | getHeightAsLong() > Integer.MAX_VALUE
	 * @post | result == getHeightAsLong()
	 */
	public int getHeight() {
		long height = (long)bottom - top;
		if (height > Integer.MAX_VALUE)
			throw new UnsupportedOperationException("height too big");
		return (int)height;
	}
	
	/**
	 * Returns the top-left corner of this extent.
	 * 
	 * @post | result != null
	 * @post | result.equals(new IntPoint(getLeft(), getTop()))
	 */
	public IntPoint getTopLeft() { return new IntPoint(left, top); }
	
	/**
	 * Returns the bottom-right corner of this extent.
	 * 
	 * @post | result != null
	 * @post | result.equals(new IntPoint(getRight(), getBottom()))
	 */
	public IntPoint getBottomRight() { return new IntPoint(right, bottom); }
	
	/**
	 * Returns whether this extent, considered as a closed set of points (i.e.
	 * including its edges and its vertices), contains the given point.
	 * 
	 * @throws IllegalArgumentException if {@code point} is null
	 *    | point == null
	 * @post
	 *    | result == (
	 *    |     getLeft() <= point.getX() && point.getX() <= getRight() &&
	 *    |     getTop() <= point.getY() && point.getY() <= getBottom()
	 *    | ) 
	 */
	public boolean contains(IntPoint point) {
		return
				getLeft() <= point.getX() && point.getX() <= getRight() &&
				getTop() <= point.getY() && point.getY() <= getBottom();
	}

	/**
	 * Returns whether this extent is a smallest nonempty axis-aligned rectangle that contains the given rectangular area.
	 * 
	 * @pre | left <= right
	 * @pre | top <= bottom
	 * @post
	 *    | result == (
	 *    |     getLeft() <= left && right <= getRight() &&
	 *    |     getTop() <= top && bottom <= getBottom() &&
	 *    |     getWidthAsLong() == Math.max(1, (long)right - left) &&
	 *    |     getHeightAsLong() == Math.max(1, (long)bottom - top)
	 *    | )
	 */
	public boolean isBoundingBoxFor(int left, int top, int right, int bottom) {
		return
				this.left <= left && this.top <= top && right <= this.right && bottom <= this.bottom &&
				getWidthAsLong() == Math.max(1, (long)right - left) &&
				getHeightAsLong() == Math.max(1, (long)bottom - top);
	}

	/**
	 * Returns whether this extent equals the given extent.
	 * 
	 * @post | result == (
	 *       |     other != null &&
	 *       |     getTopLeft().equals(other.getTopLeft()) &&
	 *       |     getBottomRight().equals(other.getBottomRight())
	 *       | )
	 */
	public boolean equals(Extent other) {
		return other != null && left == other.left && top == other.top && right == other.right && bottom == other.bottom;
	}
	
	/**
	 * Returns whether this extent equals the given object.
	 * 
	 * @post | result == (other instanceof Extent && this.equals((Extent)other))
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof Extent && equals((Extent)other);
	}
	
	/**
	 * Returns a number that depends only on this extent's attributes.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bottom;
		result = prime * result + left;
		result = prime * result + right;
		result = prime * result + top;
		return result;
	}
	
	/**
	 * Returns a textual representation of this object.
	 * 
	 * @post | result != null
	 */
	@Override
	public String toString() {
		return "drawit.shapegroups1.Extent[left="+left+", top="+top+", right="+right+", bottom="+bottom+"]";
	}
	
	private Extent(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
	/**
	 * Returns an object representing the extent defined by the given left, top, width, and height.
	 * 
	 * @throws IllegalArgumentException if the given width is not positive
	 *    | width <= 0
	 * @throws IllegalArgumentException if the given height is not positive
	 *    | height <= 0
	 * @throws IllegalArgumentException if the sum of {@code left} and {@code width} is greater than {@code Integer.MAX_VALUE}
	 *    | Integer.MAX_VALUE - width < left 
	 * @throws IllegalArgumentException if the sum of {@code top} and {@code height} is greater than {@code Integer.MAX_VALUE}
	 *    | Integer.MAX_VALUE - height < top
	 * @post | result != null
	 * @post | result.getLeft() == left
	 * @post | result.getTop() == top
	 * @post | result.getWidth() == width
	 * @post | result.getHeight() == height
	 */
	public static Extent ofLeftTopWidthHeight(int left, int top, int width, int height) {
		if (width <= 0)
			throw new IllegalArgumentException("width is not positive");
		if (height <= 0)
			throw new IllegalArgumentException("height is not positive");
		if (Integer.MAX_VALUE - width < left)
			throw new IllegalArgumentException("left + width too large");
		if (Integer.MAX_VALUE - height < top)
			throw new IllegalArgumentException("top + height too large");
		return new Extent(left, top, left + width, top + height);
	}
	
	/**
	 * Returns an object representing the extent defined by the given left, top, right, and bottom.
	 * 
	 * @throws IllegalArgumentException if the given right is not greater than the given left
	 *    | right <= left
	 * @throws IllegalArgumentException if the given bottom is not greater than the given top
	 *    | bottom <= top
	 * @post | result != null
	 * @post | result.getLeft() == left
	 * @post | result.getTop() == top
	 * @post | result.getRight() == right
	 * @post | result.getBottom() == bottom
	 */
	public static Extent ofLeftTopRightBottom(int left, int top, int right, int bottom) {
		if (right <= left)
			throw new IllegalArgumentException("right not greater than left");
		if (bottom <= top)
			throw new IllegalArgumentException("bottom not greater than top");
		return new Extent(left, top, right, bottom);
	}
	
	/**
	 * Returns an object that has the given left coordinate and the same
	 * right, top, and bottom coordinate as this object.
	 * 
	 * @throws IllegalArgumentException if the given left coordinate is not less than this extent's right coordinate
	 *    | getRight() <= newLeft
	 * @throws IllegalArgumentException if the width of the resulting extent is greater than {@code Integer.MAX_VALUE}
	 *    | 0 <= getRight() && newLeft < getRight() - Integer.MAX_VALUE ||
	 *    | newLeft < 0 && newLeft + Integer.MAX_VALUE < getRight()
	 * @post | result != null
	 * @post | result.getLeft() == newLeft
	 * @post | result.getTop() == getTop()
	 * @post | result.getRight() == getRight()
	 * @post | result.getBottom() == getBottom()
	 */
	public Extent withLeft(int newLeft) {
		if (getRight() <= newLeft)
			throw new IllegalArgumentException("newLeft not less than getRight()");
		if (0 <= getRight() && newLeft < getRight() - Integer.MAX_VALUE ||
			newLeft < 0 && newLeft + Integer.MAX_VALUE < getRight())
			throw new IllegalArgumentException("width too large");
		return new Extent(newLeft, top, right, bottom);
	}

	/**
	 * Returns an object that has the given top coordinate and the same
	 * left, right, and bottom coordinate as this object.
	 * 
	 * @throws IllegalArgumentException if the given left coordinate is not less than this extent's right coordinate
	 *    | getBottom() <= newTop
	 * @throws IllegalArgumentException if the width of the resulting extent is greater than {@code Integer.MAX_VALUE}
	 *    | 0 <= getBottom() && newTop < getBottom() - Integer.MAX_VALUE ||
	 *    | newTop < 0 && newTop + Integer.MAX_VALUE < getBottom()
	 * @post | result != null
	 * @post | result.getLeft() == getLeft()
	 * @post | result.getTop() == newTop
	 * @post | result.getRight() == getRight()
	 * @post | result.getBottom() == getBottom()
	 */
	public Extent withTop(int newTop) {
		if (getBottom() <= newTop)
			throw new IllegalArgumentException("newLeft not less than getRight()");
		if (0 <= getBottom() && newTop < getBottom() - Integer.MAX_VALUE ||
			newTop < 0 && newTop + Integer.MAX_VALUE < getBottom())
			throw new IllegalArgumentException("width too large");
		return new Extent(left, newTop, right, bottom);
	}

	/**
	 * Returns an object that has the given right coordinate and the same
	 * left, top, and bottom coordinate as this object.
	 * 
	 * @throws IllegalArgumentException if the given left coordinate is not less than this extent's right coordinate
	 *    | newRight <= getLeft()
	 * @throws IllegalArgumentException if the width of the resulting extent is greater than {@code Integer.MAX_VALUE}
	 *    | 0 <= newRight && getLeft() < newRight - Integer.MAX_VALUE ||
	 *    | getLeft() < 0 && getLeft() + Integer.MAX_VALUE < newRight
	 * @post | result != null
	 * @post | result.getLeft() == getLeft()
	 * @post | result.getTop() == getTop()
	 * @post | result.getRight() == newRight
	 * @post | result.getBottom() == getBottom()
	 */
	public Extent withRight(int newRight) {
		if (newRight <= getLeft())
			throw new IllegalArgumentException("newLeft not less than getRight()");
		if (0 <= newRight && getLeft() < newRight - Integer.MAX_VALUE ||
			newRight < 0 && getLeft() + Integer.MAX_VALUE < newRight)
			throw new IllegalArgumentException("width too large");
		return new Extent(left, top, newRight, bottom);
	}
	
	/**
	 * Returns an object that has the given bottom coordinate and the same
	 * left, top, and right coordinate as this object.
	 * 
	 * @throws IllegalArgumentException if the given left coordinate is not less than this extent's right coordinate
	 *    | newBottom <= getTop()
	 * @throws IllegalArgumentException if the width of the resulting extent is greater than {@code Integer.MAX_VALUE}
	 *    | 0 <= newBottom && getTop() < newBottom - Integer.MAX_VALUE ||
	 *    | getTop() < 0 && getTop() + Integer.MAX_VALUE < newBottom
	 * @post | result != null
	 * @post | result.getLeft() == getLeft()
	 * @post | result.getTop() == getTop()
	 * @post | result.getRight() == getRight()
	 * @post | result.getBottom() == newBottom
	 */
	public Extent withBottom(int newBottom) {
		if (newBottom <= getTop())
			throw new IllegalArgumentException("newLeft not less than getRight()");
		if (0 <= newBottom && getTop() < newBottom - Integer.MAX_VALUE ||
			getTop() < 0 && getTop() + Integer.MAX_VALUE < newBottom)
			throw new IllegalArgumentException("width too large");
		return new Extent(left, top, right, newBottom);
	}
	
	/**
	 * Returns an object that has the given width and the same left, top,
	 * and bottom coordinate as this object.
	 * 
	 * @throws IllegalArgumentException if the given width is not positive
	 *    | newWidth <= 0
	 * @throws IllegalArgumentException if the new right coordinate would be greater than {@code Integer.MAX_VALUE}
	 *    | Integer.MAX_VALUE - newWidth < getLeft()
	 * @post | result != null
	 * @post | result.getLeft() == getLeft()
	 * @post | result.getTop() == getTop()
	 * @post | result.getWidth() == newWidth
	 * @post | result.getHeightAsLong() == getHeightAsLong()
	 */
	public Extent withWidth(int newWidth) {
		if (newWidth <= 0)
			throw new IllegalArgumentException("newWidth not positive");
		if (Integer.MAX_VALUE - newWidth < getLeft())
			throw new IllegalArgumentException("new right coordinate would be greater than Integer.MAX_VALUE");
		return new Extent(left, top, left + newWidth, bottom);
	}
	
	/**
	 * Returns an object that has the given height and the same left, top,
	 * and right coordinate as this object.
	 * 
	 * @throws IllegalArgumentException if the given height is not positive
	 *    | newHeight <= 0
	 * @throws IllegalArgumentException if the new bottom coordinate would be greater than {@code Integer.MAX_VALUE}
	 *    | Integer.MAX_VALUE - newHeight < getTop()
	 * @post | result != null
	 * @post | result.getLeft() == getLeft()
	 * @post | result.getTop() == getTop()
	 * @post | result.getWidthAsLong() == getWidthAsLong()
	 * @post | result.getHeight() == newHeight
	 */
	public Extent withHeight(int newHeight) {
		if (newHeight <= 0)
			throw new IllegalArgumentException("newHeight not positive");
		if (Integer.MAX_VALUE - newHeight < getTop())
			throw new IllegalArgumentException("new bottom coordinate would be greater than Integer.MAX_VALUE");
		return new Extent(left, top, right, top + newHeight);
	}
}
