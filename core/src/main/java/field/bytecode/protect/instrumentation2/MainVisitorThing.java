package field.bytecode.protect.instrumentation2;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jason on 7/21/14.
 */
public
class MainVisitorThing extends ClassVisitor implements AnnotationHandlerProvider {
    final ASMMethodCtx methodCtx = new ASMMethodCtx();
    final ASMClassCtx classCtx = methodCtx.classCtx;

    final Map<String, AnnotatedMethodHandler2> handlers;

    final List<VisitEndCallback> callbacks = new ArrayList<VisitEndCallback>();
    final List<PostConstructCallback> postConstructCallbacks = new ArrayList<PostConstructCallback>();

    public
    void callBeforeEnd(VisitEndCallback cb) {
        callbacks.add(cb);
    }

    public
    void callOnPostConstruct(PostConstructCallback callback) {
        postConstructCallbacks.add(callback);
    }


    @Override
    public
    boolean canHandle(String desc) {
        return handlers.containsKey(desc);
    }

    @Override
    public
    AnnotatedMethodHandler2 getHandler(String desc) {
        return handlers.get(desc);
    }

    public
    MainVisitorThing(ClassVisitor cv) {
        this(cv, StandardMethodAnnotationHandlers.getHandlers());
    }


    public
    MainVisitorThing(ClassVisitor cv, Map<String, AnnotatedMethodHandler2> handlers) {
        super(Opcodes.ASM5, cv);
        this.handlers = handlers;
    }

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

    @Override
    public
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        methodCtx.access = access;
        methodCtx.name = name;
        methodCtx.desc = desc;
        methodCtx.signature = signature;
        methodCtx.exceptions = exceptions;

        return createMethodAdapter();

    }


    AnnotatedMethodAdapter2 createMethodAdapter() {
        ASMAnnotatedMethodCtx asmMethodCtx = ASMAnnotatedMethodCtx.from(methodCtx);
        MethodVisitor delegate = super.visitMethod(asmMethodCtx.access,
                                                   asmMethodCtx.name,
                                                   asmMethodCtx.desc,
                                                   asmMethodCtx.signature,
                                                   asmMethodCtx.exceptions);
        return new AnnotatedMethodAdapter2(delegate, this, asmMethodCtx);
    }

    @Override
    public
    void visitEnd() {
        for (VisitEndCallback callback : callbacks) {
            callback.apply(this);
        }
        super.visitEnd();
    }

    static
    class AnnotatedMethodAdapter2 extends MethodVisitor {
        final AnnotationHandlerProvider handlers;
        final ASMAnnotatedMethodCtx methodCtx;

        public
        AnnotatedMethodAdapter2(MethodVisitor mv, AnnotationHandlerProvider handlers, ASMAnnotatedMethodCtx methodCtx) {
            super(Opcodes.ASM5, mv);
            this.handlers = handlers;
            this.methodCtx = methodCtx;
            this.methodCtx.delegate = mv;
        }

        @Override
        public
        AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
            final AnnotationVisitor delegate = super.visitAnnotation(desc, visible);
            if (handlers.canHandle(desc)) {
                methodCtx.params.clear();

                return new AnnotationVisitor(Opcodes.ASM5, delegate) {
                    @Override
                    public
                    void visit(String name, Object value) {
                        methodCtx.params.put(name, value);
                        super.visit(name, value);
                    }

                    @Override
                    public
                    void visitEnd() {
                        super.visitEnd();
                        mv = handlers.getHandler(desc).handleMethod(methodCtx);
                    }
                };

            }
            return delegate;
        }

    }

}
