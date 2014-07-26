package field.core.ui.text.referencealgorithms;

import field.core.dispatch.iVisualElement;
import field.core.ui.text.embedded.iReferenceAlgorithm;

import java.util.ArrayList;
import java.util.List;


public
class SelfReference extends iReferenceAlgorithm.BaseReferenceAlgorithm {

    @Override
    protected
    List<iVisualElement> doEvaluation(iVisualElement root, List<iVisualElement> old, iVisualElement forElement) {
        ArrayList<iVisualElement> r = new ArrayList<iVisualElement>();
        r.add(forElement);
        r.add(forElement);
        return r;
    }
}
