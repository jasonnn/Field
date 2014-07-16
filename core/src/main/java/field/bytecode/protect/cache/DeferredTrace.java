/**
 *
 */
package field.bytecode.protect.cache;

import field.bytecode.protect.asm.ASMMethod;
import field.bytecode.protect.instrumentation.DeferCallingFast;
import field.bytecode.protect.trampoline.TrampolineReflection;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public final class DeferredTrace extends DeferCallingFast {

	private final HashMap<String, Object> parameters;
	Method original = null;

	public DeferredTrace(String name, int access, ASMMethod method, ClassVisitor delegate, MethodVisitor to, String signature, HashMap<String, Object> parameters) {
		super(name, access, method, delegate, to, signature, parameters);
		this.parameters = parameters;
	}

	@Override
	public Object handle(int fromName, Object fromThis, String originalMethod, Object[] argArray) {
		if (original == null) {
			Method[] all = TrampolineReflection.getAllMethods(fromThis.getClass());
			for (Method m : all) {
				if (m.getName().equals(originalMethod)) {
					original = m;
					break;
				}
			}
			original.setAccessible(true);
			assert original != null : originalMethod;
		}
		Object object;
		boolean success = false;
		try{

            //System.out.println(">> "+originalMethod+" ["+Arrays.asList(argArray)+"] "+System.identityHashCode(fromThis));
            object = original.invoke(fromThis, argArray);
            //System.out.println("<< "+originalMethod+" ("+object+") "+System.identityHashCode(fromThis));
            success = true;
			return object;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		finally
		{
			if (!success)
				;//System.out.println("<< (exception) "+originalMethod+" "+System.identityHashCode(fromThis));
		}
		return null;
	}
}