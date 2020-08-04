package drawit;

public interface RoundedPolygonContainsTestStrategy {

	/**
	 * Returns whether the given polygon contains the given point, according to this contains test strategy.
	 * 
	 * @pre | polygon != null
	 * @pre | point != null
	 * @inspects | polygon
	 * @post If the given polygon's bounding box does not contain the given point, the result is {@code false}.
	 *    | polygon.getBoundingBox().contains(point) || result == false
	 * @post If the given polygon contains the given point, the result is {@code true}.
	 *    | !polygon.contains(point) || result == true
	 */
	boolean contains(RoundedPolygon polygon, IntPoint point);
	
}
