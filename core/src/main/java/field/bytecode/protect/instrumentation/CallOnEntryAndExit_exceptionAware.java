package field.bytecode.protect.instrumentation;

import field.namespace.generic.tuple.Pair;
import field.protect.asm.ASMMethod;
import field.protect.asm.ASMType;
import field.protect.asm.FieldASMGeneratorAdapter;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.LinkedHashMap;
import java.util.Map;

import static field.bytecode.protect.instrumentation.FieldBytecodeAdapterConstants.*;

/**
 * Created by jason on 7/14/14.
 */
public abstract
class CallOnEntryAndExit_exceptionAware extends FieldASMGeneratorAdapter implements EntryHandler, ExitHandler {


    private final int access2;

    private int exceptionLocal;

    private final ASMMethod onMethod;

    private final String parameterName;


    private int returnNumber;

    protected String name;

    protected LinkedHashMap<Integer, Pair<String, String>> aliasedParameterSet =
            new LinkedHashMap<Integer, Pair<String, String>>();

    boolean isConstructor = false;

    Label startTryCatchLabel;

    public
    CallOnEntryAndExit_exceptionAware(String name,
                                      int access,
                                      ASMMethod onMethod,
                                      MethodVisitor delegateTo,
                                      Map<String, Object> parameters) {
        super(access, onMethod, delegateTo);
        this.name = name;
        access2 = access;
        this.onMethod = onMethod;
        parameterName = "parameter:" + FieldBytecodeAdapter.uniq_parameter++;
        returnNumber = 0;
        FieldBytecodeAdapter.parameters.put(parameterName, parameters);

        assert !FieldBytecodeAdapter.entryHandlers.containsKey(name);
        FieldBytecodeAdapter.entryHandlers.put(name, this);

        assert !FieldBytecodeAdapter.exitHandlers.containsKey(name);
        FieldBytecodeAdapter.exitHandlers.put(name, this);

        parameters.put("method", onMethod);
    }

    abstract public
    Object handle(Object returningThis,
                  String fromName,
                  Object fromThis,
                  String methodName,
                  Map<String, Object> parameterName,
                  String methodReturnName);

    abstract public
    void handle(String fromName,
                Object fromThis,
                String methodName,
                Map<String, Object> parameterName,
                Object[] argArray);

    @Override
    public
    void visitCode() {

        super.visitCode();

        if (onMethod.getName().equals("<init>")) {

            // we have to leave this until after the first
            // invoke special
            isConstructor = true;

        }
        else {


            startTryCatchLabel = mark();
            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            loadArgArray();
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle1_V_SOSSo);
        }
        exceptionLocal = newLocal(ASMType.THROWABLE_TYPE);

    }

    @Override
    public
    void visitEnd() {

        if ((access2 & Opcodes.ACC_ABSTRACT) == 0) {

            Label endTryCatchLabel = mark();
            storeLocal(exceptionLocal);

            push((String) null);
            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle0_O_OSOSSS);

            loadLocal(exceptionLocal);
            throwException();

            //java.lang.IllegalStateException: Try catch blocks must be visited before their labels
            super.visitTryCatchBlock(startTryCatchLabel, endTryCatchLabel, endTryCatchLabel, null);

        }
        super.visitMaxs(0, 0);
        super.visitEnd();
    }

    @Override
    public
    void visitInsn(int op) {
        if (op == Opcodes.RETURN) {
            push((String) null);
            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle0_O_OSOSSS);
            pop();

        }
        else if (op == Opcodes.IRETURN) {
            // dup();
            box(ASMType.INT_TYPE);

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle0_O_OSOSSS);
            unbox(ASMType.INT_TYPE);

        }
        else if (op == Opcodes.FRETURN) {
            // dup();
            box(ASMType.FLOAT_TYPE);

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle0_O_OSOSSS);
            unbox(ASMType.FLOAT_TYPE);

        }
        else if (op == Opcodes.ARETURN) {
            // dup();

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle0_O_OSOSSS);
            checkCast(onMethod.getASMReturnType());
            //if (StandardTrampoline.debug)
            //System.out.println(ANSIColorUtils.red(" entryAndExit :instrumented ARETURN"));
        }

        super.visitInsn(op);
    }

    @Override
    public
    void visitMaxs(int maxStack, int maxLocals) {
    }

    @Override
    public
    void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (isConstructor) {
            if (opcode == Opcodes.INVOKESPECIAL) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);

                startTryCatchLabel = this.mark();
                push(this.name);
                loadThis();
                push(onMethod.getName());
                push(parameterName);
                loadArgArray();
                invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle1_V_SOSSo);

                isConstructor = false;
            }
            else super.visitMethodInsn(opcode, owner, name, desc, itf);

        }
        else super.visitMethodInsn(opcode, owner, name, desc, itf);
    }


    @Override
    public
    AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, boolean visible) {


        if (FieldBytecodeAdapter.knownAliasingParameters.contains(desc)) {
            aliasedParameterSet.put(parameter, new Pair<String, String>(desc, null));

            // rip out the name and argument
            return new AnnotationVisitor(Opcodes.ASM5) {

                public
                void visit(String name, Object value) {
                    aliasedParameterSet.put(parameter, new Pair<String, String>(desc, (String) value));
                }

                public
                AnnotationVisitor visitAnnotation(String name, String desc) {
                    return null;
                }

                public
                AnnotationVisitor visitArray(String name) {
                    return null;
                }

                public
                void visitEnd() {
                }

                public
                void visitEnum(String name, String desc, String value) {
                }
            };
        }
        else return super.visitParameterAnnotation(parameter, desc, visible);
    }

}
