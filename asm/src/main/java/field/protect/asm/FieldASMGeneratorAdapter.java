package field.protect.asm; /***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.objectweb.asm.*;
import org.objectweb.asm.commons.TableSwitchGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link org.objectweb.asm.MethodVisitor} with convenient methods to generate
 * code. For example, using this adapter, the class below
 * <p/>
 * <pre>
 * public class Example {
 *     public static void main(String[] args) {
 *         System.out.println(&quot;Hello world!&quot;);
 *     }
 * }
 * </pre>
 * <p/>
 * can be generated as follows:
 * <p/>
 * <pre>
 * ClassWriter cw = new ClassWriter(true);
 * cw.visit(V1_1, ACC_PUBLIC, &quot;Example&quot;, null, &quot;java/lang/Object&quot;, null);
 *
 * Method m = Method.getMethod(&quot;void &lt;init&gt; ()&quot;);
 * GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, m, null, null, cw);
 * mg.loadThis();
 * mg.invokeConstructor(ASMType.getType(Object.class), m);
 * mg.returnValue();
 * mg.endMethod();
 *
 * m = Method.getMethod(&quot;void main (String[])&quot;);
 * mg = new GeneratorAdapter(ACC_PUBLIC + ACC_STATIC, m, null, null, cw);
 * mg.getStatic(ASMType.getType(System.class), &quot;out&quot;, ASMType.getType(PrintStream.class));
 * mg.push(&quot;Hello world!&quot;);
 * mg.invokeVirtual(ASMType.getType(PrintStream.class),
 *         Method.getMethod(&quot;void println (String)&quot;));
 * mg.returnValue();
 * mg.endMethod();
 *
 * cw.visitEnd();
 * </pre>
 *
 * @author Juozas Baliuka
 * @author Chris Nokleberg
 * @author Eric Bruneton
 * @author Prashant Deva
 */
@SuppressWarnings({"UnusedDeclaration", "UnnecessaryBoxing", "ForLoopReplaceableByForEach"})
public
class FieldASMGeneratorAdapter extends ASMLocalVarSorter {

    private static final String CLDESC = "Ljava/lang/Class;";

    private static final ASMType BYTE_TYPE = ASMType.getObjectType("java/lang/Byte");

    private static final ASMType BOOLEAN_TYPE = ASMType.getObjectType("java/lang/Boolean");

    private static final ASMType SHORT_TYPE = ASMType.getObjectType("java/lang/Short");

    private static final ASMType CHARACTER_TYPE = ASMType.getObjectType("java/lang/Character");

    private static final ASMType INTEGER_TYPE = ASMType.getObjectType("java/lang/Integer");

    private static final ASMType FLOAT_TYPE = ASMType.getObjectType("java/lang/Float");

    private static final ASMType LONG_TYPE = ASMType.getObjectType("java/lang/Long");

    private static final ASMType DOUBLE_TYPE = ASMType.getObjectType("java/lang/Double");

    private static final ASMType NUMBER_TYPE = ASMType.getObjectType("java/lang/Number");

    private static final ASMType OBJECT_TYPE = ASMType.getObjectType("java/lang/Object");

    private static final ASMMethod BOOLEAN_VALUE = ASMMethod.getMethod("boolean booleanValue()");

    private static final ASMMethod CHAR_VALUE = ASMMethod.getMethod("char charValue()");

    private static final ASMMethod INT_VALUE = ASMMethod.getMethod("int intValue()");

    private static final ASMMethod FLOAT_VALUE = ASMMethod.getMethod("float floatValue()");

    private static final ASMMethod LONG_VALUE = ASMMethod.getMethod("long longValue()");

    private static final ASMMethod DOUBLE_VALUE = ASMMethod.getMethod("double doubleValue()");

    /**
     * Constant for the {@link #math math} method.
     */
    public static final int ADD = Opcodes.IADD;

    /**
     * Constant for the {@link #math math} method.
     */
    public static final int SUB = Opcodes.ISUB;

    /**
     * Constant for the {@link #math math} method.
     */
    public static final int MUL = Opcodes.IMUL;

    /**
     * Constant for the {@link #math math} method.
     */
    public static final int DIV = Opcodes.IDIV;

    /**
     * Constant for the {@link #math math} method.
     */
    public static final int REM = Opcodes.IREM;

    /**
     * Constant for the {@link #math math} method.
     */
    public static final int NEG = Opcodes.INEG;

    /**
     * Constant for the {@link #math math} method.
     */
    public static final int SHL = Opcodes.ISHL;

    /**
     * Constant for the {@link #math math} method.
     */
    public static final int SHR = Opcodes.ISHR;

    /**
     * Constant for the {@link #math math} method.
     */
    public static final int USHR = Opcodes.IUSHR;

    /**
     * Constant for the {@link #math math} method.
     */
    public static final int AND = Opcodes.IAND;

    /**
     * Constant for the {@link #math math} method.
     */
    public static final int OR = Opcodes.IOR;

    /**
     * Constant for the {@link #math math} method.
     */
    public static final int XOR = Opcodes.IXOR;

    /**
     * Constant for the {@link #ifCmp ifCmp} method.
     */
    public static final int EQ = Opcodes.IFEQ;

    /**
     * Constant for the {@link #ifCmp ifCmp} method.
     */
    public static final int NE = Opcodes.IFNE;

    /**
     * Constant for the {@link #ifCmp ifCmp} method.
     */
    public static final int LT = Opcodes.IFLT;

