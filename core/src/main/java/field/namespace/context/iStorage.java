package field.namespace.context;

import field.bytecode.protect.BaseRef;
import field.math.graph.visitors.hint.TraversalHint;

/**
 *
 */ // some useful interfaces for t_Interface's to
// implement
public
interface IStorage<T> {
    public
    TraversalHint get(String name, BaseRef<? super T> result);

    public
    TraversalHint set(String name, BaseRef<? extends T> value);

    public
    TraversalHint unset(String key);
}
