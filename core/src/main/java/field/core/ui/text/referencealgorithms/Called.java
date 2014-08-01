package field.core.ui.text.referencealgorithms;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.ui.text.embedded.iReferenceAlgorithm.BaseReferenceAlgorithm;
import field.math.linalg.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;


public
class Called extends BaseReferenceAlgorithm implements Comparator<IVisualElement> {

    private final Pattern pattern;

    private Vector2 center;

    public
    Called(String reg) {
        pattern = Pattern.compile(reg);
    }

    @Override
    protected
    List<IVisualElement> doEvaluation(IVisualElement root, List<IVisualElement> old, IVisualElement forElement) {
        ArrayList<IVisualElement> r = new ArrayList<IVisualElement>();

        List<IVisualElement> all = allVisualElements(root);

        center = forElement.getFrame(null).midpoint2();

        for (IVisualElement v : all) {
            if (pattern.matcher(v.getProperty(IVisualElement.name)).matches()) r.add(v);
        }

        Collections.sort(r, this);

        return r;
    }

    Rect tFrame = new Rect(0, 0, 0, 0);

    public
    int compare(IVisualElement o1, IVisualElement o2) {
        float d1 = o1.getFrame(tFrame).midpoint2().distanceFrom(center);
        float d2 = o2.getFrame(tFrame).midpoint2().distanceFrom(center);
        return d2 < d1 ? 1 : -1;
    }
}
