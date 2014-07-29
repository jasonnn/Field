package field.bytecode.mirror;

import field.namespace.generic.IFunction;

import java.util.Collection;

/**
* Created by jason on 7/29/14.
*/
public
interface IMethodFunction<E, t_accepts, t_returns> {
    public
    <A extends E> IFunction<t_accepts, t_returns> function(A to);

    public
    <A extends E> IFunction<t_accepts, Collection<? extends t_returns>> function(final Collection<A> to);
}
