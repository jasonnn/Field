package field.namespace.context;

import field.bytecode.protect.BaseRef;
import field.math.graph.visitors.GraphNodeSearching;

/**
 * Created by jason on 7/14/14.
 */ // some useful interfaces for t_Interface's to
// implement
public
interface iStorage<T> {
    public
    GraphNodeSearching.VisitCode get(String name, BaseRef<? super T> result);

    public
    GraphNodeSearching.VisitCode set(String name, BaseRef<? extends T> value);

    public
    GraphNodeSearching.VisitCode unset(String key);
}
