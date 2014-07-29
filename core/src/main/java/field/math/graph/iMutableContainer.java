/**
 *
 */
package field.math.graph;


public
interface IMutableContainer<T, P extends IMutable<P>> extends IMutable<P>, IContainer<T, P> {
    public
    IMutableContainer<T, P> setPayload(T t);
}