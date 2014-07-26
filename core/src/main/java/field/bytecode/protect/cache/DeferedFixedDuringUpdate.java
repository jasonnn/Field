package field.bytecode.protect.cache;

import field.bytecode.protect.instrumentation.DeferCallingFast;
import field.bytecode.protect.trampoline.TrampolineReflection;
import field.launch.Launcher;
import field.launch.iUpdateable;
import field.protect.asm.ASMMethod;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * a type of cache of a method that cannot change inside a single update. Cached
 * statically, single parameter only, by speed
 */
public
class DeferedFixedDuringUpdate extends DeferCallingFast {

    private Method original;

    long count = 0;
    long lastCount = 0;

    WeakHashMap<Object, Object> cache = new WeakHashMap<Object, Object>();

    public
    DeferedFixedDuringUpdate(String name,
                             int access,
                             ASMMethod method,
                             ClassVisitor delegate,
                             MethodVisitor to,
                             String signature,
                             HashMap<String, Object> parameters) {
        super(name, access, method, delegate, to, signature, parameters);
        Launcher.getLauncher().registerUpdateable(new iUpdateable() {

            public
            void update() {
                count++;
            }
        });
    }

    Object[] zeroArgs = {};

    @Override
    public
    Object handle(int fromName, Object fromThis, String originalMethod, Object[] args) {
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
//		assert args.length == 1;

        if (lastCount != count) {
            cache.clear();
            lastCount = count;
        }

        Object c = null;
        if (args.length == 1) c = cache.get(args[0]);
        else c = cache.get(zeroArgs);

        if (c != null) return c;
        try {
            Object o = original.invoke(fromThis, args);
            cache.put((args.length == 1) ? args[0] : zeroArgs, o);
            return o;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw e;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            IllegalArgumentException ll = new IllegalArgumentException();
            ll.initCause(e);
            throw ll;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            IllegalArgumentException ll = new IllegalArgumentException();
            ll.initCause(e);
            throw ll;
        }

    }

}
