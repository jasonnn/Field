package field.math.abstraction;

import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;


public interface iAcceptor<T> {

	public static final Method method_set = ReflectionTools.methodOf("set", iAcceptor.class, Object.class);
	
	public iAcceptor<T> set(T to);
}
