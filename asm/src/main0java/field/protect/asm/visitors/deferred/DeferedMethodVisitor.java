package field.protect.asm.visitors.deferred;

import field.protect.asm.visitors.paramObj.MethodParams;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Created by jason on 7/26/14.
 */
public
class DeferedMethodVisitor {
    private final
    MethodParams params;
    private final
    ClassVisitor delegate;


    public
    DeferedMethodVisitor(MethodParams params, ClassVisitor delegate) {
        this.params = params;
        this.delegate = delegate;
    }
    public MethodVisitor create(){
       return params.callVisitMethod(delegate);
    }
}
