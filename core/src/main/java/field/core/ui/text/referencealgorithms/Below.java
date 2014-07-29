package field.core.ui.text.referencealgorithms;

import field.core.dispatch.IVisualElement;

import java.util.List;


public
class Below extends Above {
    @Override
    protected
    List<IVisualElement> doEvaluation(IVisualElement root, List<IVisualElement> old, IVisualElement forElement) {
        atPoint.set(0.5f, 1, 0);
        targetDirection.set(0, 1, 0);
        otherDirection.set(1, 0, 0);
        return super.doEvaluation(root, old, forElement);
    }
}
