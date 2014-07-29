package field.launch;

import field.bytecode.protect.iInside;
import field.internal.UncheckedReflection;

import java.lang.reflect.Method;


public
interface iPhasic extends IUpdateable, iInside {

    public static final Method method_begin = UncheckedReflection.getDeclaredMethod(iPhasic.class,"begin");
    public static final Method method_end = UncheckedReflection.getDeclaredMethod(iPhasic.class,"end");
    public static final Method method_rebegin = UncheckedReflection.getDeclaredMethod(iPhasic.class,"rebegin");

    public
    void begin();

    public
    void end();

    public
    void rebegin();

}
