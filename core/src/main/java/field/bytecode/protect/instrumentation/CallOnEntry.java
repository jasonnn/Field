package field.bytecode.protect.instrumentation;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.util.HashMap;
import java.util.Map;

/**
* Created by jason on 7/14/14.
*/
public abstract class CallOnEntry extends GeneratorAdapter implements EntryHandler {

    static int uniq = 0;

    private final String name;

    private final Method onMethod;

    private final String parameterName;

    private final HashMap<String, Object> parameters;

    int returnNumber = 0;

    public CallOnEntry(String name, int access, Method onMethod, MethodVisitor delegateTo, HashMap<String, Object> parameters) {
        super(access, onMethod, delegateTo);
        this.name = name;
        this.onMethod = onMethod;
        this.parameters = parameters;
        parameterName = "parameter:" + BasicInstrumentation2.uniq_parameter++;
        BasicInstrumentation2.parameters.put(parameterName, parameters);
        returnNumber = 0;

        assert !BasicInstrumentation2.entryHandlers.containsKey(name);
        BasicInstrumentation2.entryHandlers.put(name, this);
    }

    abstract public void handle(String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, Object[] argArray);

    @Override
    public void visitCode() {

        push(name);
        loadThis();
        push(onMethod.getName());
        push(parameterName);
        loadArgArray();
        invokeStatic(Type.getType(BasicInstrumentation2.class), new Method("handleFast", Type.VOID_TYPE, new Type[] { Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class), Type.getType(String.class), Type.getType(Object[].class) }));
        super.visitCode();
    }
}
