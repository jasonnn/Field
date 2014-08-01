package field.core.execution;

import field.core.dispatch.VisualElementProperty;
import field.core.execution.PythonScriptingSystem.Promise;
import field.math.abstraction.IFloatProvider;

public
interface IExecutesPromise {

    public static final VisualElementProperty<IExecutesPromise> promiseExecution =
            new VisualElementProperty<IExecutesPromise>("promiseExecution_");

    public abstract
    void addActive(IFloatProvider timeProvider, Promise p);

    public abstract
    void removeActive(Promise p);

    public
    void stopAll(float t);

}