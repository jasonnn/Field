package field.protect.asm.visitors;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by jason on 7/26/14.
 */
public
class AbstractFieldVisitor extends FieldVisitor{
    public
    AbstractFieldVisitor(FieldVisitor fv) {
        super(Opcodes.ASM5, fv);
    }

}
