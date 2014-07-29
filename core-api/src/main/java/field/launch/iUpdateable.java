package field.launch;

import field.internal.UncheckedReflection;

import java.lang.reflect.Method;

public
interface IUpdateable {

    public static final Method UPDATE_METHOD = UncheckedReflection.getDeclaredMethod(IUpdateable.class, "update");


    public
    void update();
}