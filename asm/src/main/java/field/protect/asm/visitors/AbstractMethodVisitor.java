package field.protect.asm.visitors;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by jason on 7/26/14.
 */
public abstract
class AbstractMethodVisitor extends MethodVisitor {
    public
    AbstractMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }

}
