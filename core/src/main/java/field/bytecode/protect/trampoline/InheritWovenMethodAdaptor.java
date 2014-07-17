package field.bytecode.protect.trampoline;

import field.bytecode.protect.asm.ASMMethod;
import field.bytecode.protect.asm.CommonTypes;
import field.bytecode.protect.asm.EmptyVisitors;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by jason on 7/14/14.
 */
public class InheritWovenMethodAdaptor extends MethodVisitor {

    private final StandardTrampoline trampoline;

    //private final int access;

    private final String desc;

    private final String[] interfaces;

    private final String name;

    private final String super_name;

    public InheritWovenMethodAdaptor(StandardTrampoline trampoline,
                                     String name, String desc,
                                     String super_name, String[] interfaces) {
        super(Opcodes.ASM5);
        this.trampoline = trampoline;
        this.name = name;
        this.desc = desc;
        this.super_name = super_name;
        this.interfaces = interfaces;
    }

    public InheritWovenMethodAdaptor setDelegate(MethodVisitor mv) {
        this.mv = mv;
        return this;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String annotationName, boolean vis) {
        if (!CommonTypes.INHERIT_WEAVE.getDescriptor().equals(annotationName)) {
            return super.visitAnnotation(annotationName, vis);
        }
//        if (!annotationName.equals("Lfield/bytecode/protect/annotations/InheritWeave;")) {
//            return super.visitAnnotation(annotationName, vis);
//        }
        try {

            Type[] at = new ASMMethod(name, desc).getArgumentTypes();
            Annotation[] annotations = trampoline.getAllAnotationsForSuperMethodsOf(name, desc, at, super_name, interfaces);

            for (Annotation annotation : annotations) {
                AnnotationVisitor va = mv.visitAnnotation("L" + annotation.annotationType().getName().replace('.', '/') + ";", true);

                Method[] annotationMethods = annotation.annotationType().getDeclaredMethods();

                if (annotation.annotationType().getClassLoader() != trampoline.loader)
                    assert !trampoline.shouldLoadLocal(annotation.annotationType().getName()) : "WARNING: leaked, " + annotation.annotationType().getClassLoader() + " " + annotation.annotationType();

                for (Method mm : annotationMethods) {
                    try {
                        Object r;
                        r = mm.invoke(annotation);
                        if (r instanceof Class)
                            r = Type.getType((Class) r);
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

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return EmptyVisitors.annotationVisitor;
    }

}
