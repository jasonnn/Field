package field.bytecode.protect;

import field.launch.IUpdateable;

/**
 * Created by jason on 7/14/14.
 */
public
interface iRegistersUpdateable {
    public
    void deregisterUpdateable(IUpdateable up);

    public
    void registerUpdateable(IUpdateable up);
}
