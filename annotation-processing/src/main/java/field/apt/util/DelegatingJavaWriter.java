package field.apt.util;

import com.squareup.javawriter.JavaWriter;

import javax.lang.model.element.Modifier;
import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jason on 7/10/14.
 */
@SuppressWarnings({"JavaDoc", "UnusedDeclaration"})
public class DelegatingJavaWriter implements Closeable {
    protected final JavaWriter javaWriter;

    protected DelegatingJavaWriter(JavaWriter javaWriter) {
        this.javaWriter = javaWriter;
    }

    protected void setCompressingTypes(boolean isCompressingTypes) {
        javaWriter.setCompressingTypes(isCompressingTypes);
    }

    protected JavaWriter beginConstructor(Set<Modifier> modifiers, String... parameters) throws IOException {
        return javaWriter.beginConstructor(modifiers, parameters);
    }

    protected JavaWriter beginConstructor(Set<Modifier> modifiers, List<String> parameters, List<String> throwsTypes) throws IOException {
        return javaWriter.beginConstructor(modifiers, parameters, throwsTypes);
    }

    /**
     * Emits some Javadoc comments with line separated by {@code \n}.
     *
     * @param javadoc
     * @param params
     */
    protected JavaWriter emitJavadoc(String javadoc, Object... params) throws IOException {
        return javaWriter.emitJavadoc(javadoc, params);
    }

    /**
     * @param pattern a code pattern like "int i = %s". Newlines will be further indented. Should not
     *                contain trailing semicolon.
     * @param args
     */
    protected JavaWriter emitStatement(String pattern, Object... args) throws IOException {
        return javaWriter.emitStatement(pattern, args);
    }

    /**
     * Emit a list of enum values followed by a semi-colon ({@code ;}).
     *
     * @param names
     */
    protected JavaWriter emitEnumValues(Iterable<String> names) throws IOException {
        return javaWriter.emitEnumValues(names);
    }

    protected void setIndent(String indent) {
        javaWriter.setIndent(indent);
    }

    /**
     * Emit a static import for each {@code type} in the provided {@code Collection}. For the
     * duration of the file, all references to these classes will be automatically shortened.
     *
     * @param types
     */
    protected JavaWriter emitStaticImports(Collection<String> types) throws IOException {
        return javaWriter.emitStaticImports(types);
    }

    /**
     * Emits a type declaration.
     *
     * @param type
     * @param kind      such as "class", "interface" or "enum".
     * @param modifiers
     */
    protected JavaWriter beginType(String type, String kind, Set<Modifier> modifiers) throws IOException {
        return javaWriter.beginType(type, kind, modifiers);
    }

    /**
     * Emits a type declaration.
     *
     * @param type
     * @param kind            such as "class", "interface" or "enum".
     * @param modifiers
     * @param extendsType     the class to extend, or null for no extends clause.
     * @param implementsTypes
     */
    protected JavaWriter beginType(String type, String kind, Set<Modifier> modifiers, String extendsType, String... implementsTypes) throws IOException {
        return javaWriter.beginType(type, kind, modifiers, extendsType, implementsTypes);
    }

    /**
     * Emits a field declaration.
     *
     * @param type
     * @param name
     * @param modifiers
     */
    protected JavaWriter emitField(String type, String name, Set<Modifier> modifiers) throws IOException {
        return javaWriter.emitField(type, name, modifiers);
    }

    /**
     * Completes the current type declaration.
     */
    protected JavaWriter endType() throws IOException {
        return javaWriter.endType();
    }

    /**
     * Emit a method declaration.
     * <p/>
     * <p>A {@code null} return type may be used to indicate a constructor, but
     * {@link #beginConstructor(java.util.Set, String...)} should be preferred. This behavior may be removed in
     * a future release.
     *
     * @param returnType the method's return type, or null for constructors
     * @param name       the method name, or the fully qualified class name for constructors.
     * @param modifiers  the set of modifiers to be applied to the method
     * @param parameters alternating parameter types and names.
     */
    protected JavaWriter beginMethod(String returnType, String name, Set<Modifier> modifiers, String... parameters) throws IOException {
        return javaWriter.beginMethod(returnType, name, modifiers, parameters);
    }

