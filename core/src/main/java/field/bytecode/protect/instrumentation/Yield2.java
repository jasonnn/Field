package field.bytecode.protect.instrumentation;

import field.protect.asm.ASMMethod;
import field.protect.asm.ASMType;
import field.protect.asm.FieldASMGeneratorAdapter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnalyzerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jason on 7/14/14.
 */
public abstract class Yield2 extends FieldASMGeneratorAdapter implements YieldHandler {
    private final int access;

    private final String className;

    private Label initialJumpLabel;

    private final ASMMethod onMethod;

    private final byte[] originalByteCode;

    private final String parameterName;

    private final int returnNumber;

    private Label startLabel;

    protected final String name;

    protected final HashMap<String, Object> parameters;

    List<Label> jumpLabels = new ArrayList<Label>();

    List<Integer> validLocals = new ArrayList<Integer>();

    List<Type> validLocalTypes = new ArrayList<Type>();

    int yieldNumber = 0;

    AnalyzerAdapter analyzer;

    public Yield2(String name, int access, ASMMethod onMethod, MethodVisitor delegateTo, HashMap<String, Object> parameters, byte[] originalByteCode, String className) {
        // super(name, access, onMethod.getName(),
        // onMethod.getDescriptor(), delegateTo);
        super(new AnalyzerAdapter(className, access, onMethod.getName(), onMethod.getDescriptor(), delegateTo) {
        }, access, onMethod.getName(), onMethod.getDescriptor());

        this.analyzer = (AnalyzerAdapter) this.mv;
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

    }

    @Override
    public void visitCode() {
        // if (StandardTrampoline.debug)
        //System.out.println(ANSIColorUtils.red(" yield begins "));
        super.visitCode();

        initialJumpLabel = this.newLabel();
        this.goTo(initialJumpLabel);

        startLabel = this.mark();
        jumpLabels.add(startLabel);
        analyzer.stack = new ArrayList();
        analyzer.locals = new ArrayList();
    }