    /**
     * Constant for the {@link #ifCmp ifCmp} method.
     */
    public static final int GE = Opcodes.IFGE;

    /**
     * Constant for the {@link #ifCmp ifCmp} method.
     */
    public static final int GT = Opcodes.IFGT;

    /**
     * Constant for the {@link #ifCmp ifCmp} method.
     */
    public static final int LE = Opcodes.IFLE;

    /**
     * Access flags of the method visited by this adapter.
     */
    private final int access;

    /**
     * Return ASMType of the method visited by this adapter.
     */
    private final ASMType returnType;

    /**
     * Argument types of the method visited by this adapter.
     */
    private final ASMType[] argumentTypes;

    /**
     * Types of the local variables of the method visited by this adapter.
     */
    private final List<ASMType> localTypes = new ArrayList<ASMType>();

    /**
     * Creates a new {@link FieldASMGeneratorAdapter}. <i>Subclasses must not use this
     * constructor</i>. Instead, they must use the
     * {@link #FieldASMGeneratorAdapter(int, MethodVisitor, int, String, String)}
     * version.
     *
     * @param mv     the method visitor to which this adapter delegates calls.
     * @param access the method's access flags (see {@link Opcodes}).
     * @param name   the method's name.
     * @param desc   the method's descriptor (see {@link ASMType type}).
     * @throws IllegalStateException If a subclass calls this constructor.
     */
    public
    FieldASMGeneratorAdapter(final MethodVisitor mv, final int access, final String name, final String desc) {
        this(Opcodes.ASM5, mv, access, name, desc);
//        if (getClass() != FieldASMGeneratorAdapter.class) {
//            throw new IllegalStateException();
//        }
    }

    /**
     * Creates a new {@link FieldASMGeneratorAdapter}.
     *
     * @param api    the ASM API version implemented by this visitor. Must be one
     *               of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     * @param mv     the method visitor to which this adapter delegates calls.
     * @param access the method's access flags (see {@link Opcodes}).
     * @param name   the method's name.
     * @param desc   the method's descriptor (see {@link ASMType type}).
     */
    protected
    FieldASMGeneratorAdapter(final int api,
                             final MethodVisitor mv,
                             final int access,
                             final String name,
                             final String desc) {
        super(access, desc, mv);
        this.access = access;
        this.returnType = ASMType.getReturnType(desc);
        this.argumentTypes = ASMType.getArgumentTypes(desc);
    }

    /**
     * Creates a new {@link FieldASMGeneratorAdapter}. <i>Subclasses must not use this
     * constructor</i>. Instead, they must use the
     * {@link #FieldASMGeneratorAdapter(int, MethodVisitor, int, String, String)}
     * version.
     *
     * @param access access flags of the adapted method.
     * @param method the adapted method.
     * @param mv     the method visitor to which this adapter delegates calls.
     */
    public
    FieldASMGeneratorAdapter(final int access, final ASMMethod method, final MethodVisitor mv) {
        this(mv, access, null, method.getDescriptor());
    }

    /**
     * Creates a new {@link FieldASMGeneratorAdapter}. <i>Subclasses must not use this
     * constructor</i>. Instead, they must use the
     * {@link #FieldASMGeneratorAdapter(int, MethodVisitor, int, String, String)}
     * version.
     *
     * @param access     access flags of the adapted method.
     * @param method     the adapted method.
     * @param signature  the signature of the adapted method (may be <tt>null</tt>).
     * @param exceptions the exceptions thrown by the adapted method (may be
     *                   <tt>null</tt>).
     * @param cv         the class visitor to which this adapter delegates calls.
     */
    public
    FieldASMGeneratorAdapter(final int access,
                             final ASMMethod method,
                             final String signature,
                             final ASMType[] exceptions,
                             final ClassVisitor cv) {
        this(access,
             method,
             cv.visitMethod(access, method.getName(), method.getDescriptor(), signature, getInternalNames(exceptions)));
    }

    /**
     * Returns the internal names of the given types.
     *
     * @param types a set of types.
     * @return the internal names of the given types.
     */
    private static
    String[] getInternalNames(final ASMType[] types) {
        if (types == null) {
            return null;
        }
        String[] names = new String[types.length];
        for (int i = 0; i < names.length; ++i) {
            names[i] = types[i].getInternalName();
        }
        return names;
    }