    /**
     * @param controlFlow the optional control flow construct and its code, such as
     *                    "while(foo == 20)". Only used for "do/while" control flows.
     * @param args
     */
    protected JavaWriter endControlFlow(String controlFlow, Object... args) throws IOException {
        return javaWriter.endControlFlow(controlFlow, args);
    }

    protected JavaWriter emitEmptyLine() throws IOException {
        return javaWriter.emitEmptyLine();
    }

    /**
     * A simple switch to emit the proper enum depending if its last causing it to be terminated
     * by a semi-colon ({@code ;}).
     *
     * @param name
     * @param isLast
     */
    protected JavaWriter emitEnumValue(String name, boolean isLast) throws IOException {
        return javaWriter.emitEnumValue(name, isLast);
    }

    /**
     * Emits a type declaration.
     *
     * @param type
     * @param kind such as "class", "interface" or "enum".
     */
    protected JavaWriter beginType(String type, String kind) throws IOException {
        return javaWriter.beginType(type, kind);
    }

    protected JavaWriter emitEnumValue(String name) throws IOException {
        return javaWriter.emitEnumValue(name);
    }

    /**
     * Annotates the next element with {@code annotation} and a {@code value}.
     *
     * @param annotation
     * @param value      an object used as the default (value) parameter of the annotation. The value will
     *                   be encoded using Object.toString(); use {@link #stringLiteral} for String values. Object
     */
    protected JavaWriter emitAnnotation(String annotation, Object value) throws IOException {
        return javaWriter.emitAnnotation(annotation, value);
    }

    /**
     * Completes the current constructor declaration.
     */
    protected JavaWriter endConstructor() throws IOException {
        return javaWriter.endConstructor();
    }

    /**
     * Annotates the next element with {@code annotationType} and a {@code value}.
     *
     * @param annotationType
     * @param value          an object used as the default (value) parameter of the annotation. The value will
     *                       be encoded using Object.toString(); use {@link #stringLiteral} for String values. Object
     */
    protected JavaWriter emitAnnotation(Class<? extends Annotation> annotationType, Object value) throws IOException {
        return javaWriter.emitAnnotation(annotationType, value);
    }

    /**
     * Try to compress a fully-qualified class name to only the class name.
     *
     * @param type
     */
    protected String compressType(String type) {
        return javaWriter.compressType(type);
    }

    /**
     * Emit an import for each {@code type} provided. For the duration of the file, all references to
     * these classes will be automatically shortened.
     *
     * @param types
     */
    protected JavaWriter emitImports(Class<?>... types) throws IOException {
        return javaWriter.emitImports(types);
    }

    public void close() throws IOException {
        javaWriter.close();
    }

    /**
     * Build a string representation of a type and optionally its generic type arguments.
     *
     * @param raw
     * @param parameters
     */
    protected static String type(Class<?> raw, String... parameters) {
        return JavaWriter.type(raw, parameters);
    }

    protected JavaWriter endControlFlow() throws IOException {
        return javaWriter.endControlFlow();
    }

    /**
     * Returns the string literal representing {@code data}, including wrapping quotes.
     *
     * @param data
     */
    protected static String stringLiteral(String data) {
        return JavaWriter.stringLiteral(data);
    }

    /**
     * Equivalent to {@code annotation(annotationType.getName(), attributes)}.
     *
     * @param annotationType
     * @param attributes
     */
    protected JavaWriter emitAnnotation(Class<? extends Annotation> annotationType, Map<String, ?> attributes) throws IOException {
        return javaWriter.emitAnnotation(annotationType, attributes);
    }

    /**
     * @param controlFlow the control flow construct and its code, such as "if (foo == 5)". Shouldn't
     *                    contain braces or newline characters.
     * @param args
     */
    protected JavaWriter beginControlFlow(String controlFlow, Object... args) throws IOException {
        return javaWriter.beginControlFlow(controlFlow, args);
    }

    /**
     * Build a string representation of the raw type for a (optionally generic) type.
     *
     * @param type
     */
    protected static String rawType(String type) {
        return JavaWriter.rawType(type);
    }

    protected boolean isCompressingTypes() {
        return javaWriter.isCompressingTypes();
    }

    /**
     * Completes the current method declaration.
     */
    protected JavaWriter endMethod() throws IOException {
        return javaWriter.endMethod();
    }

