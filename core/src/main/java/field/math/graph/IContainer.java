package field.math.graph;

/**
* Created by jason on 7/29/14.
*/
public
interface IContainer<T, P extends IGraphNode<P>> extends IGraphNode<P> {
    public
    T payload();
}
