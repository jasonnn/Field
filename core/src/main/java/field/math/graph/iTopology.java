package field.math.graph;

import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;
import java.util.List;


public
interface ITopology<T> {

    public
    List<T> getParentsOf(T of);

    public
    List<T> getChildrenOf(T of);

    public
    interface iHasTopology {
        public
        ITopology getTopology();
    }


}
