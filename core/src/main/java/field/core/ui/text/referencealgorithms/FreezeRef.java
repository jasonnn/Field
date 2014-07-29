package field.core.ui.text.referencealgorithms;

import field.core.dispatch.IVisualElement;
import field.core.ui.text.embedded.iReferenceAlgorithm.BaseReferenceAlgorithm;

import java.util.List;


public
class FreezeRef extends BaseReferenceAlgorithm {
    @Override
    protected
    List<IVisualElement> doEvaluation(IVisualElement root, List<IVisualElement> old, IVisualElement forElement) {
        return old;
    }
}
