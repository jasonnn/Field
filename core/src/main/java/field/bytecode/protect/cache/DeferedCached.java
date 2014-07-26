/**
 *
 */
package field.bytecode.protect.cache;

import field.bytecode.protect.annotations.CacheParameter;
import field.bytecode.protect.instrumentation.DeferCallingFast;
import field.bytecode.protect.instrumentation2.ASMAnnotatedMethodCtx;
import field.bytecode.protect.trampoline.TrampolineReflection;
import field.protect.asm.ASMMethod;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class DeferedCached extends DeferCallingFast {

    public static DeferedCached fromCtx(ASMAnnotatedMethodCtx ctx, String name) {
        return new DeferedCached(name,
                ctx.access,
                new ASMMethod(ctx.name, ctx.desc),
                ctx.classCtx.cv,
                ctx.delegate,
                ctx.signature,
                ctx.params);
    }

    //private final HashMap<String, Object> parameters;

    Map<ImmutableArrayWrapper, Object> cache;

    List<Field> implicatedFields = null;

    Method original = null;

    public DeferedCached(String name, int access, ASMMethod method, ClassVisitor delegate, MethodVisitor to, String signature, Map<String, Object> parameters) {
        super(name, access, method, delegate, to, signature, parameters);

        final Integer max = (Integer) parameters.get("max");
        if ((max == null) || (max == -1)) {
            cache = new WeakHashMap<ImmutableArrayWrapper, Object>();
        } else {
            cache = new LinkedHashMap<ImmutableArrayWrapper, Object>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry<ImmutableArrayWrapper, Object> eldest) {
                    return size() > max;
                }
            };
        }
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
        if (implicatedFields == null) {
            implicatedFields = new ArrayList<Field>();
            Field[] allFields = TrampolineReflection.getAllFields(fromThis.getClass());
            for (Field f : allFields) {

                CacheParameter ann = f.getAnnotation(CacheParameter.class);
                if (ann != null) {
                    if (((ann.name() == null) && (parameters.get("name") == null)) || ann.name().equals(parameters.get("name"))) {
                        f.setAccessible(true);
                        implicatedFields.add(f);
                    }
                }
            }
        }
        if (implicatedFields.isEmpty()) {

            // first check the cache
            ImmutableArrayWrapper iaw = new ImmutableArrayWrapper(argArray, false);

            Object object = cache.get(iaw);
            if ((object == null) && !cache.containsKey(iaw)) {
                try {
//					;//System.out.println(" cache miss");
                    cache.put(iaw, object = original.invoke(fromThis, argArray));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
//				;//System.out.println(" cache hit");
            }
            return object;
        }
        Object[] na = new Object[argArray.length + implicatedFields.size()];
        System.arraycopy(argArray, 0, na, 0, argArray.length);
        for (int i = 0; i < implicatedFields.size(); i++)
            try {
                na[argArray.length + i] = implicatedFields.get(i).get(fromThis);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        ImmutableArrayWrapper iaw = new ImmutableArrayWrapper(na, false);

        Object object = cache.get(iaw);
        if ((object == null) && !cache.containsKey(iaw)) {
            try {
                cache.put(iaw, object = original.invoke(fromThis, argArray));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
//		else
//			System.err.println(" cache hit ");
//		
        return object;

    }
}