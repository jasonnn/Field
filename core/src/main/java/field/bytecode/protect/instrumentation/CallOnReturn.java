package field.bytecode.protect.instrumentation;

import field.protect.asm.ASMMethod;
import field.protect.asm.ASMType;
import field.protect.asm.FieldASMGeneratorAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

import static field.bytecode.protect.instrumentation.FieldBytecodeAdapterConstants.FIELD_BYTECODE_ADAPTER_TYPE;
import static field.bytecode.protect.instrumentation.FieldBytecodeAdapterConstants.handleExit_O_OSOSSS;

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
        parameterName = "parameter:" + FieldBytecodeAdapter.uniq_parameter++;
        FieldBytecodeAdapter.parameters.put(parameterName, parameters);
        returnNumber = 0;

        assert !FieldBytecodeAdapter.exitHandlers.containsKey(name);
        FieldBytecodeAdapter.exitHandlers.put(name, this);
    }

    public abstract
    Object handleExit(Object returningThis,
                                      String fromName,
                                      Object fromThis,
                                      String methodName,
                                      Map<String, Object> parameterName,
                                      String methodReturnName);

    @Override
    public void visitInsn(int op) {
        //if (StandardTrampoline.debug)
        //System.out.println(" -- visit insn <" + op + "> <" + Opcodes.RETURN + ">");
        if (op == Opcodes.RETURN) {
            push((String) null);
            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push(String.valueOf(returnNumber++));
            invokeStatic(FIELD_BYTECODE_ADAPTER_TYPE, handleExit_O_OSOSSS);
            //  invokeStatic(ASMType.getType(BasicInstrumentation2.class), new ASMMethod("handle2", ASMType.getType(Object.class), new Type[]{ASMType.getType(Object.class), ASMType.getType(String.class), ASMType.getType(Object.class), ASMType.getType(String.class), ASMType.getType(String.class), ASMType.getType(String.class)}));
            pop();
        } else if (op == Opcodes.IRETURN) {
            // dup();
            box(ASMType.INT_TYPE);

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push(String.valueOf(returnNumber++));
            invokeStatic(FIELD_BYTECODE_ADAPTER_TYPE, handleExit_O_OSOSSS);
            // invokeStatic(ASMType.getType(BasicInstrumentation2.class), new ASMMethod("handle2", ASMType.getType(Object.class), new Type[]{ASMType.getType(Object.class), ASMType.getType(String.class), ASMType.getType(Object.class), ASMType.getType(String.class), ASMType.getType(String.class), ASMType.getType(String.class)}));
            unbox(ASMType.INT_TYPE);
        } else if (op == Opcodes.FRETURN) {
            // dup();
            box(ASMType.FLOAT_TYPE);

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push(String.valueOf(returnNumber++));
            invokeStatic(FIELD_BYTECODE_ADAPTER_TYPE, handleExit_O_OSOSSS);
            // invokeStatic(ASMType.getType(BasicInstrumentation2.class), new ASMMethod("handle2", ASMType.getType(Object.class), new Type[]{ASMType.getType(Object.class), ASMType.getType(String.class), ASMType.getType(Object.class), ASMType.getType(String.class), ASMType.getType(String.class), ASMType.getType(String.class)}));
            unbox(ASMType.FLOAT_TYPE);
        } else if (op == Opcodes.ARETURN) {
            // dup();

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push(String.valueOf(returnNumber++));
            invokeStatic(FIELD_BYTECODE_ADAPTER_TYPE, handleExit_O_OSOSSS);
            //invokeStatic(ASMType.getType(BasicInstrumentation2.class), new ASMMethod("handle2", ASMType.getType(Object.class), new Type[]{ASMType.getType(Object.class), ASMType.getType(String.class), ASMType.getType(Object.class), ASMType.getType(String.class), ASMType.getType(String.class), ASMType.getType(String.class)}));
        }

        super.visitInsn(op);
    }
}
