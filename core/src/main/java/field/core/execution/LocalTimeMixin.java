package field.core.execution;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.override.DefaultOverride;
import field.core.dispatch.Mixins;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.Ref;
import field.core.execution.PythonScriptingSystem.Promise;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;

public
class LocalTimeMixin extends DefaultOverride {

    public static
    void mixin(IVisualElement e) {
        new Mixins().mixInOverride(LocalTimeMixin.class, e);
    }

    public VisualElementProperty<PythonScriptingSystem> localScriptingSystem =
            new VisualElementProperty<PythonScriptingSystem>("localPSS_");

    @Override
    public
    <T> TraversalHint getProperty(IVisualElement source, VisualElementProperty<T> prop, Ref<T> ref) {
        if (isChild(source)) {
            if (prop.equals(PythonScriptingSystem.pythonScriptingSystem)) {
                PythonScriptingSystem overrides = forElement.getProperty(localScriptingSystem);

                if (overrides == null) {
                    overrides = makeLocalPSS();
                    if (overrides != null) {
                        forElement.setProperty(localScriptingSystem, overrides);
                    }
                }

                if (overrides != null) {

                    PythonScriptingSystem parent = PythonScriptingSystem.pythonScriptingSystem.get(forElement);
                    Promise oldPromise = parent.revokePromise(source);
                    if (oldPromise != null) {
                        overrides.promisePythonScriptingElement(source, oldPromise);
                    }
                    ref.set((T) overrides);
                    return StandardTraversalHint.STOP;
                }
            }
        }
        return super.getProperty(source, prop, ref);
    }

    private
    boolean isChild(IVisualElement source) {
        return forElement.getParents().contains(source);
    }

    protected static
    PythonScriptingSystem makeLocalPSS() {
        return new PythonScriptingSystem();
    }

}
