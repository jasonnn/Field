package field.core.plugins.drawing.tweak.python;

import field.core.plugins.drawing.tweak.TweakSplineUI.SelectedVertex;
import field.namespace.generic.tuple.Pair;

import java.util.List;


public interface iWholeCoordTransform extends iCoordTransformation {

	public void setNodes(List<Pair<SelectedVertex, Float>> all);
	
}
