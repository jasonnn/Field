package field.protect.asm.visitors.paramObj;

import field.protect.asm.visitors.AbstractClassVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Created by jason on 7/26/14.
 */
public abstract
class ParamClassVisitor extends AbstractClassVisitor {
    public
    ParamClassVisitor(ClassVisitor cv) {
        super(cv);
    }

    @Override
    public
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        beginClass(new ClassParams(version, access, name, signature, superName, interfaces));
    }

    void beginClass(ClassParams classParams) {
        classParams.callVisit(cv);
    }

    @Override
    public
    FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return beginField(new FieldParams(access, name, desc, signature, value));
    }


    FieldVisitor beginField(FieldParams fieldParams) {
        return fieldParams.callVisitField(cv);
    }

    @Override
    public
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return beginMethod(new MethodParams(access, name, desc, signature, exceptions));
    }

    MethodVisitor beginMethod(MethodParams methodParams) {
        return methodParams.callVisitMethod(cv);

    }
}
