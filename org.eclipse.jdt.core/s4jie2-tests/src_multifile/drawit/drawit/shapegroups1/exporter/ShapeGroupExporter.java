package drawit.shapegroups1.exporter;

import java.awt.Color;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import drawit.IntPoint;
import drawit.RoundedPolygon;
import drawit.shapegroups1.Extent;
import drawit.shapegroups1.LeafShapeGroup;
import drawit.shapegroups1.NonleafShapeGroup;
import drawit.shapegroups1.ShapeGroup;

public class ShapeGroupExporter {
	
	public static Object toPlainData(Color color) {
		return Map.of("red", color.getRed(), "green", color.getGreen(), "blue", color.getBlue());
	}
	
	public static Object toPlainData(IntPoint point) {
		return Map.of("x", point.getX(), "y", point.getY());
	}
	
	public static Object toPlainData(Extent extent) {
		return Map.of("left", extent.getLeft(), "top", extent.getTop(), "right", extent.getRight(), "bottom", extent.getBottom());
	}
	
	public static Object toPlainData(RoundedPolygon shape) {
		return Map.of(
				"vertices", Arrays.stream(shape.getVertices()).map(v -> toPlainData(v)).collect(Collectors.toList()),
				"radius", shape.getRadius(),
				"color", toPlainData(shape.getColor()));
	}
	
	public static Object toPlainData(ShapeGroup shapeGroup) {
		if (shapeGroup instanceof LeafShapeGroup) {
			return Map.of(
					"originalExtent", toPlainData(shapeGroup.getOriginalExtent()),
					"extent", toPlainData(shapeGroup.getExtent()),
					"shape", toPlainData(((LeafShapeGroup)shapeGroup).getShape()));
		} else {
			return Map.of(
					"originalExtent", toPlainData(shapeGroup.getOriginalExtent()),
					"extent", toPlainData(shapeGroup.getExtent()),
					"subgroups", ((NonleafShapeGroup)shapeGroup).getSubgroups().stream().map(g -> toPlainData(g)).collect(Collectors.toList()));
		}
	}

}