    @Override
    public void visitEnd() {
        // insert jump table
        this.visitLabel(initialJumpLabel);
        analyzer.stack = new ArrayList();
        analyzer.locals = new ArrayList();

        // insert code that works out where to jumpFrom
        push(name);
        loadThis();
        push(onMethod.getName());
        invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle_yieldIndex", ASMType.INT_TYPE, new ASMType[]{ASMType.getType(String.class), ASMType.getType(Object.class), ASMType.getType(String.class)}));

        // if (StandardTrampoline.debug)
        //System.out.println(" we have <" + jumpLabels.size() + "> <" + startLabel + ">");
        this.visitTableSwitchInsn(0, jumpLabels.size() - 1, startLabel, jumpLabels.toArray(new Label[jumpLabels.size()]));

        super.visitMaxs(0, 0);
        super.visitEnd();
        // if (StandardTrampoline.debug)
        // ;//System.out.println(ANSIColorUtils.red(" yield ends"));
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {

    }

    @Override
    public void visitInsn(int opcode) {
        //System.out.println(" vi ->:" + opcode + " " + analyzer.locals);
        super.visitInsn(opcode);
        //System.out.println(" vi <-:" + opcode + " " + analyzer.locals);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        //System.out.println(" vii ->:" + opcode + " " + analyzer.locals);
        super.visitIntInsn(opcode, operand);
        //System.out.println(" vii <-:" + opcode + " " + analyzer.locals);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        //System.out.println(" vji ->:" + opcode + " " + analyzer.locals);
        super.visitJumpInsn(opcode, label);
        //System.out.println(" vji <-:" + opcode + " " + analyzer.locals);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        // call it
        //System.out.println(" vmi ->:" + opcode + " " + analyzer.locals);
        super.visitMethodInsn(opcode, owner, name, desc);
        //System.out.println(" vmi <-:" + opcode + " " + analyzer.locals);

        if (owner.equals("field/bytecode/protect/yield/YieldUtilities") && name.equals("yield")) {

            //System.out.println(" inside yield, stack is <" + analyzer.stack + " / " + analyzer.locals + ">");

            push(analyzer.locals.size());
            newArray(Type.getType(Object.class));

            ArrayList wasLocals = new ArrayList(analyzer.locals);
            ArrayList wasStack = new ArrayList(analyzer.stack);

            int n = 0;
            for (Object o : wasLocals) {
                n++;
                //System.out.println(" o = " + o + " " + n);
                if (o == Opcodes.TOP || n == 1)
                    continue;

                dup();
                push(n - 1);

                Type t = null;
                if (o == Opcodes.INTEGER)
                    this.loadLocal(n - 1, t = Type.INT_TYPE);
                else if (o == Opcodes.FLOAT)
                    this.loadLocal(n - 1, t = Type.FLOAT_TYPE);
                else if (o instanceof String)
                    this.loadLocal(n - 1, t = Type.getObjectType(((String) o).contains("/") ? ((String) o) : ((String) o).substring(1)));
                else if (o == Opcodes.DOUBLE)
                    this.loadLocal(n - 1, t = Type.DOUBLE_TYPE);
                else if (o == Opcodes.LONG)
                    this.loadLocal(n - 1, t = Type.LONG_TYPE);
                else
                    throw new IllegalStateException("unhandled <" + o + ">");

                box(t);

                //if (StandardTrampoline.debug)
                //System.out.println(" type = <" + o + ">");

                // this.arrayStore(t);
                mv.visitInsn(Opcodes.AASTORE);
            }

            push(this.name);
            loadThis();
            push(onMethod.getName());
            push(jumpLabels.size());

            invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle_yieldStore", ASMType.getType(Object.class), new ASMType[]{ASMType.getType(Object.class), ASMType.getType(Object[].class), ASMType.getType(String.class), ASMType.getType(Object.class), ASMType.getType(String.class), ASMType.INT_TYPE}));

            // here it comes
            if (onMethod.getASMReturnType().getSort() == ASMType.OBJECT) {
                visitInsn(Opcodes.ARETURN);
            } else if (onMethod.getASMReturnType() == ASMType.INT_TYPE) {
                unbox(Type.INT_TYPE);
                super.visitInsn(Opcodes.IRETURN);
            } else if (onMethod.getASMReturnType() == ASMType.FLOAT_TYPE) {
                unbox(Type.FLOAT_TYPE);
                super.visitInsn(Opcodes.FRETURN);
            } else if (onMethod.getASMReturnType() == ASMType.VOID_TYPE) {
                super.visitInsn(Opcodes.RETURN);
            } else {
                assert false : onMethod.getASMReturnType();
            }

            analyzer.locals = new ArrayList(wasLocals);
            analyzer.stack = new ArrayList(wasStack);

            Label newLabel = mark();
            jumpLabels.add(newLabel);

            // now, we start in reverse

            push(this.name);
            loadThis();
            push(onMethod.getName());

            //  invokeStatic(Type.getType(BasicInstrumentation2.class), new ASMMethod("handle_yieldLoad", Type.getType(Object[].class), new Type[]{Type.getType(String.class), Type.getType(Object.class), Type.getType(String.class)}));
            invokeStatic(Type.getType(BasicInstrumentation2.class),
                    new ASMMethod("handle_yieldLoad", ASMType.OBJECT_ARRAY_TYPE, new ASMType[]{ASMType.STRING_TYPE, ASMType.OBJECT_TYPE, ASMType.STRING_TYPE}));
            // if (StandardTrampoline.debug)
            //System.out.println(" --- load --- <" + newLabel + ">");

            n = 0;
            int off = 1;
            for (Object o : wasLocals) {
                n++;
                if (o == Opcodes.TOP || n == 1)
                    continue;

                dup();
                push(n - off);

                mv.visitInsn(Opcodes.AALOAD);

                Type t = null;
                if (o == Opcodes.INTEGER)
                    unbox(t = Type.INT_TYPE);
                else if (o == Opcodes.FLOAT)
                    unbox(t = Type.FLOAT_TYPE);
                else if (o instanceof String) {
                    t = Type.getObjectType(((String) o).contains("/") ? ((String) o) : ((String) o).substring(1));
                    checkCast(t);
                } else if (o == Opcodes.DOUBLE) {
                    unbox(t = Type.DOUBLE_TYPE);
                } else if (o == Opcodes.LONG) {
                    unbox(t = Type.LONG_TYPE);
                } else
                    throw new IllegalStateException("unhandled <" + o + ">");

                // if (StandardTrampoline.debug)
                //System.out.println(" loading back type = <" + o + ">");
                storeLocal(n - 1, t);
            }

            analyzer.locals = new ArrayList(wasLocals);
            analyzer.stack = new ArrayList(wasStack);

        }

        yieldNumber++;
    }

    abstract public int yieldIndexFor(String fromName, Object fromThis, String methodName);

    abstract public Object[] yieldLoad(String fromName, Object fromThis, String methodName);

    abstract public Object yieldStore(Object wasReturn, Object[] localStorage, String fromName, Object fromThis, String methodName, int resumeLabel);
}
