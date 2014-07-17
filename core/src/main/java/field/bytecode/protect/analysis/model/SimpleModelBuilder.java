package field.bytecode.protect.analysis.model;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jason on 7/16/14.
 */
public class SimpleModelBuilder {

    public static SimpleClassModel buildModel(byte[] bytes) {
        SimpleModelClassVisitor modelBuilder = new SimpleModelClassVisitor(null);
        new ClassReader(bytes).accept(modelBuilder, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return modelBuilder.getModel();
    }

    static abstract class AbstractModelSpec<T extends AbstractSimpleModel> {
        public int access;

        public String name;

        public String signature;

        public final Set<String> annotations = new HashSet<String>();

        public abstract T build();

    }

    static class FieldModelSpec extends AbstractModelSpec<SimpleFieldModel> {
        public String desc;
        public Object value;

        @Override
        public SimpleFieldModel build() {
            return new SimpleFieldModel(access, name, signature, desc, value, annotations);
        }

    }

    static class MethodModelSpec extends AbstractModelSpec<SimpleMethodModel> {
        public String desc;
        public String[] exceptions;

        public SimpleMethodModel build() {
            return new SimpleMethodModel(access, name, signature, annotations, desc, exceptions);
        }
    }

    static class ClassModelSpec extends AbstractModelSpec<SimpleClassModel> {
        final Set<FieldModelSpec> fields = new HashSet<FieldModelSpec>();
        final Set<MethodModelSpec> methods = new HashSet<MethodModelSpec>();
        public String superName;
        public String[] interfaces;

        private static <B extends AbstractSimpleModel, S extends AbstractModelSpec<B>>
        Set<B> buildSet(Set<S> specs) {
            if (specs.isEmpty()) return Collections.emptySet();
            Set<B> built = new HashSet<B>(specs.size());
            for (S spec : specs) {
                built.add(spec.build());
            }
            return built;
        }


        public SimpleClassModel build() {
            return new SimpleClassModel(access,
                    name,
                    signature,
                    superName,
                    interfaces,
                    annotations,
                    buildSet(fields),
                    buildSet(methods));
        }
    }


    static class SimpleModelClassVisitor extends ClassVisitor {
        ClassModelSpec clsModel;

        public SimpleClassModel getModel() {
            return clsModel.build();
        }

        private static <T extends AbstractSimpleModel> Set<String> doAdd(AbstractModelSpec<T> spec, int access, String name, String signature) {
            spec.access = access;
            spec.name = name;
            spec.signature = signature;
            return spec.annotations;
        }

        Set<String> addField(int access, @NotNull String name, @NotNull String signature, String desc, Object value) {
            FieldModelSpec fieldSpec = new FieldModelSpec();
            fieldSpec.desc = desc;
            fieldSpec.value = value;
            clsModel.fields.add(fieldSpec);
            return doAdd(fieldSpec, access, name, signature);

        }

        Set<String> addMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodModelSpec methodSpec = new MethodModelSpec();
            methodSpec.desc = desc;
            methodSpec.exceptions = exceptions;
            clsModel.methods.add(methodSpec);
            return doAdd(methodSpec, access, name, signature);

        }

        public SimpleModelClassVisitor(ClassVisitor cv) {
            super(Opcodes.ASM5, cv);
        }


        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            clsModel = new ClassModelSpec();
            clsModel.access = access;
            clsModel.name = name;
            clsModel.signature = signature;
            clsModel.superName = superName;
            clsModel.interfaces = interfaces;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            clsModel.annotations.add(desc);
            return super.visitAnnotation(desc, visible);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return new SimpleModelFieldVisitor(addField(access, name, desc, signature, value),
                    super.visitField(access, name, desc, signature, value));

        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv;
            if (name.equals("<init>") || name.equals("<clinit>")) {
                mv = super.visitMethod(access, name, desc, signature, exceptions);
            } else {
                mv = new SimpleModelMethodVisitor(addMethod(access, name, desc, signature, exceptions),
                        super.visitMethod(access, name, desc, signature, exceptions));
            }

            return mv;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
        }
    }


    static class SimpleModelMethodVisitor extends MethodVisitor {
        final Set<String> annotationCollector;

        public SimpleModelMethodVisitor(Set<String> annotationCollector, MethodVisitor mv) {
            super(Opcodes.ASM5, mv);
            this.annotationCollector = annotationCollector;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            annotationCollector.add(desc);
            return super.visitAnnotation(desc, visible);
        }
    }

    static class SimpleModelFieldVisitor extends FieldVisitor {
        final Set<String> annotationCollector;

        public SimpleModelFieldVisitor(Set<String> annotationCollector, FieldVisitor fv) {
            super(Opcodes.ASM5, fv);
            this.annotationCollector = annotationCollector;

        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            annotationCollector.add(desc);
            return super.visitAnnotation(desc, visible);
        }
    }

}
