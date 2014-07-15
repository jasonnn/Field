package field.bytecode.protect.instrumentation;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.util.HashMap;

/**
* Created by jason on 7/14/14.
*/
public abstract class CallOnEntryFast extends GeneratorAdapter implements FastEntryHandler {

    static int uniq = 0;

    private final String name;

    private final Method onMethod;

    protected final HashMap<String, Object> parameters;

    int returnNumber = 0;

    public CallOnEntryFast(String name, int access, Method onMethod, MethodVisitor delegateTo, HashMap<String, Object> parameters) {
        super(access, onMethod, delegateTo);
        this.name = name;
        this.onMethod = onMethod;
        this.parameters = parameters;
        returnNumber = 0;

        assert !BasicInstrumentation2.entryHandlers.containsKey(name);
        FastEntryHandler[] ne = new FastEntryHandler[BasicInstrumentation2.entryHandlerList.length + 1];
        System.arraycopy(BasicInstrumentation2.entryHandlerList, 0, ne, 0, BasicInstrumentation2.entryHandlerList.length);
        ne[ne.length - 1] = this;
        BasicInstrumentation2.entryHandlerList = ne;
        uniq = BasicInstrumentation2.entryHandlerList.length - 1;
    }

    abstract public void handle(int fromName, Object fromThis, Object[] argArray);

    @Override
    public void visitCode() {

        push(uniq);
        loadThis();
        loadArgArray();
        invokeStatic(Type.getType(BasicInstrumentation2.class), new Method("handleFast", Type.VOID_TYPE, new Type[]{Type.getType(Integer.TYPE), Type.getType(Object.class), Type.getType(Object[].class)}));
        super.visitCode();
    }
}