    // ------------------------------------------------------------------------
    // Instructions to push constants on the stack
    // ------------------------------------------------------------------------

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack.
     */
    public
    void push(final boolean value) {
        push(value ? 1 : 0);
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack.
     */
    public
    void push(final int value) {
        if ((value >= -1) && (value <= 5)) {
            mv.visitInsn(Opcodes.ICONST_0 + value);
        }
        else if ((value >= Byte.MIN_VALUE) && (value <= Byte.MAX_VALUE)) {
            mv.visitIntInsn(Opcodes.BIPUSH, value);
        }
        else if ((value >= Short.MIN_VALUE) && (value <= Short.MAX_VALUE)) {
            mv.visitIntInsn(Opcodes.SIPUSH, value);
        }
        else {
            mv.visitLdcInsn(new Integer(value));
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack.
     */
    public
    void push(final long value) {
        if ((value == 0L) || (value == 1L)) {
            mv.visitInsn(Opcodes.LCONST_0 + (int) value);
        }
        else {
            mv.visitLdcInsn(new Long(value));
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack.
     */
    public
    void push(final float value) {
        int bits = Float.floatToIntBits(value);
        if ((bits == 0L) || (bits == 0x3f800000) || (bits == 0x40000000)) { // 0..2
            mv.visitInsn(Opcodes.FCONST_0 + (int) value);
        }
        else {
            mv.visitLdcInsn(new Float(value));
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack.
     */
    public
    void push(final double value) {
        long bits = Double.doubleToLongBits(value);
        if ((bits == 0L) || (bits == 0x3ff0000000000000L)) { // +0.0d and 1.0d
            mv.visitInsn(Opcodes.DCONST_0 + (int) value);
        }
        else {
            mv.visitLdcInsn(new Double(value));
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack. May be <tt>null</tt>.
     */
    public
    void push(final String value) {
        if (value == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        else {
            mv.visitLdcInsn(value);
        }
    }

    /**
     * Generates the instruction to push the given value on the stack.
     *
     * @param value the value to be pushed on the stack.
     */
    public
    void push(final ASMType value) {
        if (value == null) {
            mv.visitInsn(Opcodes.ACONST_NULL);
        }
        else {
            switch (value.getSort()) {
                case ASMType.BOOLEAN:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Boolean", "TYPE", CLDESC);
                    break;
                case ASMType.CHAR:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Character", "TYPE", CLDESC);
                    break;
                case ASMType.BYTE:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Byte", "TYPE", CLDESC);
                    break;
                case ASMType.SHORT:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Short", "TYPE", CLDESC);
                    break;
                case ASMType.INT:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Integer", "TYPE", CLDESC);
                    break;
                case ASMType.FLOAT:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Float", "TYPE", CLDESC);
                    break;
                case ASMType.LONG:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Long", "TYPE", CLDESC);
                    break;
                case ASMType.DOUBLE:
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Double", "TYPE", CLDESC);
                    break;
                default:
                    mv.visitLdcInsn(value);
            }
        }
    }

    /**
     * Generates the instruction to push a handle on the stack.
     *
     * @param handle the handle to be pushed on the stack.
     */
    public
    void push(final Handle handle) {
        mv.visitLdcInsn(handle);
    }

    // ------------------------------------------------------------------------
    // Instructions to load and store method arguments
    // ------------------------------------------------------------------------

    /**
     * Returns the index of the given method argument in the frame's local
     * variables array.
     *
     * @param arg the index of a method argument.
     * @return the index of the given method argument in the frame's local
     * variables array.
     */
    private
    int getArgIndex(final int arg) {
        int index = ((access & Opcodes.ACC_STATIC) == 0) ? 1 : 0;
        for (int i = 0; i < arg; i++) {
            index += argumentTypes[i].getSize();
        }
        return index;
    }

    /**
     * Generates the instruction to push a local variable on the stack.
     *
     * @param ASMType the ASMType of the local variable to be loaded.
     * @param index   an index in the frame's local variables array.
     */
    private
    void loadInsn(final ASMType type, final int index) {
        mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), index);
    }

    /**
     * Generates the instruction to store the top stack value in a local
     * variable.
     *
     * @param ASMType the ASMType of the local variable to be stored.
     * @param index   an index in the frame's local variables array.
     */
    private
    void storeInsn(final ASMType type, final int index) {
        mv.visitVarInsn(type.getOpcode(Opcodes.ISTORE), index);
    }

    /**
     * Generates the instruction to load 'this' on the stack.
     */
    public
    void loadThis() {
        if ((access & Opcodes.ACC_STATIC) != 0) {
            throw new IllegalStateException("no 'this' pointer within static method");
        }
        mv.visitVarInsn(Opcodes.ALOAD, 0);
    }

    /**
     * Generates the instruction to load the given method argument on the stack.
     *
     * @param arg the index of a method argument.
     */
    public
    void loadArg(final int arg) {
        loadInsn(argumentTypes[arg], getArgIndex(arg));
    }

    /**
     * Generates the instructions to load the given method arguments on the
     * stack.
     *
     * @param arg   the index of the first method argument to be loaded.
     * @param count the number of method arguments to be loaded.
     */
    public
    void loadArgs(final int arg, final int count) {
        int index = getArgIndex(arg);
        for (int i = 0; i < count; ++i) {
            ASMType t = argumentTypes[arg + i];
            loadInsn(t, index);
            index += t.getSize();
        }
    }

    /**
     * Generates the instructions to load all the method arguments on the stack.
     */
    public
    void loadArgs() {
        loadArgs(0, argumentTypes.length);
    }

    /**
     * Generates the instructions to load all the method arguments on the stack,
     * as a single object array.
     */
    public
    void loadArgArray() {
        push(argumentTypes.length);
        newArray(OBJECT_TYPE);
        for (int i = 0; i < argumentTypes.length; i++) {
            dup();
            push(i);
            loadArg(i);
            box(argumentTypes[i]);
            arrayStore(OBJECT_TYPE);
        }
    }

    /**
     * Generates the instruction to store the top stack value in the given
     * method argument.
     *
     * @param arg the index of a method argument.
     */
    public
    void storeArg(final int arg) {
        storeInsn(argumentTypes[arg], getArgIndex(arg));
    }

    // ------------------------------------------------------------------------
    // Instructions to load and store local variables
    // ------------------------------------------------------------------------

    /**
     * Returns the ASMType of the given local variable.
     *
     * @param local a local variable identifier, as returned by
     *              {@link org.objectweb.asm.commons.ASMLocalVarSorter#newLocal(ASMType) newLocal()}.
     * @return the ASMType of the given local variable.
     */
    public
    ASMType getLocalType(final int local) {
        return localTypes.get(local - firstLocal);
    }


    protected
    void setLocalType(final int local, final ASMType type) {
        int index = local - firstLocal;
        while (localTypes.size() < (index + 1)) {
            localTypes.add(null);
        }
        localTypes.set(index, type);
    }

    /**
     * Generates the instruction to load the given local variable on the stack.
     *
     * @param local a local variable identifier, as returned by
     *              {@link org.objectweb.asm.commons.ASMLocalVarSorter#newLocal(ASMType) newLocal()}.
     */
    public
    void loadLocal(final int local) {
        loadInsn(getLocalType(local), local);
    }

    /**
     * Generates the instruction to load the given local variable on the stack.
     *
     * @param local   a local variable identifier, as returned by
     *                {@link org.objectweb.asm.commons.ASMLocalVarSorter#newLocal(ASMType) newLocal()}.
     * @param ASMType the ASMType of this local variable.
     */
    public
    void loadLocal(final int local, final ASMType type) {
        setLocalType(local, type);
        loadInsn(type, local);
    }

    /**
     * Generates the instruction to store the top stack value in the given local
     * variable.
     *
     * @param local a local variable identifier, as returned by
     *              {@link org.objectweb.asm.commons.ASMLocalVarSorter#newLocal(ASMType) newLocal()}.
     */
    public
    void storeLocal(final int local) {
        storeInsn(getLocalType(local), local);
    }

    /**
     * Generates the instruction to store the top stack value in the given local
     * variable.
     *
     * @param local   a local variable identifier, as returned by
     *                {@link org.objectweb.asm.commons.ASMLocalVarSorter#newLocal(ASMType) newLocal()}.
     * @param ASMType the ASMType of this local variable.
     */
    public
    void storeLocal(final int local, final ASMType type) {
        setLocalType(local, type);
        storeInsn(type, local);
    }

    /**
     * Generates the instruction to load an element from an array.
     *
     * @param ASMType the ASMType of the array element to be loaded.
     */
    public
    void arrayLoad(final ASMType type) {
        mv.visitInsn(type.getOpcode(Opcodes.IALOAD));
    }

    /**
     * Generates the instruction to store an element in an array.
     *
     * @param ASMType the ASMType of the array element to be stored.
     */
    public
    void arrayStore(final ASMType type) {
        mv.visitInsn(type.getOpcode(Opcodes.IASTORE));
    }

    // ------------------------------------------------------------------------
    // Instructions to manage the stack
    // ------------------------------------------------------------------------

    /**
     * Generates a POP instruction.
     */
    public
    void pop() {
        mv.visitInsn(Opcodes.POP);
    }

    /**
     * Generates a POP2 instruction.
     */
    public
    void pop2() {
        mv.visitInsn(Opcodes.POP2);
    }

    /**
     * Generates a DUP instruction.
     */
    public
    void dup() {
        mv.visitInsn(Opcodes.DUP);
    }

    /**
     * Generates a DUP2 instruction.
     */
    public
    void dup2() {
        mv.visitInsn(Opcodes.DUP2);
    }

    /**
     * Generates a DUP_X1 instruction.
     */
    public
    void dupX1() {
        mv.visitInsn(Opcodes.DUP_X1);
    }

    /**
     * Generates a DUP_X2 instruction.
     */
    public
    void dupX2() {
        mv.visitInsn(Opcodes.DUP_X2);
    }

    /**
     * Generates a DUP2_X1 instruction.
     */
    public
    void dup2X1() {
        mv.visitInsn(Opcodes.DUP2_X1);
    }

    /**
     * Generates a DUP2_X2 instruction.
     */
    public
    void dup2X2() {
        mv.visitInsn(Opcodes.DUP2_X2);
    }

    /**
     * Generates a SWAP instruction.
     */
    public
    void swap() {
        mv.visitInsn(Opcodes.SWAP);
    }

    /**
     * Generates the instructions to swap the top two stack values.
     *
     * @param prev    ASMType of the top - 1 stack value.
     * @param ASMType ASMType of the top stack value.
     */
    public
    void swap(final ASMType prev, final ASMType type) {
        if (type.getSize() == 1) {
            if (prev.getSize() == 1) {
                swap(); // same as dupX1(), pop();
            }
            else {
                dupX2();
                pop();
            }
        }
        else {
            if (prev.getSize() == 1) {
                dup2X1();
                pop2();
            }
            else {
                dup2X2();
                pop2();
            }
        }
    }

    // ------------------------------------------------------------------------
    // Instructions to do mathematical and logical operations
    // ------------------------------------------------------------------------

    /**
     * Generates the instruction to do the specified mathematical or logical
     * operation.
     *
     * @param op      a mathematical or logical operation. Must be one of ADD, SUB,
     *                MUL, DIV, REM, NEG, SHL, SHR, USHR, AND, OR, XOR.
     * @param ASMType the ASMType of the operand(s) for this operation.
     */
    public
    void math(final int op, final ASMType type) {
        mv.visitInsn(type.getOpcode(op));
    }

    /**
     * Generates the instructions to compute the bitwise negation of the top
     * stack value.
     */
    public
    void not() {
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitInsn(Opcodes.IXOR);
    }

    /**
     * Generates the instruction to increment the given local variable.
     *
     * @param local  the local variable to be incremented.
     * @param amount the amount by which the local variable must be incremented.
     */
    public
    void iinc(final int local, final int amount) {
        mv.visitIincInsn(local, amount);
    }

    /**
     * Generates the instructions to cast a numerical value from one ASMType to
     * another.
     *
     * @param from the ASMType of the top stack value
     * @param to   the ASMType into which this value must be cast.
     */
    public
    void cast(final ASMType from, final ASMType to) {
        if (from != to) {
            if (from == ASMType.DOUBLE_TYPE) {
                if (to == ASMType.FLOAT_TYPE) {
                    mv.visitInsn(Opcodes.D2F);
                }
                else if (to == ASMType.LONG_TYPE) {
                    mv.visitInsn(Opcodes.D2L);
                }
                else {
                    mv.visitInsn(Opcodes.D2I);
                    cast(ASMType.INT_TYPE, to);
                }
            }
            else if (from == ASMType.FLOAT_TYPE) {
                if (to == ASMType.DOUBLE_TYPE) {
                    mv.visitInsn(Opcodes.F2D);
                }
                else if (to == ASMType.LONG_TYPE) {
                    mv.visitInsn(Opcodes.F2L);
                }
                else {
                    mv.visitInsn(Opcodes.F2I);
                    cast(ASMType.INT_TYPE, to);
                }
            }
            else if (from == ASMType.LONG_TYPE) {
                if (to == ASMType.DOUBLE_TYPE) {
                    mv.visitInsn(Opcodes.L2D);
                }
                else if (to == ASMType.FLOAT_TYPE) {
                    mv.visitInsn(Opcodes.L2F);
                }
                else {
                    mv.visitInsn(Opcodes.L2I);
                    cast(ASMType.INT_TYPE, to);
                }
            }
            else {
                if (to == ASMType.BYTE_TYPE) {
                    mv.visitInsn(Opcodes.I2B);
                }
                else if (to == ASMType.CHAR_TYPE) {
                    mv.visitInsn(Opcodes.I2C);
                }
                else if (to == ASMType.DOUBLE_TYPE) {
                    mv.visitInsn(Opcodes.I2D);
                }
                else if (to == ASMType.FLOAT_TYPE) {
                    mv.visitInsn(Opcodes.I2F);
                }
                else if (to == ASMType.LONG_TYPE) {
                    mv.visitInsn(Opcodes.I2L);
                }
                else if (to == ASMType.SHORT_TYPE) {
                    mv.visitInsn(Opcodes.I2S);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Instructions to do boxing and unboxing operations
    // ------------------------------------------------------------------------

    private static
    ASMType getBoxedType(final ASMType type) {
        switch (type.getSort()) {
            case ASMType.BYTE:
                return ASMType.BYTE_TYPE;
            case ASMType.BOOLEAN:
                return BOOLEAN_TYPE;
            case ASMType.SHORT:
                return SHORT_TYPE;
            case ASMType.CHAR:
                return CHARACTER_TYPE;
            case ASMType.INT:
                return INTEGER_TYPE;
            case ASMType.FLOAT:
                return FLOAT_TYPE;
            case ASMType.LONG:
                return LONG_TYPE;
            case ASMType.DOUBLE:
                return DOUBLE_TYPE;
        }
        return type;
    }

    /**
     * Generates the instructions to box the top stack value. This value is
     * replaced by its boxed equivalent on top of the stack.
     *
     * @param ASMType the ASMType of the top stack value.
     */
    public
    void box(final ASMType type) {
        if ((type.getSort() == ASMType.OBJECT) || (type.getSort() == ASMType.ARRAY)) {
            return;
        }
        if (type == ASMType.VOID_TYPE) {
            push((String) null);
        }
        else {
            ASMType boxed = getBoxedType(type);
            newInstance(boxed);
            if (type.getSize() == 2) {
                // Pp -> Ppo -> oPpo -> ooPpo -> ooPp -> o
                dupX2();
                dupX2();
                pop();
            }
            else {
                // p -> po -> opo -> oop -> o
                dupX1();
                swap();
            }
            invokeConstructor(boxed, new ASMMethod("<init>", ASMType.VOID_TYPE, new ASMType[]{type}));
        }
    }

    /**
     * Generates the instructions to box the top stack value using Java 5's
     * valueOf() method. This value is replaced by its boxed equivalent on top
     * of the stack.
     *
     * @param ASMType the ASMType of the top stack value.
     */
    public
    void valueOf(final ASMType type) {
        if ((type.getSort() == ASMType.OBJECT) || (type.getSort() == ASMType.ARRAY)) {
            return;
        }
        if (type == ASMType.VOID_TYPE) {
            push((String) null);
        }
        else {
            ASMType boxed = getBoxedType(type);
            invokeStatic(boxed, new ASMMethod("valueOf", boxed, new ASMType[]{type}));
        }
    }

    /**
     * Generates the instructions to unbox the top stack value. This value is
     * replaced by its unboxed equivalent on top of the stack.
     *
     * @param ASMType the ASMType of the top stack value.
     */
    public
    void unbox(final ASMType type) {
        ASMType t = NUMBER_TYPE;
        ASMMethod sig = null;
        switch (type.getSort()) {
            case ASMType.VOID:
                return;
            case ASMType.CHAR:
                t = CHARACTER_TYPE;
                sig = CHAR_VALUE;
                break;
            case ASMType.BOOLEAN:
                t = BOOLEAN_TYPE;
                sig = BOOLEAN_VALUE;
                break;
            case ASMType.DOUBLE:
                sig = DOUBLE_VALUE;
                break;
            case ASMType.FLOAT:
                sig = FLOAT_VALUE;
                break;
            case ASMType.LONG:
                sig = LONG_VALUE;
                break;
            case ASMType.INT:
            case ASMType.SHORT:
            case ASMType.BYTE:
                sig = INT_VALUE;
        }
        if (sig == null) {
            checkCast(type);
        }
        else {
            checkCast(t);
            invokeVirtual(t, sig);
        }
    }

    // ------------------------------------------------------------------------
    // Instructions to jump to other instructions
    // ------------------------------------------------------------------------

    /**
     * Creates a new {@link Label}.
     *
     * @return a new {@link Label}.
     */
    public
    Label newLabel() {
        return new Label();
    }

    /**
     * Marks the current code position with the given label.
     *
     * @param label a label.
     */
    public
    void mark(final Label label) {
        mv.visitLabel(label);
    }

    /**
     * Marks the current code position with a new label.
     *
     * @return the label that was created to mark the current code position.
     */
    public
    Label mark() {
        Label label = new Label();
        mv.visitLabel(label);
        return label;
    }

    /**
     * Generates the instructions to jump to a label based on the comparison of
     * the top two stack values.
     *
     * @param ASMType the ASMType of the top two stack values.
     * @param mode    how these values must be compared. One of EQ, NE, LT, GE, GT,
     *                LE.
     * @param label   where to jump if the comparison result is <tt>true</tt>.
     */
    public
    void ifCmp(final ASMType type, final int mode, final Label label) {
        switch (type.getSort()) {
            case ASMType.LONG:
                mv.visitInsn(Opcodes.LCMP);
                break;
            case ASMType.DOUBLE:
                mv.visitInsn(((mode == GE) || (mode == GT)) ? Opcodes.DCMPL : Opcodes.DCMPG);
                break;
            case ASMType.FLOAT:
                mv.visitInsn(((mode == GE) || (mode == GT)) ? Opcodes.FCMPL : Opcodes.FCMPG);
                break;
            case ASMType.ARRAY:
            case ASMType.OBJECT:
                switch (mode) {
                    case EQ:
                        mv.visitJumpInsn(Opcodes.IF_ACMPEQ, label);
                        return;
                    case NE:
                        mv.visitJumpInsn(Opcodes.IF_ACMPNE, label);
                        return;
                }
                throw new IllegalArgumentException("Bad comparison for ASMType " + type);
            default:
                int intOp = -1;
                switch (mode) {
                    case EQ:
                        intOp = Opcodes.IF_ICMPEQ;
                        break;
                    case NE:
                        intOp = Opcodes.IF_ICMPNE;
                        break;
                    case GE:
                        intOp = Opcodes.IF_ICMPGE;
                        break;
                    case LT:
                        intOp = Opcodes.IF_ICMPLT;
                        break;
                    case LE:
                        intOp = Opcodes.IF_ICMPLE;
                        break;
                    case GT:
                        intOp = Opcodes.IF_ICMPGT;
                        break;
                }
                mv.visitJumpInsn(intOp, label);
                return;
        }
        mv.visitJumpInsn(mode, label);
    }

    /**
     * Generates the instructions to jump to a label based on the comparison of
     * the top two integer stack values.
     *
     * @param mode  how these values must be compared. One of EQ, NE, LT, GE, GT,
     *              LE.
     * @param label where to jump if the comparison result is <tt>true</tt>.
     */
    public
    void ifICmp(final int mode, final Label label) {
        ifCmp(ASMType.INT_TYPE, mode, label);
    }

    /**
     * Generates the instructions to jump to a label based on the comparison of
     * the top integer stack value with zero.
     *
     * @param mode  how these values must be compared. One of EQ, NE, LT, GE, GT,
     *              LE.
     * @param label where to jump if the comparison result is <tt>true</tt>.
     */
    public
    void ifZCmp(final int mode, final Label label) {
        mv.visitJumpInsn(mode, label);
    }

    /**
     * Generates the instruction to jump to the given label if the top stack
     * value is null.
     *
     * @param label where to jump if the condition is <tt>true</tt>.
     */
    public
    void ifNull(final Label label) {
        mv.visitJumpInsn(Opcodes.IFNULL, label);
    }

    /**
     * Generates the instruction to jump to the given label if the top stack
     * value is not null.
     *
     * @param label where to jump if the condition is <tt>true</tt>.
     */
    public
    void ifNonNull(final Label label) {
        mv.visitJumpInsn(Opcodes.IFNONNULL, label);
    }

    /**
     * Generates the instruction to jump to the given label.
     *
     * @param label where to jump if the condition is <tt>true</tt>.
     */
    public
    void goTo(final Label label) {
        mv.visitJumpInsn(Opcodes.GOTO, label);
    }

    /**
     * Generates a RET instruction.
     *
     * @param local a local variable identifier, as returned by
     *              {@link org.objectweb.asm.commons.ASMLocalVarSorter#newLocal(ASMType) newLocal()}.
     */
    public
    void ret(final int local) {
        mv.visitVarInsn(Opcodes.RET, local);
    }

    /**
     * Generates the instructions for a switch statement.
     *
     * @param keys      the switch case keys.
     * @param generator a generator to generate the code for the switch cases.
     */
    public
    void tableSwitch(final int[] keys, final TableSwitchGenerator generator) {
        float density;
        if (keys.length == 0) {
            density = 0;
        }
        else {
            density = (float) keys.length / ((keys[keys.length - 1] - keys[0]) + 1);
        }
        tableSwitch(keys, generator, density >= 0.5f);
    }

    /**
     * Generates the instructions for a switch statement.
     *
     * @param keys      the switch case keys.
     * @param generator a generator to generate the code for the switch cases.
     * @param useTable  <tt>true</tt> to use a TABLESWITCH instruction, or
     *                  <tt>false</tt> to use a LOOKUPSWITCH instruction.
     */
    public
    void tableSwitch(final int[] keys, final TableSwitchGenerator generator, final boolean useTable) {
        for (int i = 1; i < keys.length; ++i) {
            if (keys[i] < keys[i - 1]) {
                throw new IllegalArgumentException("keys must be sorted ascending");
            }
        }
        Label def = newLabel();
        Label end = newLabel();
        if (keys.length > 0) {
            int len = keys.length;
            int min = keys[0];
            int max = keys[len - 1];
            int range = (max - min) + 1;
            if (useTable) {
                Label[] labels = new Label[range];
                Arrays.fill(labels, def);
                for (int i = 0; i < len; ++i) {
                    labels[keys[i] - min] = newLabel();
                }
                mv.visitTableSwitchInsn(min, max, def, labels);
                for (int i = 0; i < range; ++i) {
                    Label label = labels[i];
                    if (label != def) {
                        mark(label);
                        generator.generateCase(i + min, end);
                    }
                }
            }
            else {
                Label[] labels = new Label[len];
                for (int i = 0; i < len; ++i) {
                    labels[i] = newLabel();
                }
                mv.visitLookupSwitchInsn(def, keys, labels);
                for (int i = 0; i < len; ++i) {
                    mark(labels[i]);
                    generator.generateCase(keys[i], end);
                }
            }
        }
        mark(def);
        generator.generateDefault();
        mark(end);
    }

    /**
     * Generates the instruction to return the top stack value to the caller.
     */
    public
    void returnValue() {
        mv.visitInsn(returnType.getOpcode(Opcodes.IRETURN));
    }

    // ------------------------------------------------------------------------
    // Instructions to load and store fields
    // ------------------------------------------------------------------------

    /**
     * Generates a get field or set field instruction.
     *
     * @param opcode    the instruction's opcode.
     * @param ownerType the class in which the field is defined.
     * @param name      the name of the field.
     * @param fieldType the ASMType of the field.
     */
    private
    void fieldInsn(final int opcode, final ASMType ownerType, final String name, final ASMType fieldType) {
        mv.visitFieldInsn(opcode, ownerType.getInternalName(), name, fieldType.getDescriptor());
    }

    /**
     * Generates the instruction to push the value of a static field on the
     * stack.
     *
     * @param owner   the class in which the field is defined.
     * @param name    the name of the field.
     * @param ASMType the ASMType of the field.
     */
    public
    void getStatic(final ASMType owner, final String name, final ASMType type) {
        fieldInsn(Opcodes.GETSTATIC, owner, name, type);
    }

    /**
     * Generates the instruction to store the top stack value in a static field.
     *
     * @param owner   the class in which the field is defined.
     * @param name    the name of the field.
     * @param ASMType the ASMType of the field.
     */
    public
    void putStatic(final ASMType owner, final String name, final ASMType type) {
        fieldInsn(Opcodes.PUTSTATIC, owner, name, type);
    }

    /**
     * Generates the instruction to push the value of a non static field on the
     * stack.
     *
     * @param owner   the class in which the field is defined.
     * @param name    the name of the field.
     * @param ASMType the ASMType of the field.
     */
    public
    void getField(final ASMType owner, final String name, final ASMType type) {
        fieldInsn(Opcodes.GETFIELD, owner, name, type);
    }

    /**
     * Generates the instruction to store the top stack value in a non static
     * field.
     *
     * @param owner   the class in which the field is defined.
     * @param name    the name of the field.
     * @param ASMType the ASMType of the field.
     */
    public
    void putField(final ASMType owner, final String name, final ASMType type) {
        fieldInsn(Opcodes.PUTFIELD, owner, name, type);
    }

    // ------------------------------------------------------------------------
    // Instructions to invoke methods
    // ------------------------------------------------------------------------

    /**
     * Generates an invoke method instruction.
     *
     * @param opcode  the instruction's opcode.
     * @param ASMType the class in which the method is defined.
     * @param method  the method to be invoked.
     */
    private
    void invokeInsn(final int opcode, final ASMType type, final ASMMethod method, final boolean itf) {
        String owner = (type.getSort() == ASMType.ARRAY) ? type.getDescriptor() : type.getInternalName();
        mv.visitMethodInsn(opcode, owner, method.getName(), method.getDescriptor(), itf);
    }

    /**
     * Generates the instruction to invoke a normal method.
     *
     * @param owner  the class in which the method is defined.
     * @param method the method to be invoked.
     */
    public
    void invokeVirtual(final ASMType owner, final ASMMethod method) {
        invokeInsn(Opcodes.INVOKEVIRTUAL, owner, method, false);
    }

    /**
     * Generates the instruction to invoke a constructor.
     *
     * @param ASMType the class in which the constructor is defined.
     * @param method  the constructor to be invoked.
     */
    public
    void invokeConstructor(final ASMType type, final ASMMethod method) {
        invokeInsn(Opcodes.INVOKESPECIAL, type, method, false);
    }

    /**
     * Generates the instruction to invoke a static method.
     *
     * @param owner  the class in which the method is defined.
     * @param method the method to be invoked.
     */
    public
    void invokeStatic(final ASMType owner, final ASMMethod method) {
        invokeInsn(Opcodes.INVOKESTATIC, owner, method, false);
    }

    /**
     * Generates the instruction to invoke an interface method.
     *
     * @param owner  the class in which the method is defined.
     * @param method the method to be invoked.
     */
    public
    void invokeInterface(final ASMType owner, final ASMMethod method) {
        invokeInsn(Opcodes.INVOKEINTERFACE, owner, method, true);
    }

    /**
     * Generates an invokedynamic instruction.
     *
     * @param name    the method's name.
     * @param desc    the method's descriptor (see {@link ASMType type}).
     * @param bsm     the bootstrap method.
     * @param bsmArgs the bootstrap method constant arguments. Each argument must be
     *                an {@link Integer}, {@link Float}, {@link Long},
     *                {@link Double}, {@link String}, {@link ASMType} or {@link Handle}
     *                value. This method is allowed to modify the content of the
     *                array so a caller should expect that this array may change.
     */
    public
    void invokeDynamic(String name, String desc, Handle bsm, Object... bsmArgs) {
        mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    // ------------------------------------------------------------------------
    // Instructions to create objects and arrays
    // ------------------------------------------------------------------------

    /**
     * Generates a ASMType dependent instruction.
     *
     * @param opcode  the instruction's opcode.
     * @param ASMType the instruction's operand.
     */
    private
    void typeInsn(final int opcode, final ASMType type) {
        mv.visitTypeInsn(opcode, type.getInternalName());
    }

    /**
     * Generates the instruction to create a new object.
     *
     * @param ASMType the class of the object to be created.
     */
    public
    void newInstance(final ASMType type) {
        typeInsn(Opcodes.NEW, type);
    }

    /**
     * Generates the instruction to create a new array.
     *
     * @param ASMType the ASMType of the array elements.
     */
    public
    void newArray(final ASMType type) {
        int typ;
        switch (type.getSort()) {
            case ASMType.BOOLEAN:
                typ = Opcodes.T_BOOLEAN;
                break;
            case ASMType.CHAR:
                typ = Opcodes.T_CHAR;
                break;
            case ASMType.BYTE:
                typ = Opcodes.T_BYTE;
                break;
            case ASMType.SHORT:
                typ = Opcodes.T_SHORT;
                break;
            case ASMType.INT:
                typ = Opcodes.T_INT;
                break;
            case ASMType.FLOAT:
                typ = Opcodes.T_FLOAT;
                break;
            case ASMType.LONG:
                typ = Opcodes.T_LONG;
                break;
            case ASMType.DOUBLE:
                typ = Opcodes.T_DOUBLE;
                break;
            default:
                typeInsn(Opcodes.ANEWARRAY, type);
                return;
        }
        mv.visitIntInsn(Opcodes.NEWARRAY, typ);
    }

    // ------------------------------------------------------------------------
    // Miscelaneous instructions
    // ------------------------------------------------------------------------

    /**
     * Generates the instruction to compute the length of an array.
     */
    public
    void arrayLength() {
        mv.visitInsn(Opcodes.ARRAYLENGTH);
    }

    /**
     * Generates the instruction to throw an exception.
     */
    public
    void throwException() {
        mv.visitInsn(Opcodes.ATHROW);
    }

    /**
     * Generates the instructions to create and throw an exception. The
     * exception class must have a constructor with a single String argument.
     *
     * @param ASMType the class of the exception to be thrown.
     * @param msg     the detailed message of the exception.
     */
    public
    void throwException(final ASMType type, final String msg) {
        newInstance(type);
        dup();
        push(msg);
        invokeConstructor(type, ASMMethod.getMethod("void <init> (String)"));
        throwException();
    }

    /**
     * Generates the instruction to check that the top stack value is of the
     * given ASMType.
     *
     * @param ASMType a class or interface ASMType.
     */
    public
    void checkCast(final ASMType type) {
        if (!type.equals(OBJECT_TYPE)) {
            typeInsn(Opcodes.CHECKCAST, type);
        }
    }

    /**
     * Generates the instruction to test if the top stack value is of the given
     * ASMType.
     *
     * @param ASMType a class or interface ASMType.
     */
    public
    void instanceOf(final ASMType type) {
        typeInsn(Opcodes.INSTANCEOF, type);
    }

    /**
     * Generates the instruction to get the monitor of the top stack value.
     */
    public
    void monitorEnter() {
        mv.visitInsn(Opcodes.MONITORENTER);
    }

    /**
     * Generates the instruction to release the monitor of the top stack value.
     */
    public
    void monitorExit() {
        mv.visitInsn(Opcodes.MONITOREXIT);
    }

    // ------------------------------------------------------------------------
    // Non instructions
    // ------------------------------------------------------------------------

    /**
     * Marks the end of the visited method.
     */
    public
    void endMethod() {
        if ((access & Opcodes.ACC_ABSTRACT) == 0) {
            mv.visitMaxs(0, 0);
        }
        mv.visitEnd();
    }

    /**
     * Marks the start of an exception handler.
     *
     * @param start     beginning of the exception handler's scope (inclusive).
     * @param end       end of the exception handler's scope (exclusive).
     * @param exception internal name of the ASMType of exceptions handled by the
     *                  handler.
     */
    public
    void catchException(final Label start, final Label end, final ASMType exception) {
        if (exception == null) {
            mv.visitTryCatchBlock(start, end, mark(), null);
        }
        else {
            mv.visitTryCatchBlock(start, end, mark(), exception.getInternalName());
        }
    }
}
