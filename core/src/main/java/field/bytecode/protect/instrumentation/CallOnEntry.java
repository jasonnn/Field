package field.bytecode.protect.instrumentation;

import field.protect.asm.ASMMethod;
import field.protect.asm.FieldASMGeneratorAdapter;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;
import java.util.Map;

import static field.bytecode.protect.instrumentation.FieldBytecodeAdapterConstants.FIELD_BYTECODE_ADAPTER_TYPE;
import static field.bytecode.protect.instrumentation.FieldBytecodeAdapterConstants.handleFast_V_IOo;

/**
* Created by jason on 7/14/14.
*/
public abstract class CallOnEntry extends FieldASMGeneratorAdapter implements EntryHandler {

    static int uniq = 0;

    private final String name;

    private final ASMMethod onMethod;

    private final String parameterName;

    private final HashMap<String, Object> parameters;

    int returnNumber = 0;

    public CallOnEntry(String name, int access, ASMMethod onMethod, MethodVisitor delegateTo, HashMap<String, Object> parameters) {
        super(access, onMethod, delegateTo);
        this.name = name;
        this.onMethod = onMethod;
        this.parameters = parameters;
        parameterName = "parameter:" + FieldBytecodeAdapter.uniq_parameter++;
        FieldBytecodeAdapter.parameters.put(parameterName, parameters);
        returnNumber = 0;

        assert !FieldBytecodeAdapter.entryHandlers.containsKey(name);
        FieldBytecodeAdapter.entryHandlers.put(name, this);
    }

    abstract public void handleEntry(String fromName,
                                     Object fromThis,
                                     String methodName,
                                     Map<String, Object> parameterName,
                                     Object[] argArray);

    @Override
    public void visitCode() {

        push(name);
        loadThis();
        push(onMethod.getName());
        push(parameterName);
        loadArgArray();
        invokeStatic(FIELD_BYTECODE_ADAPTER_TYPE, handleFast_V_IOo);
        // invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handleFast", Type.VOID_TYPE, new Type[] { Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class), Type.getType(String.class), Type.getType(Object[].class) }));
        super.visitCode();
    }
}
