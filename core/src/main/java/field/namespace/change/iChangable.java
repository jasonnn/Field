package field.namespace.change;

import java.io.Serializable;

/**
 * @author marc
 *         Created on May 6, 2003
 */
public
interface IChangable extends Serializable {
    public
    IModCount getModCount(Object withRespectTo);

}
