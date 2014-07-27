package asm.handlers;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by jason on 7/26/14.
 */
public
class MethodWrappingVisitor extends ClassVisitor {
    public
    MethodWrappingVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }
}
