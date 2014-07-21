package field.bytecode.protect.instrumentation;

import field.namespace.generic.Generics;
import field.protect.asm.ASMMethod;
import field.protect.asm.ASMType;
import field.protect.asm.FieldASMGeneratorAdapter;
import org.objectweb.asm.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static field.bytecode.protect.instrumentation.BasicInstrumentationConstants.*;

/**
* Created by jason on 7/14/14.
*/
public abstract class CallOnEntryAndExit_exceptionAware extends FieldASMGeneratorAdapter implements EntryHandler, ExitHandler {


    private static final ASMType[] Type_handle_sig = new ASMType[]{ASMType.OBJECT_TYPE, ASMType.STRING_TYPE, ASMType.OBJECT_TYPE, ASMType.STRING_TYPE, ASMType.STRING_TYPE, ASMType.STRING_TYPE};

    private final int access2;

    private Label endTryCatchLabel;

    private int exceptionLocal;

    private final ASMMethod onMethod;

    private final String parameterName;

    private final HashMap<String, Object> parameters;

    private int returnNumber;

    protected String name;

    protected LinkedHashMap<Integer, Generics.Pair<String, String>> aliasedParameterSet = new LinkedHashMap<Integer, Generics.Pair<String, String>>();

    boolean isConstructor = false;

    Label startTryCatchLabel;

    public CallOnEntryAndExit_exceptionAware(String name, int access, ASMMethod onMethod, MethodVisitor delegateTo, HashMap<String, Object> parameters) {
        super(access, onMethod, delegateTo);
        this.name = name;
        access2 = access;
        this.onMethod = onMethod;
        this.parameters = parameters;
        parameterName = "parameter:" + BasicInstrumentation2.uniq_parameter++;
        returnNumber = 0;
        BasicInstrumentation2.parameters.put(parameterName, parameters);

        assert !BasicInstrumentation2.entryHandlers.containsKey(name);
        BasicInstrumentation2.entryHandlers.put(name, this);

        assert !BasicInstrumentation2.exitHandlers.containsKey(name);
        BasicInstrumentation2.exitHandlers.put(name, this);

        parameters.put("method", onMethod);
    }

    abstract public Object handle(Object returningThis, String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, String methodReturnName);

    abstract public void handle(String fromName, Object fromThis, String methodName, Map<String, Object> parameterName, Object[] argArray);

    @Override
    public void visitCode() {
        // if (StandardTrampoline.debug)
        //System.out.println(ANSIColorUtils.red(" entryAndExit begins"));
        super.visitCode();

        if (onMethod.getName().equals("<init>")) {

            // we have to leave this until after the first
            // invoke special
            isConstructor = true;

        } else {
            //if (StandardTrampoline.debug)
            // ;//System.out.println(ANSIColorUtils.red(" entryAndExit :instrumented entrance"));

            startTryCatchLabel = this.mark();
            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            loadArgArray();
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle_V_SOSSo);
            // invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type.VOID_TYPE, new Type[]{Type_String, Type_Object, Type_String, Type_String, Type.getType(Object[].class)}));
        }
        exceptionLocal = this.newLocal(Type.getType(Throwable.class));

    }

    @Override
    public void visitEnd() {

        if ((access2 & Opcodes.ACC_ABSTRACT) == 0) {

            endTryCatchLabel = this.mark();
            // if (StandardTrampoline.debug)
            //System.out.println(" exception local is <" + exceptionLocal + ">");
            storeLocal(exceptionLocal);

            push((String) null);
            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle_O_OSOSSS);
            //invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type_Object, Type_handle_sig));

            loadLocal(exceptionLocal);
            throwException();

            super.visitTryCatchBlock(startTryCatchLabel, endTryCatchLabel, endTryCatchLabel, null);

        }
        super.visitMaxs(0, 0);
        super.visitEnd();
        // if (StandardTrampoline.debug)
        //System.out.println(ANSIColorUtils.red(" entryAndExit ends"));
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
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle_O_OSOSSS);
            // invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type_Object, Type_handle_sig));
            pop();
           // if (StandardTrampoline.debug)
                //System.out.println(ANSIColorUtils.red(" entryAndExit :instrumented RETURN"));
        } else if (op == Opcodes.IRETURN) {
            // dup();
            box(Type.INT_TYPE);

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle_O_OSOSSS);
            //invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type_Object, Type_handle_sig));
            unbox(Type.INT_TYPE);
            // if (StandardTrampoline.debug)
            //System.out.println(ANSIColorUtils.red(" entryAndExit :instrumented IRETURN"));
        } else if (op == Opcodes.FRETURN) {
            // dup();
            box(Type.FLOAT_TYPE);

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle_O_OSOSSS);
//            invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type_Object, Type_handle_sig));
            unbox(Type.FLOAT_TYPE);
            //if (StandardTrampoline.debug)
            //System.out.println(ANSIColorUtils.red(" entryAndExit :instrumented FRETURN"));
        } else if (op == Opcodes.ARETURN) {
            // dup();

            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle_O_OSOSSS);
//            invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type_Object, Type_handle_sig));
            checkCast(onMethod.getReturnType());
            //if (StandardTrampoline.debug)
            //System.out.println(ANSIColorUtils.red(" entryAndExit :instrumented ARETURN"));
        }

        super.visitInsn(op);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (isConstructor) {
            if (opcode == Opcodes.INVOKESPECIAL) {
                super.visitMethodInsn(opcode, owner, name, desc);
                //if (StandardTrampoline.debug)
                //System.out.println(ANSIColorUtils.red(" entryAndExit :instrumented entrance of constructor"));

                startTryCatchLabel = this.mark();
                push(this.name);
                loadThis();
                push(onMethod.getName());
                push(parameterName);
                loadArgArray();
                invokeStatic(BASIC_INSTRUMENTATION_TYPE, handle_V_SOSSo);


                //     invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle", Type.VOID_TYPE, new Type[]{Type_String, Type_Object, Type_String, Type_String, Type.getType(Object[].class)}));

                isConstructor = false;
            } else
                super.visitMethodInsn(opcode, owner, name, desc);

        } else
            super.visitMethodInsn(opcode, owner, name, desc);

    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, boolean visible) {

        // if (StandardTrampoline.debug)
        //System.out.println(" entryAndExit, visit parameter annotation <" + parameter + "> <" + desc + "> <" + visible + ">");

        if (BasicInstrumentation2.knownAliasingParameters.contains(desc)) {
            aliasedParameterSet.put(parameter, new Generics.Pair<String, String>(desc, null));

            // rip out the name and argument
            return new AnnotationVisitor(Opcodes.ASM5) {

                public void visit(String name, Object value) {
                    aliasedParameterSet.put(parameter, new Generics.Pair<String, String>(desc, (String) value));
                }

                public AnnotationVisitor visitAnnotation(String name, String desc) {
                    return null;
                }

                public AnnotationVisitor visitArray(String name) {
                    return null;
                }

                public void visitEnd() {
                }

                public void visitEnum(String name, String desc, String value) {
                }
            };
        } else
            return super.visitParameterAnnotation(parameter, desc, visible);
    }

}
