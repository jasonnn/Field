package field.core.plugins.drawing.tweak.python;

import field.core.plugins.drawing.tweak.TweakSplineUI.SelectedVertex;
import field.util.collect.tuple.Pair;

import java.util.List;


public
interface iWholeCoordTransform extends iCoordTransformation {

    public
    void setNodes(List<Pair<SelectedVertex, Float>> all);

}
