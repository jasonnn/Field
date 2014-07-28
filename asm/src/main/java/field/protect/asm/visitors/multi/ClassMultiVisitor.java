package field.protect.asm.visitors.multi;

import field.protect.asm.visitors.AbstractClassVisitor;
import org.objectweb.asm.*;

/**
 * Created by jason on 7/28/14.
 */
public
class ClassMultiVisitor extends AbstractClassVisitor {
    private final ClassVisitor[] visitors;

    public
    ClassMultiVisitor(ClassVisitor... visitors) {
        super(null);
        this.visitors = visitors;
    }

    @Override
    public
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        for (ClassVisitor visitor : visitors) {
            visitor.visit(version, access, name, signature, superName, interfaces);
        }
    }

    @Override
    public
    void visitSource(String source, String debug) {
        for (ClassVisitor visitor : visitors) {
            visitor.visitSource(source, debug);
        }
    }

    @Override
    public
    void visitOuterClass(String owner, String name, String desc) {
        for (ClassVisitor visitor : visitors) {
            visitor.visitOuterClass(owner, name, desc);
        }
    }

    @Override
    public
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor[] annotationVisitors = new AnnotationVisitor[visitors.length];
        for (int i = 0; i < annotationVisitors.length; i++) {
            annotationVisitors[i] = visitors[i].visitAnnotation(desc, visible);
        }
        return new AnnotationMultiVisitor(annotationVisitors);
    }

    @Override
    public
    AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        AnnotationVisitor[] annotationVisitors = new AnnotationVisitor[visitors.length];
        for (int i = 0; i < annotationVisitors.length; i++) {
            annotationVisitors[i] = visitors[i].visitTypeAnnotation(typeRef, typePath, desc, visible);
        }
        return new AnnotationMultiVisitor(annotationVisitors);
    }

    @Override
    public
    void visitAttribute(Attribute attr) {
        for (ClassVisitor visitor : visitors) {
            visitor.visitAttribute(attr);
        }
    }

    @Override
    public
    void visitInnerClass(String name, String outerName, String innerName, int access) {
        for (ClassVisitor visitor : visitors) {
            visitor.visitInnerClass(name, outerName, innerName, access);
        }
    }

    @Override
    public
    FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldVisitor[] fieldVisitors = new FieldVisitor[visitors.length];
        for (int i = 0; i < visitors.length; i++) {
            fieldVisitors[i] = visitors[i].visitField(access, name, desc, signature, value);
        }
        return new FieldMultiVisitor(fieldVisitors);
    }

    @Override
    public
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public
    void visitEnd() {
        for (ClassVisitor visitor : visitors) {
            visitor.visitEnd();
        }
    }
}
