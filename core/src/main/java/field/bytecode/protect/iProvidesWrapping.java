package field.bytecode.protect;

import java.lang.reflect.Method;

/**
 * Created by jason on 7/15/14.
 */
public
interface iProvidesWrapping {
    public
    iWrappedExit enter(Method m);
}
