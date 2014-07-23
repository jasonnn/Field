package field.bytecode.protect.instrumentation2;

import field.bytecode.protect.annotations.DispatchOverTopology;
import field.bytecode.protect.instrumentation.FieldBytecodeAdapter;
import field.bytecode.protect.instrumentation2.rt.DispatchOverTopologyHandler;
import field.protect.asm.ASMMethod;
import field.protect.asm.ASMType;
import field.protect.asm.FieldASMAdviceAdapter;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;

import static field.bytecode.protect.instrumentation.FieldBytecodeAdapterConstants.FIELD_BYTECODE_ADAPTER_TYPE;
import static field.bytecode.protect.instrumentation.FieldBytecodeAdapterConstants.handleEntry_V_SOSSo;

/**
 * Created by jason on 7/22/14.
 */
public
class DispatchOverTopologyTransformer extends FieldASMAdviceAdapter {

    public static final AnnotatedMethodHandler2 HANDLER = new AnnotatedMethodHandler2() {
        @NotNull
        @Override
        public
        MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx) {
            String uniqueName = Namer.createName(DispatchOverTopology.class, ctx);

            FieldBytecodeAdapter.registerHandler(uniqueName,
                                                 new DispatchOverTopologyHandler(uniqueName, ctx.classCtx.name));

            Map<String, Object> params = ctx.params;
            String paramsID = Namer.uniqueParamID();
            FieldBytecodeAdapter.registerParameters(paramsID, params);

            return new DispatchOverTopologyTransformer(ctx.delegate,
                                                       ctx.access,
                                                       ctx.name,
                                                       ctx.desc,
                                                       uniqueName,
                                                       new ASMMethod(ctx.name, ctx.desc),
                                                       paramsID);
        }
    };


    final String handlerID;
    final String paramsID;
    final ASMMethod onMethod;
    private final boolean isConstructor;
    private final boolean returnsValue;

    private final Label tryStart = new Label();
    private final Label tryStop = new Label();
    private final Label tryHandler = new Label();
    private int localStoragePtr;


    protected
    DispatchOverTopologyTransformer(MethodVisitor mv,
                                    int access,
                                    String name,
                                    String desc,
                                    String handlerID,
                                    ASMMethod onMethod,
                                    String paramsID) {
        super(Opcodes.ASM5, mv, access, name, desc);
        this.handlerID = handlerID;
        this.onMethod = onMethod;
        this.paramsID = paramsID;
        this.isConstructor = "<init>".equals(onMethod.getName());
        this.returnsValue = !ASMType.VOID_TYPE.equals(onMethod.getASMReturnType());
    }


    @Override
    public
    void visitInsn(int opcode) {
        switch (opcode) {
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case RETURN:
                handleReturnInsn(opcode);
                break;
            default:
                super.visitInsn(opcode);
        }

    }

    private
    void handleReturnInsn(int opcode) {
        if (returnsValue) loadLocal(localStoragePtr);
        else super.visitInsn(opcode);
    }

    @Override
    protected
    void onMethodEnter() {
        if (isConstructor) return;

        callEnter();
        if (returnsValue) localStoragePtr = newLocal(onMethod.getASMReturnType());
        visitTryCatchBlock(tryStart, tryStop, tryHandler, null);
        visitLabel(tryStart);

    }

    @Override
    protected
    void onMethodExit(int opcode) {

    }

    private
    void callEnter() {
        push(handlerID);
        loadThis();
        push(onMethod.getName());
        push(paramsID);
        loadArgArray();
        invokeStatic(FIELD_BYTECODE_ADAPTER_TYPE, handleEntry_V_SOSSo);
        pop();
    }
}
