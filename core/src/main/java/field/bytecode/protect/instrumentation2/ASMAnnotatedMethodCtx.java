package field.bytecode.protect.instrumentation2;

import org.objectweb.asm.MethodVisitor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by jason on 7/21/14.
 */
public class ASMAnnotatedMethodCtx extends ASMMethodCtx {
    public final Map<String, Object> params = new LinkedHashMap<String, Object>(4);
    public MethodVisitor delegate;

    public static ASMAnnotatedMethodCtx from(ASMMethodCtx copyFrom) {
        ASMAnnotatedMethodCtx copied = new ASMAnnotatedMethodCtx();
        copyFrom.copyTo(copied);
        return copied;
    }
}
