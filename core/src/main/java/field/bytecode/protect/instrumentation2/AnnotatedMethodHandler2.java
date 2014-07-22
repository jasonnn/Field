package field.bytecode.protect.instrumentation2;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.MethodVisitor;

/**
 * Created by jason on 7/21/14.
 */
public interface AnnotatedMethodHandler2 {

    @NotNull
    MethodVisitor handleMethod(ASMAnnotatedMethodCtx ctx);
}
