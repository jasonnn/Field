package field.core.ui.text.referencealgorithms;

import field.core.dispatch.IVisualElement;

import java.util.List;


public
class Left extends Above {
    @Override
    protected
    List<IVisualElement> doEvaluation(IVisualElement root, List<IVisualElement> old, IVisualElement forElement) {
        atPoint.set(0, 0.5f, 0);
        targetDirection.set(-1, 0, 0);
        otherDirection.set(0, 1, 0);
        return super.doEvaluation(root, old, forElement);
    }
}
