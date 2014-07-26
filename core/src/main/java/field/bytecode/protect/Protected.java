package field.bytecode.protect;

import field.bytecode.protect.trampoline.StandardTrampoline;
import field.bytecode.protect.trampoline.TrampolineReflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


public
class Protected {

    public
    interface iShim {
        public
        Object run();
    }

    public static
    Object loadSerialized(String filename) {
        try {
            Class<?> c = StandardTrampoline.trampoline.loader.loadClass("field.bytecode.NonProtected");

            Method[] m = TrampolineReflection.getAllMethods(c);
            for (Method method : m) {
                if ("loadSerialized".equals(method.getName())) {
                    Object inst = c.newInstance();
                    return method.invoke(inst, filename);
                }
            }
            assert false : "no method called run in " + Arrays.asList(m);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        assert false;
        return null;
    }

    // doesn't work
    public static
    Object run(iShim r) {
        try {
            Class<?> c = StandardTrampoline.trampoline.loader.loadClass("field.bytecode.NonProtected");

            Method[] m = TrampolineReflection.getAllMethods(c);
            for (Method method : m) {
                if ("run".equals(method.getName())) {
                    Object inst = c.newInstance();
                    return method.invoke(inst, r);
                }
            }
            assert false : "no method called run in " + Arrays.asList(m);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        assert false;
        return null;
    }
}
