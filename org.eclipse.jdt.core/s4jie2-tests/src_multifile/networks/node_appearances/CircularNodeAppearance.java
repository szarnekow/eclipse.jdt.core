package node_appearances;

import java.awt.Color;
import java.util.Objects;

public class CircularNodeAppearance extends NodeAppearance {
	
	private final int radius;
	
	public int getRadius() { return radius; }
	
	public CircularNodeAppearance(Color color, int radius) {
		super(color);
		this.radius = radius;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;
		return radius == ((CircularNodeAppearance)other).radius;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getColor(), radius);
	}

}
