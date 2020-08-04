package node_appearances;

import java.awt.Color;
import java.util.Objects;

public class SquareNodeAppearance extends NodeAppearance {

	private final int width;
	
	public int getWidth() { return width; }
	
	public SquareNodeAppearance(Color color, int width) {
		super(color);
		this.width = width;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;
		return width == ((SquareNodeAppearance)other).width;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getColor(), width);
	}
	
}
