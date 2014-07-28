package field.protect.asm.visitors.multi;

import field.protect.asm.visitors.AbstractFieldVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;

/**
 * Created by jason on 7/28/14.
 */
public
class FieldMultiVisitor extends AbstractFieldVisitor {

    private final FieldVisitor[] visitors;

    public
    FieldMultiVisitor(FieldVisitor... visitors) {
        super(null);
        this.visitors = visitors;
    }

    @Override
    public
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor[] annotationVisitors = new AnnotationVisitor[visitors.length];
        for (int i = 0; i < visitors.length; i++) {
            annotationVisitors[i] = visitors[i].visitAnnotation(desc, visible);
        }
        return new AnnotationMultiVisitor(annotationVisitors);
    }

    @Override
    public
    AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        AnnotationVisitor[] annotationVisitors = new AnnotationVisitor[visitors.length];
        for (int i = 0; i < visitors.length; i++) {
            annotationVisitors[i] = visitors[i].visitTypeAnnotation(typeRef, typePath, desc, visible);
        }
        return new AnnotationMultiVisitor(annotationVisitors);
    }

    @Override
    public
    void visitAttribute(Attribute attr) {
        for (FieldVisitor visitor : visitors) {
            visitor.visitAttribute(attr);
        }
    }

    @Override
    public
    void visitEnd() {
        for (FieldVisitor visitor : visitors) {
            visitor.visitEnd();
        }
    }
}
