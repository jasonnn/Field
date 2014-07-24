package field.bytecode.protect.instrumentation2;

import field.protect.asm.FieldASMAdviceAdapter;

/**
 * Created by jason on 7/23/14.
 */
public abstract
class AbstractMethodInstrumentation extends FieldASMAdviceAdapter {
    protected final ASMAnnotatedMethodCtx ctx;

//    public
//    AbstractMethodInstrumentation(MethodVisitor mv, int access, String name, String desc) {
//        super(Opcodes.ASM5, mv, access, name, desc);
//    }

    public
    AbstractMethodInstrumentation(ASMAnnotatedMethodCtx ctx) {
        super(ASM5, ctx.delegate, ctx.access, ctx.name, ctx.desc);
        this.ctx = ctx;
    }
}
