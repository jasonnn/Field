package field.launch;

import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;

public interface iUpdateable {

	public static final Method method_update = ReflectionTools.methodOf("update", iUpdateable.class);

	public void update();
}