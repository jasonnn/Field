package field.bytecode.protect.instrumentation;

import field.bytecode.protect.asm.ASMMethod;
import field.bytecode.protect.asm.FieldASMGeneratorAdapter;
import field.bytecode.protect.trampoline.StandardTrampoline;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

/**
* Created by jason on 7/14/14.
*/
public abstract class CallOnReturn extends FieldASMGeneratorAdapter implements ExitHandler {

    private final String name;

    private final ASMMethod onMethod;

    private final String parameterName;

    private final HashMap<String, Object> parameters;

    int returnNumber = 0;

    public CallOnReturn(String name, int access, ASMMethod onMethod, MethodVisitor delegateTo, HashMap<String, Object> parameters) {
        super(access, onMethod, delegateTo);
        this.name = name;
        this.onMethod = onMethod;
        this.parameters = parameters;
        parameterName = "parameter:" + BasicInstrumentation2.uniq_parameter++;
        BasicInstrumentation2.parameters.put(parameterName, parameters);
        returnNumber = 0;

        assert !BasicInstrumentation2.exitHandlers.containsKey(name);
        BasicInstrumentation2.exitHandlers.put(name, this);
    }

    abstract public Object handle(Object returningThis, String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, String methodReturnName);

    @Override
    public void visitInsn(int op) {
        if (StandardTrampoline.debug)
            ;//System.out.println(" -- visit insn <" + op + "> <" + Opcodes.RETURN + ">");
        if (op == Opcodes.RETURN) {
            push((String) null);
            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type.getType(Object.class), new Type[]{Type.getType(Object.class), Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class), Type.getType(String.class), Type.getType(String.class)}));
            pop();
        } else if (op == Opcodes.IRETURN) {
            // dup();
            box(Type.INT_TYPE);

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type.getType(Object.class), new Type[]{Type.getType(Object.class), Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class), Type.getType(String.class), Type.getType(String.class)}));
            unbox(Type.INT_TYPE);
        } else if (op == Opcodes.FRETURN) {
            // dup();
            box(Type.FLOAT_TYPE);

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type.getType(Object.class), new Type[]{Type.getType(Object.class), Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class), Type.getType(String.class), Type.getType(String.class)}));
            unbox(Type.FLOAT_TYPE);
        } else if (op == Opcodes.ARETURN) {
            // dup();

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type.getType(Object.class), new Type[]{Type.getType(Object.class), Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class), Type.getType(String.class), Type.getType(String.class)}));
        }

        super.visitInsn(op);
    }
}
