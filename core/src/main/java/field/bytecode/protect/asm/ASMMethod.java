package field.bytecode.protect.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.lang.reflect.Constructor;

/**
 * Created by jason on 7/15/14.
 */
public class ASMMethod extends Method {
    public static ASMMethod from(Method m) {
        if (m instanceof ASMMethod) return (ASMMethod) m;
        return new ASMMethod(m);
    }

    public static ASMMethod getMethod(java.lang.reflect.Method method) {
        return from(Method.getMethod(method));
    }

    public static ASMMethod getMethod(Constructor constructor) {
        return from(Method.getMethod(constructor));
    }

    public static ASMMethod getMethod(String string) {
        return from(Method.getMethod(string));
    }

    public static ASMMethod getMethod(String meth, boolean defaultPacakge) {
        return from(Method.getMethod(meth, defaultPacakge));
    }

    ASMMethod(Method m) {
        this(m.getName(), m.getReturnType(), m.getArgumentTypes());
    }

    public ASMMethod(String name, String desc) {
        super(name, desc);
    }

    public ASMMethod(String name, Type returnType, Type[] argumentTypes) {
        super(name, returnType, argumentTypes);
    }
}
