package field.bytecode.protect;

import field.bytecode.protect.instrumentation.DeferCallingFast;
import field.bytecode.protect.trampoline.TrampolineReflection;
import field.protect.asm.ASMMethod;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;


public class DeferedNewThread extends DeferCallingFast {

	private int priority=0;

	Method original = null;
	public DeferedNewThread(String name, int access, ASMMethod onMethod, ClassVisitor classDelegate, MethodVisitor delegateTo, String signature, HashMap<String, Object> parameters) {
		super(name, access, onMethod, classDelegate, delegateTo, signature, parameters);

		Integer p = (Integer)parameters.get("priority");
		if (p==null)
			priority=0;
		else priority=p;
	}

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
			assert original!=null : originalMethod;
		}
		original.setAccessible(true);

		Thread t = new Thread(new Runnable() {

			public void run() {
				try {
					original.invoke(fromThis, argArray);
				} catch (IllegalArgumentException e) {
					System.err.println(" inside deferednewthread +"+fromThis+ ' ' +originalMethod+ ' ' +Arrays.asList(argArray));
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					System.err.println(" inside deferednewthread +"+fromThis+ ' ' +originalMethod+ ' ' +Arrays.asList(argArray));
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					System.err.println(" inside deferednewthread +"+fromThis+ ' ' +originalMethod+ ' ' +Arrays.asList(argArray));
					e.printStackTrace();
				} catch (Throwable t)
				{
					System.err.println(" inside deferednewthread +"+fromThis+ ' ' +originalMethod+ ' ' +Arrays.asList(argArray));
					t.printStackTrace();
				}
			}

		});

		t.setPriority((priority == 0)
                      ? Thread.NORM_PRIORITY
                      : ((priority < 0) ? Thread.MIN_PRIORITY : Thread.MAX_PRIORITY));

		t.start();

		return null;
	}

}
