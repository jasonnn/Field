package field.core.plugins.drawing.tweak;

import field.core.dispatch.IVisualElement;
import field.core.plugins.drawing.opengl.CachedLine;
import field.core.plugins.drawing.tweak.AbsoluteTool.AbsoluteNodeDescription;
import field.core.plugins.drawing.tweak.TweakSplineCodeGen.BaseTool;
import field.core.plugins.drawing.tweak.TweakSplineCodeGen.iCoordDesc;
import field.core.plugins.drawing.tweak.TweakSplineCodeGen.iResult;
import field.core.plugins.drawing.tweak.TweakSplineUI.MouseInfo;
import field.core.plugins.drawing.tweak.TweakSplineUI.SelectedVertex;
import field.core.ui.PopupTextBox;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public
class ExtractPathTool extends BaseTool {

    String name;


    // todo, need to "preclean" obtainExpressions( ... selected ...) so that we only get one path extraction regardless of selection

    public
    class ExtractPathCoordinateChange implements iCoordDesc {
        private final List<SelectedVertex> totalClaimed = new ArrayList<SelectedVertex>();

        public
        iResult describe(List<CachedLine> index, List<SelectedVertex> claimed, MouseInfo mi) {
            if (claimed.size() == 0) return null;

            final SelectedVertex c = claimed.remove(0);
            totalClaimed.add(c);
            Iterator<SelectedVertex> i = claimed.iterator();
            while (i.hasNext()) {
                SelectedVertex n = i.next();
                if (n.onLine == c.onLine) {
                    claimed.add(n);
                    i.remove();
                }
            }

            return new iResult() {
                public
                List<SelectedVertex> getClaimedVertex() {
                    return totalClaimed;
                }

                public
                String toExpression() {
                    return "ExtractPath(\"" + name + "\")";
                }

                public
                void toProperties(IVisualElement e, Map<String, Object> soFar) {
                }
            };
        }
    }

    public
    ExtractPathTool() {

        nodes.add(new AbsoluteNodeDescription());
        coords.add(new ExtractPathCoordinateChange());
    }

    static public int uniq = 0;

    @Override
    public
    void populateParameters(IVisualElement inside, final IUpdateable continuation) {
        // fixme, not that uniq
        PopupTextBox.Modal.getString(PopupTextBox.Modal.elementAt(inside),
                                     "name for extraction :",
                                     "extracted path " + (uniq++),
                                     new IAcceptor<String>() {

                                         public
                                         IAcceptor<String> set(String to) {
                                             name = to;
                                             continuation.update();
                                             return this;
                                         }
                                     });
    }
}
