/***
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
package field.protect.asm;


import org.objectweb.asm.commons.Method;

/**
 * A named method descriptor.
 *
 * @author Juozas Baliuka
 * @author Chris Nokleberg
 * @author Eric Bruneton
 */
public class ASMMethod extends Method {

    public static ASMMethod from(Method m) {
        return new ASMMethod(m.getName(), m.getDescriptor());
    }

//    public Method toMethod() {
//        return new Method(name, desc);
//    }
//
//    /**
//     * The method name.
//     */
//    public final String name;
//
//    /**
//     * The method descriptor.
//     */
//    public final String desc;

//    /**
//     * Maps primitive Java type names to their descriptors.
//     */
//    private static final Map<String, String> DESCRIPTORS;
//
//    static {
//        DESCRIPTORS = new HashMap<String, String>();
//        DESCRIPTORS.put("void", "V");
//        DESCRIPTORS.put("byte", "B");
//        DESCRIPTORS.put("char", "C");
//        DESCRIPTORS.put("double", "D");
//        DESCRIPTORS.put("float", "F");
//        DESCRIPTORS.put("int", "I");
//        DESCRIPTORS.put("long", "J");
//        DESCRIPTORS.put("short", "S");
//        DESCRIPTORS.put("boolean", "Z");
//    }

    /**
     * Creates a new {@link Method}.
     *
     * @param name the method's name.
     * @param desc the method's descriptor.
     */
    public ASMMethod(final String name, final String desc) {
        super(name, desc);
//        this.name = name;
//        this.desc = desc;
    }

    /**
     * Creates a new {@link Method}.
     *
     * @param name          the method's name.
     * @param returnType    the method's return type.
     * @param argumentTypes the method's argument types.
     */
    public ASMMethod(final String name, final ASMType returnType,
                     final ASMType[] argumentTypes) {
        this(name, ASMType.getMethodDescriptor(returnType, argumentTypes));
    }

    /**
     * Creates a new {@link Method}.
     *
     * @param m a java.lang.reflect method descriptor
     * @return a {@link Method} corresponding to the given Java method
     * declaration.
     */
    public static ASMMethod getMethod(java.lang.reflect.Method m) {
        return new ASMMethod(m.getName(), ASMType.getMethodDescriptor(m));
    }

    /**
     * Creates a new {@link Method}.
     *
     * @param c a java.lang.reflect constructor descriptor
     * @return a {@link Method} corresponding to the given Java constructor
     * declaration.
     */
    public static ASMMethod getMethod(java.lang.reflect.Constructor<?> c) {
        return new ASMMethod("<init>", ASMType.getConstructorDescriptor(c));
    }

    /**
     * Returns a {@link Method} corresponding to the given Java method
     * declaration.
     *
     * @param method a Java method declaration, without argument names, of the form
     *               "returnType name (argumentType1, ... argumentTypeN)", where
     *               the types are in plain Java (e.g. "int", "float",
     *               "java.util.List", ...). Classes of the java.lang package can
     *               be specified by their unqualified name; all other classes
     *               names must be fully qualified.
     * @return a {@link Method} corresponding to the given Java method
     * declaration.
     * @throws IllegalArgumentException if <code>method</code> could not get parsed.
     */
    public static ASMMethod getMethod(final String method)
            throws IllegalArgumentException {
        return getMethod(method, false);
    }

    /**
     * Returns a {@link Method} corresponding to the given Java method
     * declaration.
     *
     * @param method         a Java method declaration, without argument names, of the form
     *                       "returnType name (argumentType1, ... argumentTypeN)", where
     *                       the types are in plain Java (e.g. "int", "float",
     *                       "java.util.List", ...). Classes of the java.lang package may
     *                       be specified by their unqualified name, depending on the
     *                       defaultPackage argument; all other classes names must be fully
     *                       qualified.
     * @param defaultPackage true if unqualified class names belong to the default package,
     *                       or false if they correspond to java.lang classes. For instance
     *                       "Object" means "Object" if this option is true, or
     *                       "java.lang.Object" otherwise.
     * @return a {@link Method} corresponding to the given Java method
     * declaration.
     * @throws IllegalArgumentException if <code>method</code> could not get parsed.
     */
    public static ASMMethod getMethod(final String method,
                                      final boolean defaultPackage) throws IllegalArgumentException {

        return from(Method.getMethod(method, defaultPackage));
    }


    /**
     * Returns the return type of the method described by this object.
     *
     * @return the return type of the method described by this object.
     */
    public ASMType getASMReturnType() {
        return ASMType.getReturnType(getDescriptor());
    }

    /**
     * Returns the argument types of the method described by this object.
     *
     * @return the argument types of the method described by this object.
     */
    public ASMType[] getASMArgumentTypes() {
        return ASMType.getArgumentTypes(getDescriptor());
    }




}
