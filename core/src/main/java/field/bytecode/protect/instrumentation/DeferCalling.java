package field.bytecode.protect.instrumentation;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.util.HashMap;
import java.util.Map;

/**
* Created by jason on 7/14/14.
*/
public abstract class DeferCalling extends GeneratorAdapter implements DeferedHandler {
    private final int access;

    private final ClassVisitor classDelegate;

    private final String name;

    private final Method onMethod;

    private final String parameterName;

    private final HashMap<String, Object> parameters;

    private final int returnNumber;

    private final String signature;

    public DeferCalling(String name, int access, Method onMethod, ClassVisitor classDelegate, MethodVisitor delegateTo, String signature, HashMap<String, Object> parameters) {
        super(access, onMethod, delegateTo);
        this.name = name;
        this.access = access;
        this.onMethod = onMethod;
        this.classDelegate = classDelegate;
        this.signature = signature;
        this.parameters = parameters;
        parameterName = "parameter:" + BasicInstrumentation2.uniq_parameter++;
        returnNumber = 0;
        BasicInstrumentation2.parameters.put(parameterName, parameters);

        assert onMethod.getReturnType() == Type.VOID_TYPE : onMethod.getReturnType();

        assert !BasicInstrumentation2.entryHandlers.containsKey(name);
        BasicInstrumentation2.deferedHandlers.put(name, this);

    }

    abstract public void handle(String fromName, Object fromThis, String methodName, Map<String, Object> parameters, Object[] argArray, Class[] argTypeArray);

    @Override
    public void visitCode() {
        super.visitCode();
        push(name);
        loadThis();
        push(onMethod.getName() + "_original");
        push(parameterName);
        loadArgArray();

        invokeStatic(Type.getType(BasicInstrumentation2.class), new Method("handleCancelFast", Type.VOID_TYPE, new Type[]{Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class), Type.getType(String.class), Type.getType(Object[].class)}));

        visitInsn(Opcodes.RETURN);

        super.visitMaxs(0, 0);
        super.visitEnd();
        //
        this.mv = classDelegate.visitMethod(access, onMethod.getName() + "_original", onMethod.getDescriptor(), signature, new String[]{});
        this.mv.visitCode();
    }
}