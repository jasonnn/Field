package asm.handlers.ctx;

import com.google.common.base.Predicate;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;

/**
 * Created by jason on 7/26/14.
 */
public abstract
class InitialCtxTrackingVisitor extends ClassVisitor {
    public
    InitialCtxTrackingVisitor(ClassVisitor cv,
                              Predicate<ASMClassCtx> collectMethodsPredicate,
                              Predicate<ASMMethodCtx> collectMethodPredicate) {
        super(Opcodes.ASM5, cv);
        this.collectMethodsPredicate = collectMethodsPredicate;
        this.collectMethodPredicate = collectMethodPredicate;
    }

    ASMClassCtx classCtx = new ASMClassCtx();

    private final Predicate<ASMClassCtx> collectMethodsPredicate;
    private final Predicate<ASMMethodCtx> collectMethodPredicate;

    @Override
    public
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        classCtx.access = access;
        classCtx.name = name;
        classCtx.signature = signature;
        classCtx.superName = superName;
        classCtx.interfaces = interfaces;
        classCtx.cv = this;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    boolean methodVisited = false;
    boolean shouldVisitMethods = false;

    @Override
    public
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (!methodVisited) {
            shouldVisitMethods = collectMethodsPredicate.apply(classCtx);
        }
        if (shouldVisitMethods) {
            ASMMethodCtx methodCtx = classCtx.newMethod(access, name, desc, signature, exceptions);
            return new MethodCtxVisitor(super.visitMethod(access, name, desc, signature, exceptions),
                                        methodCtx,
                                        collectMethodPredicate);

        }
        else {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

    }

    @Override
    public
    AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
        return new AnnotationVisitor(Opcodes.ASM5, super.visitAnnotation(desc, visible)) {
            Map<String, Object> params = classCtx.annotation(desc);

            @Override
            public
            void visit(String name, Object value) {
                params.put(name, value);
                super.visit(name, value);
            }
        };
    }

    static
    class MethodCtxVisitor extends MethodVisitor {
        private final ASMMethodCtx ctx;
        private final Predicate<ASMMethodCtx> ctxPredicate;

        public
        MethodCtxVisitor(MethodVisitor mv, ASMMethodCtx ctx, Predicate<ASMMethodCtx> ctxPredicate) {
            super(Opcodes.ASM5, mv);
            this.ctx = ctx;
            ctx.mv=this;
            this.ctxPredicate = ctxPredicate;
        }

        @Override
        public
        AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
            return new AnnotationVisitor(Opcodes.ASM5, super.visitAnnotation(desc, visible)) {
                Map<String, Object> params = ctx.annotation(desc);

                @Override
                public
                void visit(String name, Object value) {
                    params.put(name, value);
                    super.visit(name, value);
                }
            };
        }

        @Override
        public
        void visitCode() {
            if (ctxPredicate.apply(ctx)) {
                ctx.keep();
            }
            super.visitCode();
        }
    }
}
