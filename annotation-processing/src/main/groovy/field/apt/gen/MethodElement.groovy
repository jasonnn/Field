package field.apt.gen

import field.bytecode.protect.annotations.Mirror
import groovy.util.logging.Log

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.*
import javax.lang.model.util.Elements
import javax.lang.model.util.SimpleTypeVisitor6
import javax.lang.model.util.Types
import java.util.logging.Level

/**
 * Created by jason on 7/12/14.
 */
@Log
class MethodElement implements ExecutableElement {


    public static class Factory {
        final Types types
        final Elements elements
        final String defaultPrefix;

        Factory(ProcessingEnvironment env) {
            this(env, '')
        }

        Factory(ProcessingEnvironment env, String defaultPrefix) {
            this.types = env.typeUtils
            this.elements = env.elementUtils
            this.defaultPrefix = defaultPrefix
        }

        public MethodElement create(ExecutableElement e) {
            assert e.kind == ElementKind.METHOD

            return new MethodElement(e, types, elements, defaultPrefix)
        }
    }


    @Delegate
    final ExecutableElement delegate
    final Types types
    final Elements elements
    final String defaultPrefix

    MethodElement(ExecutableElement delegate, Types types, Elements elements, String defaultPrefix) {
        this.delegate = delegate
        this.types = types
        this.elements = elements
        this.defaultPrefix = defaultPrefix
    }

    boolean hasReturnType() {
        return delegate.returnType.kind != TypeKind.VOID
    }

    String getReturnTypeName() {
        return MyTypeVisitor.getTypeName(returnType, types)
    }

    String getReflectionFieldName() {
        return prefix + simpleName
    }

    String getPrefix() {
        Mirror mirror = getAnnotation(Mirror.class);
        if (!mirror) log.log(Level.WARNING, "mirror was null for $delegate")
        String prefix = mirror?.prefix() ?: ""
        return prefix.isEmpty() ? defaultPrefix : prefix
    }

    public List<String> paramClassNames() {
        return parameters.collect { MyTypeVisitor.getParamName(it, types) }

//        def visit = MyTypeVisitor.instance.&visit.rcurry(types) << { VariableElement ve -> ve.asType() }
//        return parameters.collect(visit)
    }

    boolean hasParams() {
        return !parameters.empty
    }


    static class MyTypeVisitor extends SimpleTypeVisitor6<String, Types> {
        private static String removeJavaLang(String s) {
            return s.startsWith('java.lang.') ? s.substring(10) : s
        }

        public static final MyTypeVisitor INSTANCE = new MyTypeVisitor()

        static String getParamName(VariableElement e, Types types) {
            return removeJavaLang(INSTANCE.visit(e.asType(), types))

        }

        static String getTypeName(TypeMirror type, Types types) {
            return removeJavaLang(INSTANCE.visit(type, types))
        }

        @Override
        protected String defaultAction(TypeMirror e, Types types) {
            return "something got through the cracks: $e"
            //throw new RuntimeException(e.toString())
        }

        @Override
        String visitPrimitive(PrimitiveType t, Types types) {
            //   println "MyTypeVisitor.visitPrimitive: $t"
            return t.toString()
        }

        @Override
        String visitArray(ArrayType t, Types types) {
            //  println "MyTypeVisitor.visitArray: $t"
            def arr = t
            int dim = 0
            while (arr.kind == TypeKind.ARRAY) {
                dim++
                arr = arr.componentType
            }

            return visit(arr, types) + brackets(dim)
        }

        private static String brackets(int n) {
            def sb = new StringBuilder()
            n.times { sb << '[]' }
            return sb.toString()
        }


        @Override
        String visitDeclared(DeclaredType t, Types types) {
            // println "MyTypeVisitor.visitDeclared: $t"
            return t.asElement().toString()
        }

//        @Override
//        String visitExecutable(ExecutableType t, Types types) {
//            println "MyTypeVisitor.visitExecutable: $t"
//            return super.visitExecutable(t, types)
//        }
//
//
//        @Override
//        String visitUnion(UnionType t, Types types) {
//            println "MyTypeVisitor.visitUnion: $t"
//            return super.visitUnion(t, types)
//        }


        @Override
        String visitTypeVariable(TypeVariable t, Types types) {
//            println "MyTypeVisitor.visitTypeVariable: $t"

            return t.lowerBound.kind != TypeKind.NULL ?
                    t.lowerBound.accept(this, types) :
                    t.upperBound.accept(this, types)

        }

//        @Override
//        String visitWildcard(WildcardType t, Types types) {
//            println "MyTypeVisitor.visitWildcard: $t"
//            return super.visitWildcard(t, types)
//        }
    }
}
