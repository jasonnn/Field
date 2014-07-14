package field.core.ui.text.referencealgorithms;

import field.core.dispatch.iVisualElement;
import field.core.ui.text.embedded.iReferenceAlgorithm.BaseReferenceAlgorithm;

import java.util.List;


public class FreezeRef extends BaseReferenceAlgorithm {
	@Override
	protected List<iVisualElement> doEvaluation(iVisualElement root, List<iVisualElement> old, iVisualElement forElement) {
		return old;
	}
}
