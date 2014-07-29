package field.bytecode.mirror.impl;

import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;

/**
 * Created by jason on 7/29/14.
 */
public abstract
class AbstractMirrorMethod {
    protected final Method method;

    public
    AbstractMirrorMethod(Class on, String name, Class...parameters) {
        method = ReflectionTools.methodOf(name, on, parameters);
        method.setAccessible(true);
    }

    public
    AbstractMirrorMethod(Method m) {
        this.method = m;
        this.method.setAccessible(true);
    }
}
