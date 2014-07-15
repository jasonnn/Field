/**
 * 
 */
package field.bytecode.protect;

import field.bytecode.protect.cache.ModCountArrayWrapper;
import field.bytecode.protect.cache.ModCountCache;
import field.bytecode.protect.instrumentation.DeferCallingFast;
import field.bytecode.protect.trampoline.TrampolineReflection;
import field.namespace.generic.Bind.iFunction;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Method;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public final class DeferedModCountCached extends DeferCallingFast implements iFunction<Object, ModCountArrayWrapper> {

	ModCountCache<ModCountArrayWrapper, Object> cache = new ModCountCache<ModCountArrayWrapper, Object>(new ModCountArrayWrapper(null));

	java.lang.reflect.Method original = null;

	private final HashMap<String, Object> parameters;

	private java.lang.reflect.Method ongoingMethod;

	private Object[] ongoingArgs;

	private ModCountArrayWrapper ongoingIAW;

	private Object originalTarget;

	public DeferedModCountCached(String name, int access, Method method, ClassVisitor delegate, MethodVisitor to, String signature, HashMap<String, Object> parameters) {
		super(name, access, method, delegate, to, signature, parameters);
		this.parameters = parameters;
	}

	public Object handle(int fromName, Object fromThis, String originalMethod, Object[] argArray) {
		if (original == null) {
			java.lang.reflect.Method[] all = TrampolineReflection.getAllMethods(fromThis.getClass());
			for (java.lang.reflect.Method m : all) {
				if (m.getName().equals(originalMethod)) {
					original = m;
					break;
				}
			}
			original.setAccessible(true);
			assert original != null : originalMethod;
		}

		Object[] na = new Object[argArray.length];
		System.arraycopy(argArray, 0, na, 0, argArray.length);

		ModCountArrayWrapper iaw = new ModCountArrayWrapper(na);

		ongoingMethod = original;
		ongoingArgs = argArray;
		ongoingIAW = iaw;
		originalTarget = fromThis;

		Object object = cache.get(iaw, this);

		return object;

	}

	public Object f(ModCountArrayWrapper in) {
		try {
			Object object = original.invoke(originalTarget, ongoingArgs);
			return object;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
}