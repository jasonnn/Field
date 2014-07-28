package field.protect.asm.visitors.multi;

import field.protect.asm.visitors.AbstractAnnotationVisitor;
import org.objectweb.asm.AnnotationVisitor;

/**
 * Created by jason on 7/28/14.
 */
public
class AnnotationMultiVisitor extends AbstractAnnotationVisitor {
    private final AnnotationVisitor[] visitors;

    public
    AnnotationMultiVisitor(AnnotationVisitor... visitors) {
        super(null);
        this.visitors = visitors;
    }


    @Override
    public
    void visit(String name, Object value) {
        for (AnnotationVisitor visitor : visitors) {
            visitor.visit(name, value);
        }
    }

    @Override
    public
    void visitEnum(String name, String desc, String value) {
        for (AnnotationVisitor visitor : visitors) {
            visitor.visitEnum(name, desc, value);
        }
    }

    @Override
    public
    AnnotationVisitor visitAnnotation(String name, String desc) {
        AnnotationVisitor[] newVisitors = new AnnotationVisitor[visitors.length];
        for (int i = 0; i < newVisitors.length; i++) {
            newVisitors[i] = visitors[i].visitAnnotation(name, desc);
        }
        return new AnnotationMultiVisitor(newVisitors);
    }

    @Override
    public
    AnnotationVisitor visitArray(String name) {
        AnnotationVisitor[] newVisitors = new AnnotationVisitor[visitors.length];
        for (int i = 0; i < newVisitors.length; i++) {
            newVisitors[i] = visitors[i].visitArray(name);
        }
        return new AnnotationMultiVisitor(newVisitors);
    }

    @Override
    public
    void visitEnd() {
        for (AnnotationVisitor visitor : visitors) {
            visitor.visitEnd();
        }
    }
}
