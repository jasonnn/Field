package field.bytecode.protect.instrumentation;

import field.protect.asm.ASMMethod;
import field.protect.asm.ASMType;
import field.protect.asm.FieldASMGeneratorAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;

/**
 * Created by jason on 7/14/14.
 */
public abstract class DeferCallingFast extends FieldASMGeneratorAdapter implements FastCancelHandler {
    static int uniq = 0;

    private final int access;

    private final ClassVisitor classDelegate;

    //  private final String name;

    private final ASMMethod onMethod;

    // private final int returnNumber;

    private final String signature;

    protected final Map<String, Object> parameters;

    public DeferCallingFast(String name,
                            int access,
                            ASMMethod onMethod,
                            ClassVisitor classDelegate,
                            MethodVisitor delegateTo,
                            String signature,
                            Map<String, Object> parameters) {
        super(access, onMethod, delegateTo);
        // this.name = name;
        this.access = access;
        this.onMethod = onMethod;
        this.classDelegate = classDelegate;
        this.signature = signature;
        this.parameters = parameters;
        String parameterName = "parameter:" + FieldBytecodeAdapter.uniq_parameter++;
        // returnNumber = 0;
        FieldBytecodeAdapter.parameters.put(parameterName, parameters);

        // assert onMethod.getReturnType() == Type.VOID_TYPE :
        // onMethod.getASMReturnType();

        assert !FieldBytecodeAdapter.entryHandlers.containsKey(name);
        // deferedHandlers.put(name, this);

        // assert !BasicInstrumentation2.entryHandlers.containsKey(name);

        FieldBytecodeAdapter.addCancelHandler(this);
        uniq = FieldBytecodeAdapter.entryCancelList.length - 1;

    }

    abstract public Object handle(int fromName, Object fromThis, String originalMethodName, Object[] argArray);

    @Override
    public void visitCode() {
        super.visitCode();
        push(uniq);
        loadThis();
        push(onMethod.getName() + "_original");
        loadArgArray();

        // invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handleCancelFast", Type.getType(Object.class), new Type[]{Type.getType(Integer.TYPE), Type.getType(Object.class), Type.getType(String.class), Type.getType(Object[].class)}));

        invokeStatic(FieldBytecodeAdapterConstants.FIELD_BYTECODE_ADAPTER_TYPE,
                     FieldBytecodeAdapterConstants.handleCancelFast_O_IOSo);

        //TODO cleanup
        if (onMethod.getASMReturnType().getSort() == ASMType.OBJECT) {
            checkCast(onMethod.getASMReturnType());
            visitInsn(Opcodes.ARETURN);
        } else if (onMethod.getASMReturnType() == ASMType.INT_TYPE) {
            unbox(ASMType.INT_TYPE);
            super.visitInsn(Opcodes.IRETURN);
        } else if (onMethod.getASMReturnType() == ASMType.FLOAT_TYPE) {
            unbox(ASMType.FLOAT_TYPE);
            super.visitInsn(Opcodes.FRETURN);
        } else if (onMethod.getASMReturnType() == ASMType.VOID_TYPE) {
            super.visitInsn(Opcodes.RETURN);
        } else {
            assert false : onMethod.getReturnType();
        }

        super.visitMaxs(0, 0);
        super.visitEnd();
        //
        this.mv = classDelegate.visitMethod(access, onMethod.getName() + "_original", onMethod.getDescriptor(), signature, new String[]{});
        this.mv.visitCode();
    }
}
