package field.apt.util;

import com.squareup.javawriter.JavaWriter;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jason on 4/19/14.
 */
public abstract
class ExtendableJavaWriter<W extends ExtendableJavaWriter<W>> extends JavaWriter {

    public static class Impl extends ExtendableJavaWriter<Impl>{

        public
        Impl(Writer out) {
            super(out);
        }

        @Override
        protected
        Impl self() {
            return this;
        }
    }

    public
    ExtendableJavaWriter(Writer out) {
        super(out);
    }

    protected abstract
    W self();

    @Override
    public
    W emitPackage(String packageName) throws IOException {
        super.emitPackage(packageName);
        return self();
    }

    @Override
    public
    W emitImports(String... types) throws IOException {
        super.emitImports(types);
        return self();
    }

    @Override
    public
    W emitImports(Class<?>... types) throws IOException {
        super.emitImports(types);
        return self();
    }

    @Override
    public
    W emitImports(Collection<String> types) throws IOException {
        super.emitImports(types);
        return self();
    }

    @Override
    public
    W emitStaticImports(String... types) throws IOException {
        super.emitStaticImports(types);
        return self();
    }

    @Override
    public
    W emitStaticImports(Collection<String> types) throws IOException {
        super.emitStaticImports(types);
        return self();
    }



    @Override
    public
    W beginInitializer(boolean isStatic) throws IOException {
        super.beginInitializer(isStatic);
        return self();
    }

    @Override
    public
    W endInitializer() throws IOException {
        super.endInitializer();
        return self();
    }

    @Override
    public
    W beginType(String type, String kind) throws IOException {
        super.beginType(type, kind);
        return self();
    }

    @Override
    public
    W beginType(String type, String kind, Set<Modifier> modifiers) throws IOException {
        super.beginType(type, kind, modifiers);
        return self();
    }

    @Override
    public
    W beginType(String type,
                         String kind,
                         Set<Modifier> modifiers,
                         String extendsType,
                         String... implementsTypes) throws IOException {
        super.beginType(type, kind, modifiers, extendsType, implementsTypes);
        return self();
    }

    @Override
    public
    W endType() throws IOException {
        super.endType();
        return self();
    }

    @Override
    public
    W emitField(String type, String name) throws IOException {
        super.emitField(type, name);
        return self();
    }

    @Override
    public
    W emitField(String type, String name, Set<Modifier> modifiers) throws IOException {
        super.emitField(type, name, modifiers);
        return self();
    }

    @Override
    public
    W emitField(String type, String name, Set<Modifier> modifiers, String initialValue) throws IOException {
        super.emitField(type, name, modifiers, initialValue);
        return self();
    }

    @Override
    public
    W beginMethod(String Type, String name, Set<Modifier> modifiers, String... parameters)
            throws IOException {
        super.beginMethod(Type, name, modifiers, parameters);
        return self();
    }

    @Override
    public
    W beginMethod(String Type,
                           String name,
                           Set<Modifier> modifiers,
                           List<String> parameters,
                           List<String> throwsTypes) throws IOException {
        super.beginMethod(Type, name, modifiers, parameters, throwsTypes);
        return self();
    }

    @Override
    public
    W beginConstructor(Set<Modifier> modifiers, String... parameters) throws IOException {
        super.beginConstructor(modifiers, parameters);
        return self();
    }

    @Override
    public
    W beginConstructor(Set<Modifier> modifiers, List<String> parameters, List<String> throwsTypes)
            throws IOException {
        super.beginConstructor(modifiers, parameters, throwsTypes);
        return self();
    }

    @Override
    public
    W emitJavadoc(String javadoc, Object... params) throws IOException {
        super.emitJavadoc(javadoc, params);
        return self();
    }

    @Override
    public
    W emitSingleLineComment(String comment, Object... args) throws IOException {
        super.emitSingleLineComment(comment, args);
        return self();
    }

    @Override
    public
    W emitEmptyLine() throws IOException {
        super.emitEmptyLine();
        return self();
    }

    @Override
    public
    W emitEnumValue(String name) throws IOException {
        super.emitEnumValue(name);
        return self();
    }

    @Override
    public
    W emitEnumValue(String name, boolean isLast) throws IOException {
        super.emitEnumValue(name, isLast);
        return self();
    }

    @Override
    public
    W emitEnumValues(Iterable<String> names) throws IOException {
        super.emitEnumValues(names);
        return self();
    }

    @Override
    public
    W emitAnnotation(String annotation) throws IOException {
        super.emitAnnotation(annotation);
        return self();
    }

    @Override
    public
    W emitAnnotation(Class<? extends Annotation> annotationType) throws IOException {
        super.emitAnnotation(annotationType);
        return self();
    }

    @Override
    public
    W emitAnnotation(Class<? extends Annotation> annotationType, Object value) throws IOException {
        super.emitAnnotation(annotationType, value);
        return self();
    }

    @Override
    public
    W emitAnnotation(String annotation, Object value) throws IOException {
        super.emitAnnotation(annotation, value);
        return self();
    }

    @Override
    public
    W emitAnnotation(Class<? extends Annotation> annotationType, Map<String, ?> attributes)
            throws IOException {
        super.emitAnnotation(annotationType, attributes);
        return self();
    }

    @Override
    public
    W emitAnnotation(String annotation, Map<String, ?> attributes) throws IOException {
        super.emitAnnotation(annotation, attributes);
        return self();
    }

    @Override
    public
    W emitStatement(String pattern, Object... args) throws IOException {
        super.emitStatement(pattern, args);
        return self();
    }

    @Override
    public
    W beginControlFlow(String controlFlow, Object... args) throws IOException {
        super.beginControlFlow(controlFlow, args);
        return self();
    }

    @Override
    public
    W nextControlFlow(String controlFlow, Object... args) throws IOException {
        super.nextControlFlow(controlFlow, args);
        return self();
    }

    @Override
    public
    W endControlFlow() throws IOException {
        super.endControlFlow();
        return self();
    }

    @Override
    public
    W endControlFlow(String controlFlow, Object... args) throws IOException {
        super.endControlFlow(controlFlow, args);
        return self();
    }

    @Override
    public
    W endMethod() throws IOException {
        super.endMethod();
        return self();
    }

    @Override
    public
    W endConstructor() throws IOException {
        super.endConstructor();
        return self();
    }
}
