package field.bytecode.protect.asm;

import org.objectweb.asm.*;

/**
 * Created by jason on 7/14/14.
 */
public class EmptyVisitors {
    public static final AnnotationVisitor annotationVisitor = new AnnotationVisitor(Opcodes.ASM5) {

        @Override
        public AnnotationVisitor visitAnnotation(String name, String desc) {
            //TODO will this cause infinite recursion?
            return this;
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            return this;
        }
    };
    public static final FieldVisitor fieldVisitor = new FieldVisitor(Opcodes.ASM5) {

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return annotationVisitor;
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            return annotationVisitor;
        }
    };
    public static final MethodVisitor methodVisitor = new MethodVisitor(Opcodes.ASM5) {
        public AnnotationVisitor visitAnnotationDefault() {
            return annotationVisitor;
        }

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return annotationVisitor;
        }

        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            return annotationVisitor;
        }

        public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
            return annotationVisitor;
        }

        public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            return annotationVisitor;
        }

        public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            return annotationVisitor;
        }

        public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
            return annotationVisitor;
        }
    };
    public static final ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5) {
        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return annotationVisitor;
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
            return annotationVisitor;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return fieldVisitor;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return methodVisitor;
        }
    };
}
