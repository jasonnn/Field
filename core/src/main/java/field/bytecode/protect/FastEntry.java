package field.bytecode.protect;

import field.bytecode.protect.asm.ASMMethod;
import field.bytecode.protect.dispatch.Run;
import field.bytecode.protect.instrumentation.CallOnEntryFast;
import field.bytecode.protect.dispatch.ReturnCode;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * there is a new FastEntry for each method of each class, it doesn't support multiple entry (calls to super, for example), but it sure is fast
 *
 * @author marc
 *
 */
final public class FastEntry extends CallOnEntryFast {
	public static Map<String, Map<String, FastEntry>> knownEntries = new HashMap<String, Map<String, FastEntry>>();

	static public void linkWith(Method method, Class clazz, Run run) {
		String methodDescriptor = Type.getMethodDescriptor(method);
		String methodName = method.getName();

		Map<String, FastEntry> map = knownEntries.get(clazz.getName());
		assert map != null;
		FastEntry entry = map.get(methodName + ":" + methodDescriptor);
		assert entry != null;

		if (entry.execute == null) entry.execute = new ArrayList<Run>();
		entry.execute.add(run);
	}

	static public void unlinkWith(Method method, Class clazz, Run run) {
		String methodDescriptor = Type.getMethodDescriptor(method);
		String methodName = method.getName();

		Map<String, FastEntry> map = knownEntries.get(clazz.getName());
		assert map != null;
		FastEntry entry = map.get(methodName + ":" + methodDescriptor);
		assert entry != null;

		if (entry.execute != null) entry.execute.remove(run);
		if ((entry.execute != null ? entry.execute.size() : 0) == 0) entry.execute = null;
	}

	List<Run> execute;

	public FastEntry(String name, int access, ASMMethod onMethod, MethodVisitor delegateTo, HashMap<String, Object> parameters, String className) {
		super(name, access, onMethod, delegateTo, parameters);

		Map<String, FastEntry> methodMap = knownEntries.get(className);
		if (methodMap == null) knownEntries.put(className, methodMap = new HashMap<String, FastEntry>());

		methodMap.put(onMethod.getName() + ":" + onMethod.getDescriptor(), this);
	}

	@Override
	public void handle(int fromName, Object fromThis, Object[] argArray) {
		if (execute == null) return;

        for (Run anExecute : execute) {
            ReturnCode code = anExecute.head(fromThis, argArray);
            // if (code == ReturnCode.STOP) return;
        }
	}
}
