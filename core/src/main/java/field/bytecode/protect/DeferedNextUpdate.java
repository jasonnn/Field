package field.bytecode.protect;

import field.bytecode.protect.instrumentation.DeferCallingFast;
import field.bytecode.protect.trampoline.TrampolineReflection;
import field.launch.Launcher;
import field.launch.iUpdateable;
import field.protect.asm.ASMMethod;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public class DeferedNextUpdate extends DeferCallingFast {

	public DeferedNextUpdate(String name, int access, ASMMethod onMethod, ClassVisitor classDelegate, MethodVisitor delegateTo, String signature, HashMap<String, Object> parameters) {
		super(name, access, onMethod, classDelegate, delegateTo, signature, parameters);
	}

	Method original = null;

	@Override
	public Object handle(int fromName, final Object fromThis, final String originalMethod, final Object[] argArray) {

		if (original == null) {
			Method[] all = TrampolineReflection.getAllMethods(fromThis.getClass());
			for (Method m : all) {
				if (m.getName().equals(originalMethod)) {
					original = m;
					break;
				}
			}
			assert original != null : originalMethod;
		}
		original.setAccessible(true);

		boolean doSaveAliasing = parameters.get("saveAliasing") == null || (Boolean) parameters.get("saveAliasing");
		boolean doSaveContextLocation = parameters.get("saveAliasing") == null || (Boolean) parameters.get("saveAliasing");

		
		
		iUpdateable u = new iUpdateable() {

			public void update() {
				try {
										
					original.invoke(fromThis, argArray);
				} catch (IllegalArgumentException e) {
					System.err.println(" inside deferednextupdate +" + fromThis + " " + originalMethod + " " + Arrays.asList(argArray));
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					System.err.println(" inside deferednextupdate +" + fromThis + " " + originalMethod + " " + Arrays.asList(argArray));
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					System.err.println(" inside deferednextupdate +" + fromThis + " " + originalMethod + " " + Arrays.asList(argArray));
					e.printStackTrace();
				} catch (Throwable t) {
					System.err.println(" inside deferednextupdate +" + fromThis + " " + originalMethod + " " + Arrays.asList(argArray));
					t.printStackTrace();
				}
				
				Launcher.getLauncher().deregisterUpdateable(this);
			}
		};
		Launcher.getLauncher().registerUpdateable(u);

		return null;
	}
}
