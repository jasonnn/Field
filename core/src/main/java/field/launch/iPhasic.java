package field.launch;

import field.bytecode.protect.iInside;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Method;


public interface iPhasic extends iUpdateable, iInside{

	static public final Method method_begin = ReflectionTools.methodOf("begin", iPhasic.class);
	static public final Method method_end= ReflectionTools.methodOf("end", iPhasic.class);
	static public final Method method_rebegin = ReflectionTools.methodOf("rebegin", iPhasic.class);
	
	public void begin();
	public void end();
	public void rebegin();
	
}
