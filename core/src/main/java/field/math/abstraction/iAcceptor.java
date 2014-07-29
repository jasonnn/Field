package field.math.abstraction;

import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;


public
interface IAcceptor<T> {

    public static final Method SET_METHOD = ReflectionTools.methodOf("set", IAcceptor.class, Object.class);

    public
    IAcceptor<T> set(T to);
}
