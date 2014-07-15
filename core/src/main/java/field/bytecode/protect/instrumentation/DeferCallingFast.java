package field.bytecode.protect.instrumentation;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.util.HashMap;

/**
* Created by jason on 7/14/14.
*/
public abstract class DeferCallingFast extends GeneratorAdapter implements FastCancelHandler {
    static int uniq = 0;

    private final int access;

    private final ClassVisitor classDelegate;

    private final String name;

    private final Method onMethod;

    private final String parameterName;

    private final int returnNumber;

    private final String signature;

    protected HashMap<String, Object> parameters;

    public DeferCallingFast(String name, int access, Method onMethod, ClassVisitor classDelegate, MethodVisitor delegateTo, String signature, HashMap<String, Object> parameters) {
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

        // assert onMethod.getReturnType() == Type.VOID_TYPE :
        // onMethod.getReturnType();

        assert !BasicInstrumentation2.entryHandlers.containsKey(name);
        // deferedHandlers.put(name, this);

        assert !BasicInstrumentation2.entryHandlers.containsKey(name);
        FastCancelHandler[] ne = new FastCancelHandler[BasicInstrumentation2.entryCancelList.length + 1];
        System.arraycopy(BasicInstrumentation2.entryCancelList, 0, ne, 0, BasicInstrumentation2.entryCancelList.length);
        ne[ne.length - 1] = this;
        BasicInstrumentation2.entryCancelList = ne;
        uniq = BasicInstrumentation2.entryCancelList.length - 1;

    }

    abstract public Object handle(int fromName, Object fromThis, String originalMethodName, Object[] argArray);

    @Override
    public void visitCode() {
        super.visitCode();
        push(uniq);
        loadThis();
        push(onMethod.getName() + "_original");
        loadArgArray();

        invokeStatic(Type.getType(BasicInstrumentation2.class), new Method("handleCancelFast", Type.getType(Object.class), new Type[]{Type.getType(Integer.TYPE), Type.getType(Object.class), Type.getType(String.class), Type.getType(Object[].class)}));

        if (onMethod.getReturnType().getSort() == Type.OBJECT) {
            checkCast(onMethod.getReturnType());
            visitInsn(Opcodes.ARETURN);
        } else if (onMethod.getReturnType() == Type.INT_TYPE) {
            unbox(Type.INT_TYPE);
            super.visitInsn(Opcodes.IRETURN);
        } else if (onMethod.getReturnType() == Type.FLOAT_TYPE) {
            unbox(Type.FLOAT_TYPE);
            super.visitInsn(Opcodes.FRETURN);
        } else if (onMethod.getReturnType() == Type.VOID_TYPE) {
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
