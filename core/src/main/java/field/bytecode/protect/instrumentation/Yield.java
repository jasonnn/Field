package field.bytecode.protect.instrumentation;

import field.bytecode.protect.analysis.TypesClassVisitor;
import field.bytecode.protect.analysis.TypesContext;
import field.bytecode.protect.trampoline.StandardTrampoline;
import field.namespace.generic.ReflectionTools;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.util.*;

/**
* Created by jason on 7/14/14.
*/
public abstract class Yield extends GeneratorAdapter implements YieldHandler {
    private final int access;

    private final String className;

    private Label initialJumpLabel;

    private final Method onMethod;

    private final byte[] originalByteCode;

    private final String parameterName;

    private final int returnNumber;

    private Label startLabel;

    private final TypesClassVisitor typeAnalysis;

    protected final String name;

    protected final HashMap<String, Object> parameters;

    List<Label> jumpLabels = new ArrayList<Label>();

    List<Integer> validLocals = new ArrayList<Integer>();

    List<Type> validLocalTypes = new ArrayList<Type>();

    int yieldNumber = 0;

    public Yield(String name, int access, Method onMethod, MethodVisitor delegateTo, HashMap<String, Object> parameters, byte[] originalByteCode, String className) {
        super(access, onMethod, delegateTo);
        this.name = name;
        this.access = access;
        this.onMethod = onMethod;
        this.parameters = parameters;
        this.originalByteCode = originalByteCode;
        this.className = className;

        parameterName = "parameter:" + BasicInstrumentation2.uniq_parameter++;
        returnNumber = 0;
        BasicInstrumentation2.parameters.put(parameterName, parameters);

        BasicInstrumentation2.yieldHandlers.put(name, this);

        ClassReader reader = new ClassReader(originalByteCode);
        typeAnalysis = new TypesClassVisitor(className, onMethod.getName() + onMethod.getDescriptor()) {
            @Override
            protected boolean isYieldCall(String owner_classname, String name, String desc) {
                if (owner_classname.equalsIgnoreCase("field.bytecode.protect.yield.YieldUtilities") && name.equals("yield")) {
                    return true;
                }
                return false;
            }
        };
        reader.accept(typeAnalysis, ClassReader.EXPAND_FRAMES);

        // now we should have something

    }

    @Override
    public void visitCode() {
        if (StandardTrampoline.debug)
            ;//System.out.println(ANSIColorUtils.red(" yield begins "));
        super.visitCode();

        initialJumpLabel = this.newLabel();
        this.goTo(initialJumpLabel);

        startLabel = this.mark();
        jumpLabels.add(startLabel);

    }

    @Override
    public void visitEnd() {
        // insert jump table
        this.visitLabel(initialJumpLabel);

        // insert code that works out where to jumpFrom
        push(name);
        loadThis();
        push(onMethod.getName());
        invokeStatic(Type.getType(BasicInstrumentation2.class), new Method("handle_yieldIndex", Type.INT_TYPE, new Type[]{Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class)}));

        if (StandardTrampoline.debug)
            ;//System.out.println(" we have <" + jumpLabels.size() + "> <" + startLabel + ">");
        this.visitTableSwitchInsn(0, jumpLabels.size() - 1, startLabel, jumpLabels.toArray(new Label[jumpLabels.size()]));

        super.visitMaxs(0, 0);
        super.visitEnd();
        if (StandardTrampoline.debug)
            ;//System.out.println(ANSIColorUtils.red(" yield ends"));
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        if (StandardTrampoline.debug)
            ;//System.out.println(" local variable <" + name + "> <" + desc + "> <" + signature + "> <" + start + "> <" + end + "> index = <" + index + ">");
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        // call it
        super.visitMethodInsn(opcode, owner, name, desc);

