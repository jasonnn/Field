package field.namespace.change;

import java.io.Serializable;

/**
* Created by jason on 7/31/14.
*/
public
interface IModCount extends Serializable {
    public
    IModCount setRecompute(IRecompute r);

    public
    Object data();

    public
    Object data(IRecompute recompute);

    public
    boolean hasChanged();

    public
    IModCount clear(Object newData);

    public
    IModCount localChainWith(IModCount[] also);
}
