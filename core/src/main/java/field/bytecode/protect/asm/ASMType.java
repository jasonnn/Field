package field.bytecode.protect.asm;

import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by jason on 7/16/14.
 */
public class ASMType {
    public final Type delegate;

    public ASMType(Type delegate) {
        this.delegate = delegate;
    }

    public static ASMType from(Type type) {
        return new ASMType(type);
    }

    public static ASMType getType(String typeDescriptor) {
        return from(Type.getType(typeDescriptor));
    }

    public static String getConstructorDescriptor(Constructor c) {
        return Type.getConstructorDescriptor(c);
    }

    public static ASMType getReturnType(Method method) {
        return from(Type.getReturnType(method));
    }

    public int getArgumentsAndReturnSizes() {
        return delegate.getArgumentsAndReturnSizes();
    }

    public String getDescriptor() {
        return delegate.getDescriptor();
    }

    public int getDimensions() {
        return delegate.getDimensions();
    }

    public String getClassName() {
        return delegate.getClassName();
    }

    public static ASMType getMethodType(Type returnType, Type... argumentTypes) {
        return from(Type.getMethodType(returnType, argumentTypes));
    }

    public int getSort() {
        return delegate.getSort();
    }

    public static int getArgumentsAndReturnSizes(String desc) {
        return Type.getArgumentsAndReturnSizes(desc);
    }

    public static String getDescriptor(Class c) {
        return Type.getDescriptor(c);
    }

    public static ASMType getType(Method m) {
        return from(Type.getType(m));
    }

    public static ASMType getObjectType(String internalName) {
        return from(Type.getObjectType(internalName));
    }

    public ASMType getReturnType() {
        return from(delegate.getReturnType());
    }

    public static ASMType getType(Class c) {
        return from(Type.getType(c));
    }

    public static String getInternalName(Class c) {
        return Type.getInternalName(c);
    }

    public ASMType getElementType() {
        return from(delegate.getElementType());
    }

    public int getSize() {
        return delegate.getSize();
    }

    private static ASMType[] adapt(Type[] types) {
        ASMType[] args = new ASMType[types.length];
        for (int i = 0; i < types.length; i++) {
            args[i] = from(types[i]);
        }
        return args;
    }

    public ASMType[] getArgumentTypes() {
        return adapt(delegate.getArgumentTypes());
    }

    public String getInternalName() {
        return delegate.getInternalName();
    }

    public static ASMType[] getArgumentTypes(String methodDescriptor) {
        return adapt(Type.getArgumentTypes(methodDescriptor));
    }

    public static ASMType getType(Constructor c) {
        return from(Type.getType(c));
    }

    public static ASMType getMethodType(String methodDescriptor) {
        return from(Type.getMethodType(methodDescriptor));
    }

    public static ASMType[] getArgumentTypes(Method method) {
        return adapt(Type.getArgumentTypes(method));
    }

    public int getOpcode(int opcode) {
        return delegate.getOpcode(opcode);
    }

    public static String getMethodDescriptor(Method m) {
        return Type.getMethodDescriptor(m);
    }

    public static ASMType getReturnType(String methodDescriptor) {
        return from(Type.getReturnType(methodDescriptor));
    }

    public static String getMethodDescriptor(Type returnType, Type... argumentTypes) {
        return Type.getMethodDescriptor(returnType, argumentTypes);
    }
}