    /**
     * @param controlFlow the control flow construct and its code, such as "else if (foo == 10)".
     *                    Shouldn't contain braces or newline characters.
     * @param args
     */
    protected JavaWriter nextControlFlow(String controlFlow, Object... args) throws IOException {
        return javaWriter.nextControlFlow(controlFlow, args);
    }

    /**
     * Emit a static import for each {@code type} provided. For the duration of the file,
     * all references to these classes will be automatically shortened.
     *
     * @param types
     */
    protected JavaWriter emitStaticImports(String... types) throws IOException {
        return javaWriter.emitStaticImports(types);
    }

    /**
     * Emit a method declaration.
     * <p/>
     * <p>A {@code null} return type may be used to indicate a constructor, but
     * {@link #beginConstructor(java.util.Set, java.util.List, java.util.List)} should be preferred. This behavior may be removed in
     * a future release.
     *
     * @param returnType  the method's return type, or null for constructors.
     * @param name        the method name, or the fully qualified class name for constructors.
     * @param modifiers   the set of modifiers to be applied to the method
     * @param parameters  alternating parameter types and names.
     * @param throwsTypes the classes to throw, or null for no throws clause.
     */
    protected JavaWriter beginMethod(String returnType, String name, Set<Modifier> modifiers, List<String> parameters, List<String> throwsTypes) throws IOException {
        return javaWriter.beginMethod(returnType, name, modifiers, parameters, throwsTypes);
    }

    /**
     * Equivalent to {@code annotation(annotation, emptyMap())}.
     *
     * @param annotation
     */
    protected JavaWriter emitAnnotation(String annotation) throws IOException {
        return javaWriter.emitAnnotation(annotation);
    }

    /**
     * Emits an initializer declaration.
     *
     * @param isStatic true if it should be an static initializer, false for an instance initializer.
     */
    protected JavaWriter beginInitializer(boolean isStatic) throws IOException {
        return javaWriter.beginInitializer(isStatic);
    }

    /**
     * Emits a field declaration.
     *
     * @param type
     * @param name
     * @param modifiers
     * @param initialValue
     */
    protected JavaWriter emitField(String type, String name, Set<Modifier> modifiers, String initialValue) throws IOException {
        return javaWriter.emitField(type, name, modifiers, initialValue);
    }

    /**
     * Equivalent to {@code annotation(annotationType.getName(), emptyMap())}.
     *
     * @param annotationType
     */
    protected JavaWriter emitAnnotation(Class<? extends Annotation> annotationType) throws IOException {
        return javaWriter.emitAnnotation(annotationType);
    }

    /**
     * Emits a field declaration.
     *
     * @param type
     * @param name
     */
    protected JavaWriter emitField(String type, String name) throws IOException {
        return javaWriter.emitField(type, name);
    }

    /**
     * Emits a single line comment.
     *
     * @param comment
     * @param args
     */
    protected JavaWriter emitSingleLineComment(String comment, Object... args) throws IOException {
        return javaWriter.emitSingleLineComment(comment, args);
    }

    /**
     * Ends the current initializer declaration.
     */
    protected JavaWriter endInitializer() throws IOException {
        return javaWriter.endInitializer();
    }

    /**
     * Emit an import for each {@code type} in the provided {@code Collection}. For the duration of
     * the file, all references to these classes will be automatically shortened.
     *
     * @param types
     */
    protected JavaWriter emitImports(Collection<String> types) throws IOException {
        return javaWriter.emitImports(types);
    }

    /**
     * Emit a package declaration and empty line.
     *
     * @param packageName
     */
    protected JavaWriter emitPackage(String packageName) throws IOException {
        return javaWriter.emitPackage(packageName);
    }

    /**
     * Emit an import for each {@code type} provided. For the duration of the file, all references to
     * these classes will be automatically shortened.
     *
     * @param types
     */
    protected JavaWriter emitImports(String... types) throws IOException {
        return javaWriter.emitImports(types);
    }

    /**
     * Annotates the next element with {@code annotation} and {@code attributes}.
     *
     * @param annotation
     * @param attributes a map from annotation attribute names to their values. Values are encoded
     *                   using Object.toString(); use {@link #stringLiteral} for String values. Object arrays are
     */
    protected JavaWriter emitAnnotation(String annotation, Map<String, ?> attributes) throws IOException {
        return javaWriter.emitAnnotation(annotation, attributes);
    }

    protected String getIndent() {
        return javaWriter.getIndent();
    }
}
