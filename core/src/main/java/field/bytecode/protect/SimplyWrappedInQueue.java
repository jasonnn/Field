package field.bytecode.protect;

import field.bytecode.protect.instrumentation.DeferCallingFast;
import field.bytecode.protect.trampoline.TrampolineReflection;
import field.protect.asm.ASMMethod;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public
class SimplyWrappedInQueue extends DeferCallingFast {

    public
    SimplyWrappedInQueue(String name,
                         int access,
                         ASMMethod onMethod,
                         ClassVisitor classDelegate,
                         MethodVisitor delegateTo,
                         String signature,
                         HashMap<String, Object> parameters) {
        super(name, access, onMethod, classDelegate, delegateTo, signature, parameters);
    }

    Method original = null;

    @Override
    public
    Object handle(int fromName, final Object fromThis, final String originalMethod, final Object[] argArray) {

        if (original == null) {
            Method[] all = TrampolineReflection.getAllMethods(fromThis.getClass());
outer:
            for (Method m : all) {
                if (m.getName().equals(originalMethod)) {
                    Class<?>[] p = m.getParameterTypes();
                    if (p.length == argArray.length) {

                        for (int i = 0; i < p.length; i++) {
                            if (p[i].isPrimitive()) {
                                if ((p[i] == Integer.TYPE) && !(argArray[i] instanceof Integer)) continue outer;
                                if ((p[i] == Long.TYPE) && !(argArray[i] instanceof Long)) continue outer;
                                if ((p[i] == Short.TYPE) && !(argArray[i] instanceof Short)) continue outer;
                                if ((p[i] == Double.TYPE) && !(argArray[i] instanceof Double)) continue outer;
                                if ((p[i] == Byte.TYPE) && !(argArray[i] instanceof Byte)) continue outer;
                                if ((p[i] == Character.TYPE) && !(argArray[i] instanceof Character)) continue outer;
                                if ((p[i] == Float.TYPE) && !(argArray[i] instanceof Float)) continue outer;
                            }
                            else if (!((argArray[i] == null) || p[i].isAssignableFrom(argArray[i].getClass())))
                                continue outer;
                        }

                        original = m;
                        break;
                    }
                }
            }
            assert original != null : originalMethod;
        }

        if (original == null) {
            //System.out.println(" couldn't find <"+originalMethod+">");
        }
        original.setAccessible(true);

        boolean doSaveAliasing = (parameters.get("saveAliasing") == null) || (Boolean) parameters.get("saveAliasing");
        boolean doSaveContextLocation =
                (parameters.get("saveAliasing") == null) || (Boolean) parameters.get("saveAliasing");


        iWrappedExit e = ((iProvidesWrapping) fromThis).enter(original);
        Object o;
        try {
            o = original.invoke(fromThis, argArray);
            if (e != null) {
                return e.exit(o);
            }
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
            if (e != null) return e.exception(e1);
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
            if (e != null) return e.exception(e1);
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
            if (e != null) return e.exception(e1);
        }
        return null;
    }
}
