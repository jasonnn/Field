package field.bytecode.protect.instrumentation;

import field.bytecode.protect.instrumentation2.AnnotatedMethodHandler2;
import field.bytecode.protect.instrumentation2.MainVisitorThing;
import org.objectweb.asm.ClassVisitor;

import java.util.Map;

/**
 * Created by jason on 7/22/14.
 */
public
class TestingMainVisitorThing extends MainVisitorThing {
    public
    TestingMainVisitorThing(ClassVisitor cv, Map<String, AnnotatedMethodHandler2> handlers) {
        super(cv, handlers);
    }

    public boolean wasCalled = false;

    @Override
    public
    AnnotatedMethodHandler2 getHandler(String desc) {
        wasCalled = true;
        return super.getHandler(desc);
    }
}
