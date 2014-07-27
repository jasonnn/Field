package field.protect.asm.visitors;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by jason on 7/26/14.
 */
public abstract
class AbstractAnnotationVisitor extends AnnotationVisitor {
    public
    AbstractAnnotationVisitor(AnnotationVisitor av) {
        super(Opcodes.ASM5, av);
    }

}
