package field.protect.asm.visitors;

import org.objectweb.asm.*;


public abstract
class AbstractClassVisitor extends ClassVisitor {
    public
    AbstractClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }


}