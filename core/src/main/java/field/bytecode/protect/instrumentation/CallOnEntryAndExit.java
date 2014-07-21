package field.bytecode.protect.instrumentation;

import field.protect.asm.ASMMethod;
import field.protect.asm.FieldASMGeneratorAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

import static field.bytecode.protect.instrumentation.BasicInstrumentationConstants.*;

/**
* Created by jason on 7/14/14.
*/
public abstract class CallOnEntryAndExit extends FieldASMGeneratorAdapter implements EntryHandler, ExitHandler {
    private final String name;

    private final ASMMethod onMethod;

    private final String parameterName;

    private final HashMap<String, Object> parameters;

    private int returnNumber;

    boolean isConstructor = false;

    public CallOnEntryAndExit(String name, int access, ASMMethod onMethod, MethodVisitor delegateTo, HashMap<String, Object> parameters) {
        super(access, onMethod, delegateTo);
        this.name = name;
        this.onMethod = onMethod;
        this.parameters = parameters;
        parameterName = "parameter:" + BasicInstrumentation2.uniq_parameter++;
        returnNumber = 0;
        BasicInstrumentation2.parameters.put(parameterName, parameters);

        assert !BasicInstrumentation2.entryHandlers.containsKey(name);
        BasicInstrumentation2.entryHandlers.put(name, this);

        assert !BasicInstrumentation2.exitHandlers.containsKey(name);
        BasicInstrumentation2.exitHandlers.put(name, this);
    }

    abstract public Object handle(Object returningThis, String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, String methodReturnName);

    abstract public void handle(String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, Object[] argArray);

    @Override
    public void visitCode() {

        if (onMethod.getName().equals("<init>")) {

            // we have to leave this until after the first
            // invoke special
            isConstructor = true;

        } else {
            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            loadArgArray();
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle_V_SOSSo);
        }
        super.visitCode();
    }

    @Override
    public void visitInsn(int op) {
        if (op == Opcodes.RETURN) {
            push((String) null);
            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle_V_SOSSoc);
            pop();
        } else if (op == Opcodes.IRETURN) {
            box(Type.INT_TYPE);

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle_V_SOSSo);
            // new ASMMethod("handle", Type.VOID_TYPE, new Type[]{Type.getType(Object.class), Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class), Type.getType(String.class), Type.getType(String.class)}));
            unbox(Type.INT_TYPE);
        } else if (op == Opcodes.FRETURN) {
            box(Type.FLOAT_TYPE);

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle_V_SOSSo);
            // invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type.VOID_TYPE, new Type[]{Type.getType(Object.class), Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class), Type.getType(String.class), Type.getType(String.class)}));
            unbox(Type.FLOAT_TYPE);
        } else if (op == Opcodes.ARETURN) {
            dup();

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle_V_SOSSo);
            //invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type.VOID_TYPE, new Type[]{Type.getType(Object.class), Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class), Type.getType(String.class), Type.getType(String.class)}));
        }

        super.visitInsn(op);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (isConstructor) {
            if (opcode == Opcodes.INVOKESPECIAL) {
                super.visitMethodInsn(opcode, owner, name, desc);

                push(this.name);
                loadThis();
                push(onMethod.getName());
                push(parameterName);
                loadArgArray();

                invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle_V_SOSSo);
                //invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type.VOID_TYPE, new Type[]{Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class), Type.getType(String.class), Type.getType(Object[].class)}));

                isConstructor = false;
            } else
                super.visitMethodInsn(opcode, owner, name, desc);

        } else
            super.visitMethodInsn(opcode, owner, name, desc);

    }
}
