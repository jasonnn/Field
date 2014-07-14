package field.launch;

import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;

public interface iUpdateable {

	static public final Method method_update = ReflectionTools.methodOf("update", iUpdateable.class);

	public void update();
}