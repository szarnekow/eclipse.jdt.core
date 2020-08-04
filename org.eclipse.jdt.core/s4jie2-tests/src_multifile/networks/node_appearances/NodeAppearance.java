package node_appearances;

import java.awt.Color;

public abstract class NodeAppearance {
	
	private final Color color;
	
	public Color getColor() { return color; }
	
	public NodeAppearance(Color color) {
		this.color = color;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		return color.equals(((NodeAppearance)other).color);
	}
	
	@Override
	public int hashCode() {
		return color.hashCode();
	}

}
