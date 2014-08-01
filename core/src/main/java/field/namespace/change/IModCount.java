package field.namespace.change;

import java.io.Serializable;

/**
* Created by jason on 7/31/14.
*/
public
interface IModCount extends Serializable {
    public
    IModCount setRecompute(IChangable.iRecompute r);

    public
    Object data();

    public
    Object data(IChangable.iRecompute recompute);

    public
    boolean hasChanged();

    public
    IModCount clear(Object newData);

    public
    IModCount localChainWith(IModCount[] also);
}
