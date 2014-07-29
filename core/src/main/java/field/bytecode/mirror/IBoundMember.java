package field.bytecode.mirror;

import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;

/**
* Created by jason on 7/29/14.
*/
public
interface IBoundMember<T> extends IProvider<T>, IAcceptor<T> {
}
