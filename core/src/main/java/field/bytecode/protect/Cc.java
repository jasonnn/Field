package field.bytecode.protect;

import field.bytecode.protect.annotations.ConstantContext;
import field.namespace.context.ContextTopology;
import field.namespace.generic.ReflectionTools;
import field.namespace.generic.tuple.Pair;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public
class Cc {

    static WeakHashMap<Object, Map<String, ContextFor>> contextMemories =
            new WeakHashMap<Object, Map<String, ContextFor>>();

    public static
    void handle_entry(Object fromThis,
                      String name,
                      Map<String, Object> params,
                      Map<Integer, Pair<String, String>> markedArguments,
                      Object[] arguments) {

        Map<String, ContextFor> cf = contextMemories.get(fromThis);
        if (cf == null) {
            contextMemories.put(fromThis, cf = new HashMap<String, ContextFor>());
        }
        String n2 = (String) params.get("group");

        if ((n2 == null) || "--object--".equals(n2)) n2 = "for:" + System.identityHashCode(fromThis);
        else if ("--method--".equals(n2)) n2 = ((org.objectweb.asm.commons.Method) params.get("method")).getName();


        ContextFor context = cf.get(n2);
        if (context == null) {
            context = new ContextFor();
            if (params.containsKey("immediate")) context.immediate = (Boolean) params.get("immediate");
            if (params.containsKey("constant")) context.constant = (Boolean) params.get("constant");
            if (params.containsKey("resets")) context.resets = (Boolean) params.get("resets");

            try {
                context.on = ContextAnnotationTools.contextFor(fromThis, params, markedArguments, arguments);
            } catch (Exception e) {
                e.printStackTrace();
                Error ee = new Error();
                ee.initCause(e);
            }

            if (params.containsKey("topology")) {
                try {
                    Type c = (Type) params.get("topology");
                    Class<?> cloaded = fromThis.getClass().getClassLoader().loadClass(c.getClassName());
                    if (cloaded != Object.class) {
                        Field contextField;
                        contextField = cloaded.getField("context");
                        Object cc = contextField.get(null);
                        context.on = (ContextTopology) cc;
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    assert false;
                }
                if (context.on == null) throw new Error(" no context for Cc");
            }

            cf.put(n2, context);
        }

        context.enter();
    }

    public static
    void handle_exit(Object fromThis, String name, Map<String, Object> parameterName) {
        Map<String, ContextFor> cf = contextMemories.get(fromThis);
        if (cf == null) {
            contextMemories.put(fromThis, cf = new HashMap<String, ContextFor>());
        }
        String n2 = (String) parameterName.get("group");
        if ((n2 == null) || "--object--".equals(n2)) n2 = "for:" + System.identityHashCode(fromThis);
        else if ("--method--".equals(n2))
            n2 = ((org.objectweb.asm.commons.Method) parameterName.get("method")).getName();

        ContextFor context = cf.get(n2);
        assert context != null : cf + " " + n2;

        context.exit();
    }

    public static
    void setContextFor(Object fromThis, Method m, Object target) {
        m = ReflectionTools.findMethodWithParametersUpwards(m.getName(), m.getParameterTypes(), fromThis.getClass());
        assert m != null;

        ConstantContext c = m.getAnnotation(ConstantContext.class);

        String n2 = c.group();
        if ("--method--".equals(n2)) n2 = m.getName();

        Map<String, ContextFor> cf = contextMemories.get(fromThis);
        if (cf == null) {
            contextMemories.put(fromThis, cf = new HashMap<String, ContextFor>());
        }

        ContextFor context = cf.get(n2);
        if (context == null) context = new ContextFor();

        context.immediate = c.immediate();
        context.constant = c.constant();
        context.resets = c.resets();
        context.contextTarget = target;

        cf.put(n2, context);
    }

}
