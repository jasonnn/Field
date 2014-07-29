package field.bytecode.mirror;

import field.namespace.generic.IFunction;

import java.util.Collection;

/**
* Created by jason on 7/29/14.
*/
public
interface IMethodFunction<t_class, t_returns, t_accepts> {
    public
    <A extends t_class> IFunction<t_accepts, t_returns> function(A to);

    public
    <A extends t_class> IFunction<t_accepts, Collection<? extends t_returns>> function(final Collection<A> to);
}
