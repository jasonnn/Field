package field.core.ui.text.referencealgorithms;

import field.core.dispatch.iVisualElement;
import field.core.dispatch.iVisualElement.Rect;
import field.core.ui.text.embedded.iReferenceAlgorithm.BaseReferenceAlgorithm;
import field.math.linalg.Vector2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public
class Explicit extends BaseReferenceAlgorithm implements Comparator<iVisualElement> {


    private Vector2 center;
    private String hiddenUID;

    Rect tFrame = new Rect(0, 0, 0, 0);

    public
    Explicit(String hiddenUID) {
        this.hiddenUID = hiddenUID.substring(1, hiddenUID.length() - 1);
        this.hiddenUID = this.hiddenUID.replace(".", ":");
    }

    public
    int compare(iVisualElement o1, iVisualElement o2) {
        float d1 = o1.getFrame(tFrame).midpoint2().distanceFrom(center);
        float d2 = o2.getFrame(tFrame).midpoint2().distanceFrom(center);
        return (d2 < d1) ? 1 : -1;
    }

    @Override
    protected
    List<iVisualElement> doEvaluation(iVisualElement root, List<iVisualElement> old, iVisualElement forElement) {
        ArrayList<iVisualElement> r = new ArrayList<iVisualElement>();

        List<iVisualElement> all = allVisualElements(root);


        for (iVisualElement v : all) {
            if (v.getUniqueID().equals(hiddenUID)) r.add(v);
        }

        return r;
    }
}
