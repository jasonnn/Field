package field.protect.asm.visitors.paramObj;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;

public
class AnnotationParams {
    private final String desc;
    private final boolean visible;

    public
    AnnotationParams(String desc, boolean visible) {
        this.desc = desc;
        this.visible = visible;
    }
    public
    AnnotationVisitor callVisitMethod(ClassVisitor cv){
        if(cv==null) return null;
        return cv.visitAnnotation(desc,visible);
    }

    public
    String getDesc() {
        return desc;
    }

    public
    boolean isVisible() {
        return visible;
    }
}
