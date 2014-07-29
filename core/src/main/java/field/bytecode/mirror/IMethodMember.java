package field.bytecode.mirror;

import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;

/**
* Created by jason on 7/29/14.
*/
public
interface IMethodMember<t_class, t_is> {
    public
    <A extends t_class> IAcceptor<t_is> acceptor(A to);

    public
    <A extends t_class> IProvider<t_is> provider(A to);
}
