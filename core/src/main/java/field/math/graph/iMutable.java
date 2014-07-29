package field.math.graph;

import java.util.List;

public
interface IMutable<P extends IMutable<P>> extends IGraphNode<P> {

    public
    List<? extends IMutable<P>> getParents();

    public
    void addChild(P newChild);

    public
    void notifyAddParent(IMutable<P> newParent);

    public
    void removeChild(P newChild);

    public
    void notifyRemoveParent(IMutable<P> newParent);

    public
    void beginChange();

    public
    void endChange();

    public
    void registerListener(INotification<IMutable<P>> note);

    public
    void deregisterListener(INotification<IMutable<P>> note);

    public
    void catchupListener(INotification<IMutable<P>> note);
}