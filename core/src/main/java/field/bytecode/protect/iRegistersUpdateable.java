package field.bytecode.protect;

import field.launch.iUpdateable;

/**
 * Created by jason on 7/14/14.
 */
public
interface iRegistersUpdateable {
    public
    void deregisterUpdateable(iUpdateable up);

    public
    void registerUpdateable(iUpdateable up);
}
