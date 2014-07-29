package field.core.plugins.constrain;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.IVisualElementOverrides;
import field.core.dispatch.IVisualElement.Rect;
import field.core.dispatch.IVisualElement.VisualElementProperty;
import field.core.plugins.constrain.cassowary.ClConstraint;
import field.math.graph.visitors.hint.TraversalHint;

import java.util.Map;


public abstract
class BaseConstraintOverrides extends IVisualElementOverrides.DefaultOverride {

    public static final VisualElementProperty<Map<String, IVisualElement>> constraintParameters =
            new VisualElementProperty<Map<String, IVisualElement>>("constraintParameters");

    private ComplexConstraints cachedComplex;

    boolean constraintsHaveChanged = false;


    @Override
    public
    TraversalHint paintNow(IVisualElement source, Rect bounds, boolean visible) {
        ensureConstraint();
        if (source == this.forElement) {
            paint(bounds, visible);
        }

        return super.paintNow(source, bounds, visible);
    }

    @Override
    public
    <T> TraversalHint setProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> to) {
        if ((source == forElement) && prop.equals(constraintParameters)) {
            constraintsHaveChanged = true;
            forElement.setProperty(IVisualElement.dirty, true);
        }
        return super.setProperty(source, prop, to);
    }

    protected abstract
    ClConstraint createConstraint(Map<String, IVisualElement> property);

    protected
    void ensureConstraint() {
        ComplexConstraints cc = getComplexConstraintsPlugin();
        if (cc != null || constraintsHaveChanged) {
            ClConstraint constraint = cc.getConstraintForElement(forElement);
            if (constraint == null || constraintsHaveChanged) {
                constraint = createConstraint(getConstraintParameters());

                assert constraint != null;

                cc.setConstraintForElement(forElement, constraint);
            }
            constraintsHaveChanged = false;
        }
    }

    protected
    ComplexConstraints getComplexConstraintsPlugin() {
        if (cachedComplex != null) return cachedComplex;
        ComplexConstraints c1 = ComplexConstraints.complexConstraints_plugin.get(forElement);
        return cachedComplex = c1;
    }

    protected
    Map<String, IVisualElement> getConstraintParameters() {
        return forElement.getProperty(constraintParameters);
    }

    protected abstract
    void paint(Rect bounds, boolean visible);


}