package field.bytecode.protect.trampoline;

import org.objectweb.asm.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jason on 7/14/14.
 */
public class AnnotationMethodAdaptor extends MethodVisitor {

    private final Map<String, HandlesAnnontatedMethod> annotatedMethodHandlers;
    private final int access;

    private final String class_name;

    private final ClassVisitor cv;

    private final String desc;

    private final String name;

    private final byte[] originalByteCode;

    private final String signature;

    private final String super_name;

    public AnnotationMethodAdaptor(Map<String, HandlesAnnontatedMethod> annotatedMethodHandlers, int access, String name, String desc, String signature, ClassVisitor classDelegate, MethodVisitor arg0, String super_name, byte[] originalByteCode, String class_name) {
        super(Opcodes.ASM5);
        this.annotatedMethodHandlers = annotatedMethodHandlers;

        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.cv = classDelegate;
        this.super_name = super_name;
        this.originalByteCode = originalByteCode;
        this.class_name = class_name;
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    public void setDelegate(MethodVisitor mv) {
        this.mv = mv;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String annotationName, boolean vis) {

        if (annotatedMethodHandlers.containsKey(annotationName)) {
            final AnnotationVisitor av = super.visitAnnotation(annotationName, vis);
            final HashMap<String, Object> parameters = new HashMap<String, Object>();

            return new AnnotationVisitor(Opcodes.ASM5) {

                public void visit(String arg0, Object arg1) {
                    parameters.put(arg0, arg1);
                    av.visit(arg0, arg1);
                }

                public AnnotationVisitor visitAnnotation(String arg0, String arg1) {
                    return av.visitAnnotation(arg0, arg1);
                }

                public AnnotationVisitor visitArray(String arg0) {
                    return av.visitArray(arg0);
                }

                public void visitEnd() {
                    av.visitEnd();
                    MethodVisitor m = annotatedMethodHandlers.get(annotationName).handleEnd(access, name, desc, signature, cv, mv, parameters, originalByteCode, class_name);
                    if (m != null)
                        mv = m;
                }

                public void visitEnum(String arg0, String arg1, String arg2) {
                    av.visitEnum(arg0, arg1, arg2);
                }

            };
        } else {
            return super.visitAnnotation(annotationName, vis);
        }
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        return super.visitParameterAnnotation(parameter, desc, visible);
    }

    @Override
    public void visitLineNumber(int arg0, Label arg1) {
        // ;//System.out.println(" line number <"+arg0+"> <"+arg1+">");
        super.visitLineNumber(arg0, arg1);
    }

}
