package field.bytecode.protect.instrumentation;

import field.protect.asm.ASMMethod;
import field.protect.asm.FieldASMGeneratorAdapter;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;

import static field.bytecode.protect.instrumentation.FieldBytecodeAdapterConstants.FIELD_BYTECODE_ADAPTER_TYPE;
import static field.bytecode.protect.instrumentation.FieldBytecodeAdapterConstants.handleFast_V_IOo;

/**
* Created by jason on 7/14/14.
*/
public abstract class CallOnEntryFast extends FieldASMGeneratorAdapter implements FastEntryHandler {

    static int uniq = 0;

    private final String name;

    private final ASMMethod onMethod;

    protected final HashMap<String, Object> parameters;

    int returnNumber = 0;

    public CallOnEntryFast(String name, int access, ASMMethod onMethod, MethodVisitor delegateTo, HashMap<String, Object> parameters) {
        super(access, onMethod, delegateTo);
        this.name = name;
        this.onMethod = onMethod;
        this.parameters = parameters;
        returnNumber = 0;

        assert !FieldBytecodeAdapter.entryHandlers.containsKey(name);
        FastEntryHandler[] ne = new FastEntryHandler[FieldBytecodeAdapter.entryHandlerList.length + 1];
        System.arraycopy(FieldBytecodeAdapter.entryHandlerList, 0, ne, 0, FieldBytecodeAdapter.entryHandlerList.length);
        ne[ne.length - 1] = this;
        FieldBytecodeAdapter.entryHandlerList = ne;
        uniq = FieldBytecodeAdapter.entryHandlerList.length - 1;
    }

    abstract public void handle(int fromName, Object fromThis, Object[] argArray);

    @Override
    public void visitCode() {

        push(uniq);
        loadThis();
        loadArgArray();
        invokeStatic(FIELD_BYTECODE_ADAPTER_TYPE, handleFast_V_IOo);

        // invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handleFast", Type.VOID_TYPE, new Type[]{Type.getType(Integer.TYPE), Type.getType(Object.class), Type.getType(Object[].class)}));
        super.visitCode();
    }
}
