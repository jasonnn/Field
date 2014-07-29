package field.core.ui.text.referencealgorithms;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.VisualElement;
import field.core.ui.text.embedded.iReferenceAlgorithm;
import field.math.linalg.Vector3;
import field.namespace.generic.ReflectionTools;

import java.util.ArrayList;
import java.util.List;


public
class Above extends iReferenceAlgorithm.BaseReferenceAlgorithm {

    Vector3 atPoint = new Vector3(0.5f, 0, 0f);
    Vector3 targetDirection = new Vector3(0, -1, 0);
    Vector3 otherDirection = new Vector3(1, 0, 0);

    @Override
    protected
    List<IVisualElement> doEvaluation(IVisualElement root, List<IVisualElement> old, IVisualElement forElement) {
        ArrayList<IVisualElement> r = new ArrayList<IVisualElement>();

        List<IVisualElement> all = allVisualElements(root);

        IVisualElement.Rect frame = forElement.getFrame(null);


        final Vector3 targetPoint = frame.relativize(atPoint);
        IVisualElement best = ReflectionTools.argMin(all, new Object() {
            public
            float distance(IVisualElement e) {

                if (e instanceof VisualElement) {
                    IVisualElement.Rect otherFrame = e.getFrame(null);
                    Vector3 m = otherFrame.midPoint();

                    m.sub(targetPoint);

                    float d1 = m.dot(targetDirection);
                    if (d1 < 0) return Float.POSITIVE_INFINITY;
                    float d2 = Math.abs(m.dot(otherDirection));
                    return d1 + d2;
                }
                return Float.POSITIVE_INFINITY;
            }
        });

        if (best != null) r.add(best);

        return r;
    }
}
