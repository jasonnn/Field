package field.bytecode.protect.instrumentation2;

import org.objectweb.asm.ClassVisitor;

/**
 * Created by jason on 7/23/14.
 */
public
interface VisitEndCallback {
    void apply(ClassVisitor cv);
}
