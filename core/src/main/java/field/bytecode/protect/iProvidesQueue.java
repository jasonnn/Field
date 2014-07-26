package field.bytecode.protect;

import java.lang.reflect.Method;

/**
 * Created by jason on 7/14/14.
 */
public
interface iProvidesQueue {
    public
    iRegistersUpdateable getQueueFor(Method m);
}
