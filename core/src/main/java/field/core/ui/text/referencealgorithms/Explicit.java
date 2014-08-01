package field.core.ui.text.referencealgorithms;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.ui.text.embedded.iReferenceAlgorithm.BaseReferenceAlgorithm;
import field.math.linalg.Vector2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public
class Explicit extends BaseReferenceAlgorithm implements Comparator<IVisualElement> {


    private Vector2 center;
    private String hiddenUID;

    Rect tFrame = new Rect(0, 0, 0, 0);

    public
    Explicit(String hiddenUID) {
        this.hiddenUID = hiddenUID.substring(1, hiddenUID.length() - 1);
        this.hiddenUID = this.hiddenUID.replace(".", ":");
    }

    public
    int compare(IVisualElement o1, IVisualElement o2) {
        float d1 = o1.getFrame(tFrame).midpoint2().distanceFrom(center);
        float d2 = o2.getFrame(tFrame).midpoint2().distanceFrom(center);
        return (d2 < d1) ? 1 : -1;
    }

    @Override
    protected
    List<IVisualElement> doEvaluation(IVisualElement root, List<IVisualElement> old, IVisualElement forElement) {
        ArrayList<IVisualElement> r = new ArrayList<IVisualElement>();

        List<IVisualElement> all = allVisualElements(root);


        for (IVisualElement v : all) {
            if (v.getUniqueID().equals(hiddenUID)) r.add(v);
        }

        return r;
    }
}
