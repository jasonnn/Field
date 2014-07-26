package field.core.ui.text.referencealgorithms;

import field.core.dispatch.iVisualElement;

import java.util.List;


public
class Right extends Above {
    @Override
    protected
    List<iVisualElement> doEvaluation(iVisualElement root, List<iVisualElement> old, iVisualElement forElement) {
        atPoint.set(1, 0.5f, 0);
        targetDirection.set(1, 0, 0);
        otherDirection.set(0, 1, 0);
        return super.doEvaluation(root, old, forElement);
    }
}
