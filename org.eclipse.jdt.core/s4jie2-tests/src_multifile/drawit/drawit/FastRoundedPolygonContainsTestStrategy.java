package drawit;

public class FastRoundedPolygonContainsTestStrategy implements RoundedPolygonContainsTestStrategy {

	/**
	 * Returns whether the given polygon contains the given point, according to this contains test strategy.
	 * 
	 * @pre | polygon != null
	 * @pre | point != null
	 * @inspects | polygon
	 * @post This method returns {@code true} if and only if
	 *       the given polygon's bounding box contains the given point.
	 *    | result == polygon.getBoundingBox().contains(point)
	 */
	@Override
	public boolean contains(RoundedPolygon polygon, IntPoint point) {
		return polygon.getBoundingBox().contains(point);
	}
	
}
