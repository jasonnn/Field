package field.launch;

import field.internal.UncheckedReflection;

import java.lang.reflect.Method;

public
interface iUpdateable {

    public static final Method UPDATE_METHOD = UncheckedReflection.getDeclaredMethod(iUpdateable.class, "update");


    public
    void update();
}