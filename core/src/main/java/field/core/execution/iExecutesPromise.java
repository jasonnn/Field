package field.core.execution;

import field.core.dispatch.IVisualElement.VisualElementProperty;
import field.core.execution.PythonScriptingSystem.Promise;
import field.math.abstraction.IFloatProvider;

public
interface iExecutesPromise {

    public static final VisualElementProperty<iExecutesPromise> promiseExecution =
            new VisualElementProperty<iExecutesPromise>("promiseExecution_");

    public abstract
    void addActive(IFloatProvider timeProvider, Promise p);

    public abstract
    void removeActive(Promise p);

    public
    void stopAll(float t);

}