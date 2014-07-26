package field.core.plugins.drawing.tweak.python;

import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.tweak.TweakSplineUI.SelectedVertex;
import field.namespace.generic.tuple.Pair;

import java.util.List;


public
interface iNodeSelection {

    public
    List<Pair<SelectedVertex, Float>> selectFrom(List<CachedLine> here);

}
