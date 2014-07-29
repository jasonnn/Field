package field.math.graph;

import java.util.List;

/**
 * strong because you can go up and down
 *
 * @author marc
 */
public
interface IGraphNode<X extends IGraphNode<X>> {

    public
    List<? extends IGraphNode<X>> getParents();

    public
    List<X> getChildren();

}
