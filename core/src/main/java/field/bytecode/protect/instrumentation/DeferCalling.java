package field.bytecode.protect.instrumentation;

import field.protect.asm.ASMMethod;
import field.protect.asm.ASMType;
import field.protect.asm.FieldASMGeneratorAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

/**
* Created by jason on 7/14/14.
*/
public abstract class DeferCalling extends FieldASMGeneratorAdapter implements DeferedHandler {
    private final int access;

    private final ClassVisitor classDelegate;

    private final String name;

    private final ASMMethod onMethod;

    private final String parameterName;

    private final HashMap<String, Object> parameters;

    private final int returnNumber;

    private final String signature;

    public DeferCalling(String name, int access, ASMMethod onMethod, ClassVisitor classDelegate, MethodVisitor delegateTo, String signature, HashMap<String, Object> parameters) {
        super(access, onMethod, delegateTo);
        this.name = name;
        this.access = access;
        this.onMethod = onMethod;
        this.classDelegate = classDelegate;
        this.signature = signature;
        this.parameters = parameters;
        parameterName = "parameter:" + FieldBytecodeAdapter.uniq_parameter++;
        returnNumber = 0;
        FieldBytecodeAdapter.parameters.put(parameterName, parameters);

        assert onMethod.getASMReturnType() == ASMType.VOID_TYPE : onMethod.getASMReturnType();

        assert !FieldBytecodeAdapter.entryHandlers.containsKey(name);
        FieldBytecodeAdapter.deferedHandlers.put(name, this);

    }

    public abstract
    void handleDefered(String fromName,
                                       Object fromThis,
                                       String methodName,
                                       Map<String, Object> parameters,
                                       Object[] argArray,
                                       Class[] argTypeArray);

    @Override
    public void visitCode() {
        super.visitCode();
        push(name);
        loadThis();
        push(onMethod.getName() + "_original");
        push(parameterName);
        loadArgArray();

        invokeStatic(FieldBytecodeAdapterConstants.FIELD_BYTECODE_ADAPTER_TYPE,
                     FieldBytecodeAdapterConstants.handleCancelFast_O_IOSo);
        // invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handleCancelFast", ASMType.VOID_TYPE, new ASMType[]{ASMType.getType(String.class), ASMType.getType(Object.class), ASMType.getType(String.class), ASMType.getType(String.class), ASMType.getType(Object[].class)}));

        visitInsn(Opcodes.RETURN);

        super.visitMaxs(0, 0);
        super.visitEnd();
        //
        this.mv = classDelegate.visitMethod(access, onMethod.getName() + "_original", onMethod.getDescriptor(), signature, new String[]{});
        this.mv.visitCode();
    }
}
