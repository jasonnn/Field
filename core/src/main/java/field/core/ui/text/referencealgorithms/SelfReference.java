package field.core.ui.text.referencealgorithms;

import field.core.dispatch.IVisualElement;
import field.core.ui.text.embedded.iReferenceAlgorithm;

import java.util.ArrayList;
import java.util.List;


public
class SelfReference extends iReferenceAlgorithm.BaseReferenceAlgorithm {

    @Override
    protected
    List<IVisualElement> doEvaluation(IVisualElement root, List<IVisualElement> old, IVisualElement forElement) {
        ArrayList<IVisualElement> r = new ArrayList<IVisualElement>();
        r.add(forElement);
        r.add(forElement);
        return r;
    }
}
