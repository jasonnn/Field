package field.apt.util;

import com.squareup.javawriter.JavaWriter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.*;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;

/**
 * Created by jason on 7/10/14.
 */
public
class GenUtils {

    public static
    String getRawTypeName(Types types, TypeMirror type) {
        return getRawTypeName(false, types, type);
    }

    public static
    String getRawTypeName(boolean boxing, Types types, TypeMirror type) {
        return boxing ? RawTypeNameVisitor.BOXING.visit(type, types) : RawTypeNameVisitor.NON_BOXING.visit(type, types);

    }


    public static final String GEN_SUFFIX = "_m";

    public static
    String generatedSimpleName(TypeElement e) {
        return e.getSimpleName().toString() + GEN_SUFFIX;
    }

    public static
    String generatedFQN(TypeElement e) {
        return e.getQualifiedName().toString() + GEN_SUFFIX;
    }

    public static
    JavaWriter javaWriter(TypeElement e, ProcessingEnvironment env) throws IOException {
        return new JavaWriter(createSourceFile(e, env).openWriter());
    }

    public static
    JavaFileObject createSourceFile(TypeElement e, ProcessingEnvironment env) throws IOException {
        return env.getFiler().createSourceFile(generatedFQN(e), e);
    }

    public static
    String packageNameOf(TypeElement e) {
        return packageOf(e).getQualifiedName().toString();
    }

    public static
    PackageElement packageOf(TypeElement e) {
        for (Element parent = e; parent != null; parent = parent.getEnclosingElement()) {
            if (parent.getKind() == ElementKind.PACKAGE)
                return (PackageElement) parent;
        }
        throw new RuntimeException("??!!");
    }

    public static
    String getTypeName(TypeMirror typeMirror) {
        return ParameterizedTypeNameVisitor.INSTANCE.visit(typeMirror);
    }

            private static String removeJavaLang(String s) {
            return s.startsWith("java.lang.") ? s.substring(10) : s;
        }
    static
    class ParameterizedTypeNameVisitor extends SimpleTypeVisitor6<String, Void> {
        static final ParameterizedTypeNameVisitor INSTANCE = new ParameterizedTypeNameVisitor();

        @Override
        protected
        String defaultAction(TypeMirror e, Void aVoid) {
            return e.toString();
        }

        @Override
        public
        String visitDeclared(DeclaredType t, Void aVoid) {

            //return t.asElement().toString();
            //TODO deal with recursion, or is this even necessary?
            if (t.getTypeArguments().isEmpty()) {
                return super.visitDeclared(t, aVoid);
            }
            boolean hasTypeVars = false;
            for (TypeMirror mirror : t.getTypeArguments()) {
                if (mirror.getKind() == TypeKind.TYPEVAR) {
                    hasTypeVars = true;
                    break;
                }
            }
            return hasTypeVars ? t.asElement().toString() : super.visitDeclared(t, aVoid);

//            List<String> strings = new ArrayList<String>(t.getTypeArguments().size());
//            for (TypeMirror mirror : t.getTypeArguments()) {
//                strings.add(visit(mirror));
//            }
//            StringBuilder sb = new StringBuilder();
//            sb.append(t.asElement().toString()).append('<');
//            Joiner.on(',').appendTo(sb, strings);
//            sb.append('>');
//            return sb.toString();


        }

        @Override
        public
        String visitTypeVariable(TypeVariable t, Void aVoid) {

            return t.getLowerBound().getKind() == TypeKind.NULL ? visit(t.getUpperBound()) : visit(t.getLowerBound());
        }

        @Override
        public
        String visitWildcard(WildcardType t, Void aVoid) {
            return super.visitWildcard(t, aVoid);
        }
    }


    static
    class RecursionAwareTypeScanner extends SimpleTypeVisitor6<String, Void> {

    }


}
