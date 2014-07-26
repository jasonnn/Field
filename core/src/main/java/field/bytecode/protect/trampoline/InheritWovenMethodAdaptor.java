package field.bytecode.protect.trampoline;

import field.protect.asm.ASMMethod;
import field.protect.asm.ASMType;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * add annotations from parent classes to the class being instrumented
 */
public
class InheritWovenMethodAdaptor extends MethodVisitor {

    private final InheritWovenHelper trampoline;

    //private final int access;

    private final String desc;

    private final String[] interfaces;

    private final String name;

    private final String super_name;

    public
    InheritWovenMethodAdaptor(InheritWovenHelper trampoline,
                              String name,
                              String desc,
                              String super_name,
                              String[] interfaces) {
        super(Opcodes.ASM5);
        this.trampoline = trampoline;
        this.name = name;
        this.desc = desc;
        this.super_name = super_name;
        this.interfaces = interfaces;
    }

    public
    InheritWovenMethodAdaptor setDelegate(MethodVisitor mv) {
        this.mv = mv;
        return this;
    }

    @Override
    public
    AnnotationVisitor visitAnnotation(final String annotationName, boolean vis) {


        if (!"Lfield/bytecode/protect/annotations/InheritWeave;".equals(annotationName)) {
            return super.visitAnnotation(annotationName, vis);
        }

        try {

            ASMType[] at = new ASMMethod(name, desc).getASMArgumentTypes();

            Annotation[] annotations =
                    trampoline.getAllAnotationsForSuperMethodsOf(name, desc, at, super_name, interfaces);

            for (Annotation annotation : annotations) {
                AnnotationVisitor va =
                        mv.visitAnnotation('L' + annotation.annotationType().getName().replace('.', '/') + ';', true);

                Method[] annotationMethods = annotation.annotationType().getDeclaredMethods();

                if (annotation.annotationType().getClassLoader() != trampoline.getLoader())
                    assert !trampoline.shouldLoadLocal(annotation.annotationType().getName()) : "WARNING: leaked, "
                                                                                                + annotation.annotationType()
                                                                                                            .getClassLoader()
                                                                                                + ' '
                                                                                                + annotation.annotationType();

                for (Method mm : annotationMethods) {
                    try {
                        Object r;
                        r = mm.invoke(annotation);
                        if (r instanceof Class) r = Type.getType((Class) r);
                        va.visit(mm.getName(), r);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

                va.visitEnd();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;////?????? THIS DOES NOT SEEM RIGHT???!
    }

}