        if (owner.equals("field/bytecode/protect/yield/YieldUtilities") && name.equals("yield")) {

            // duplicate _valid_ locals

            Iterator<Integer> i = validLocals.iterator();
            Iterator<Type> i2 = validLocalTypes.iterator();

            // push(validLocals.size());
            TypesContext context = typeAnalysis.nextPauseContext();
            if (StandardTrampoline.debug)
                ;//System.out.println(" vars are <" + context.getVars() + ">");

            Map mm = new TreeMap<Integer, String>();
            Map m = (Map) ReflectionTools.illegalGetObject(this, "locals");

            int max = 0;
            for (Map.Entry<Integer, String> localsToSave : ((Map<Integer, String>) context.getVars()).entrySet()) {
                // assert
                // m.containsKey(localsToSave.getKey())
                // : localsToSave.getKey() + " " + m;
                if (m.containsKey(localsToSave.getKey())) {
                    int mq = (Integer) m.get(localsToSave.getKey());
                    if (mq > max)
                        max = mq;
                    mm.put(mq, localsToSave.getValue());
                }
            }

            push(max);
            newArray(Type.getType(Object.class));

            for (Map.Entry<Integer, String> localsToSave : ((Map<Integer, String>) mm).entrySet()) {
                dup();
                push(localsToSave.getKey().intValue() - 1);
                String typeName = localsToSave.getValue();

                Type t = Type.getType(typeName.contains("/") ? "L" + typeName + ";" : typeName.substring(1));

                if (StandardTrampoline.debug)
                    ;//System.out.println(" loading <" + localsToSave.getKey() + ">");
                this.loadLocal(localsToSave.getKey().intValue(), t);
                if (!typeName.contains("/"))
                    box(t);

                if (StandardTrampoline.debug)
                    ;//System.out.println(" type = <" + typeName + ">");

                // this.arrayStore(t);
                mv.visitInsn(Opcodes.AASTORE);
            }

            push(this.name);
            loadThis();
            push(onMethod.getName());
            push(jumpLabels.size());

            invokeStatic(Type.getType(BasicInstrumentation2.class), new Method("handle_yieldStore", Type.getType(Object.class), new Type[]{Type.getType(Object.class), Type.getType(Object[].class), Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class), Type.INT_TYPE}));

            // here it comes
            if (onMethod.getReturnType().getSort() == Type.OBJECT) {
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

            Label newLabel = mark();
            jumpLabels.add(newLabel);

            // now, we start in reverse

            push(this.name);
            loadThis();
            push(onMethod.getName());

            invokeStatic(Type.getType(BasicInstrumentation2.class), new Method("handle_yieldLoad", Type.getType(Object[].class), new Type[]{Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class)}));

            if (StandardTrampoline.debug)
                ;//System.out.println(" --- load --- <" + newLabel + ">");

            i = validLocals.iterator();
            i2 = validLocalTypes.iterator();

            for (Map.Entry<Integer, String> localsToSave : ((Map<Integer, String>) mm).entrySet()) {
                dup();
                push(localsToSave.getKey().intValue() - 1);
                String typeName = localsToSave.getValue();

                Type t = Type.getType(typeName.contains("/") ? "L" + typeName + ";" : typeName.substring(1));
                mv.visitInsn(Opcodes.AALOAD);
                // this.arrayLoad(t);

                if (typeName.contains("/")) {
                    this.checkCast(t);
                }
                if (!typeName.contains("/"))
                    unbox(t);
                this.storeLocal(localsToSave.getKey().intValue(), t);
            }

            if (StandardTrampoline.debug)
                ;//System.out.println(ANSIColorUtils.red(" yield :instrumented yield"));
        }

        yieldNumber++;
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode, var);

        Map m = (Map) ReflectionTools.illegalGetObject(this, "locals");

        if (StandardTrampoline.debug)
            ;//System.out.println(" var instruction <" + opcode + "> <" + var + "> <" + m.get(var) + ">");

        if (var != 0) {
            if (!validLocals.contains(var)) {
                if (opcode == Opcodes.ASTORE || opcode == Opcodes.ALOAD) {
                    validLocals.add(var);
                    validLocalTypes.add(null);
                } else if (opcode == Opcodes.ISTORE || opcode == Opcodes.ILOAD) {
                    validLocals.add(var);
                    validLocalTypes.add(Type.INT_TYPE);
                } else if (opcode == Opcodes.FSTORE || opcode == Opcodes.FLOAD) {
                    validLocals.add(var);
                    validLocalTypes.add(Type.FLOAT_TYPE);
                } else {
                    if (StandardTrampoline.debug)
                        ;//System.out.println(" opcode is <" + opcode + "> for <" + var + ">");
                }
            }
            // else
            // {
            // }
            // todo --- arrays?
        }
    }

    abstract public int yieldIndexFor(String fromName, Object fromThis, String methodName);

    abstract public Object[] yieldLoad(String fromName, Object fromThis, String methodName);

    abstract public Object yieldStore(Object wasReturn, Object[] localStorage, String fromName, Object fromThis, String methodName, int resumeLabel);
}
