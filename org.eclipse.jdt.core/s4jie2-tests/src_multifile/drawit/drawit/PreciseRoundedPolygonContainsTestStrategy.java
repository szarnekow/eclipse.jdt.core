package drawit;

public class PreciseRoundedPolygonContainsTestStrategy implements RoundedPolygonContainsTestStrategy {

	/**
	 * Returns whether the given polygon contains the given point, according to this contains test strategy.
	 * 
	 * @throws IllegalArgumentException if {@code polygon} is {@code null}
	 *    | polygon == null
	 * @throws IllegalArgumentException if {@code point} is {@code null}
	 *    | point == null
	 * @inspects | polygon
	 * @post This method returns {@code true} if and only if
	 *       the given polygon contains the given point.
	 *    | result == polygon.contains(point)
	 */
	@Override
	public boolean contains(RoundedPolygon polygon, IntPoint point) {
		if (polygon == null)
			throw new IllegalArgumentException("polygon is null");
		if (point == null)
			throw new IllegalArgumentException("point is null");
		
		return polygon.contains(point);
	}
	
}
