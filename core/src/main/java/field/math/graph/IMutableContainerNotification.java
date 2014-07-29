package field.math.graph;

/**
* Created by jason on 7/29/14.
*/
public
interface IMutableContainerNotification<T, P extends IMutable<P>> extends INotification<P> {
    public
    void payloadChanged(iMutableContainer<T, P> on, T to);
}
