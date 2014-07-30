package field.apt.util;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementScanner6;
import javax.lang.model.util.SimpleTypeVisitor6;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jason on 7/29/14.
 */
public
class CollectImportsScanner extends ElementScanner6<Void, Collection<String>> {



    public static final CollectImportsScanner INSTANCE = new CollectImportsScanner();

    public static
    Set<String> getVisibleDependencies(Element e) {
        Set<String> set = new HashSet<String>();
        e.accept(INSTANCE, set);
        return set;
    }


    @Override
    public
    Void visitType(TypeElement e, Collection<String> strings) {
        strings.add(e.getQualifiedName().toString());
        return super.visitType(e, strings);
    }

    @Override
    public
    Void visitVariable(VariableElement e, Collection<String> strings) {
        MyTypeVisitor.INSTANCE.visit(e.asType(),strings);
        return super.visitVariable(e, strings);
    }

    @Override
    public
    Void visitExecutable(ExecutableElement e, Collection<String> strings) {
        MyTypeVisitor.INSTANCE.visit(e.getReturnType(), strings);
        return super.visitExecutable(e, strings);
    }

    @Override
    public
    Void visitTypeParameter(TypeParameterElement e, Collection<String> strings) {
        return super.visitTypeParameter(e, strings);
    }

    static
    class MyTypeVisitor extends SimpleTypeVisitor6<Void, Collection<String>> {
        public static final MyTypeVisitor INSTANCE = new MyTypeVisitor();

        @Override
        public
        Void visitArray(ArrayType t, Collection<String> strings) {
            return visit(t.getComponentType(), strings);
        }

        @Override
        public
        Void visitDeclared(DeclaredType t, Collection<String> strings) {
            strings.add(t.asElement().toString());
            return super.visitDeclared(t, strings);
        }

        @Override
        public
        Void visitTypeVariable(TypeVariable t, Collection<String> strings) {
            visit(t.getLowerBound(),strings);
            visit(t.getUpperBound(),strings);
            return super.visitTypeVariable(t, strings);
        }

        @Override
        public
        Void visitExecutable(ExecutableType t, Collection<String> strings) {
            return super.visitExecutable(t, strings);
        }

        @Override
        public
        Void visitWildcard(WildcardType t, Collection<String> strings) {

            return super.visitWildcard(t, strings);
        }
    }
}
