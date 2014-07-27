package field.protect.asm.visitors.deferred;

import field.protect.asm.visitors.paramObj.ParamClassVisitor;
import org.objectweb.asm.ClassVisitor;

/**
 * Created by jason on 7/26/14.
 */
public
class DeferedClassVisitor extends ParamClassVisitor{
    public
    DeferedClassVisitor(ClassVisitor cv) {
        super(cv);
    }
}
