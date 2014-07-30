package field.apt.util;

import javax.lang.model.type.*;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

/**
 * Created by jason on 7/29/14.
 */
public
class TypeNameVisitor extends SimpleTypeVisitor6<String, Types> {
    public
    TypeNameVisitor(boolean boxing) {
        this.boxing = boxing;
    }

    public static
    String getName(Types types, TypeMirror mirror) {
        return mirror.accept(NON_BOXING, types);
    }

    public static
    String getName_Boxing(Types types, TypeMirror mirror) {
        return mirror.accept(BOXING, types);
    }

    public static TypeNameVisitor BOXING = new TypeNameVisitor(true);

    public static final TypeNameVisitor NON_BOXING = new TypeNameVisitor(false);

    private final boolean boxing;

    @Override
    public
    String visitPrimitive(PrimitiveType t, Types types) {
        return boxing ? types.boxedClass(t).getQualifiedName().toString() : t.toString();
    }

    @Override
    public
    String visitArray(ArrayType t, Types types) {
        TypeMirror arr = t;
        int dim = 0;
        while (arr.getKind() == TypeKind.ARRAY) {
            dim++;
            arr = ((ArrayType) arr).getComponentType();
        }
        return visit(arr, types) + brackets(dim);
    }

    private
    String brackets(int dim) {
        char[] chars = new char[dim * 2];
        int i = 0;
        while (i < chars.length) {
            chars[i++] = '[';
            chars[i++] = ']';
        }
        return new String(chars);
    }

    @Override
    public
    String visitTypeVariable(TypeVariable t, Types types) {
        return t.getLowerBound().getKind() != TypeKind.NULL
               ? t.getLowerBound().accept(this, types)
               : t.getUpperBound().accept(this, types);
    }


    @Override
    public
    String visitExecutable(ExecutableType t, Types types) {
        return super.visitExecutable(t, types);
    }

    @Override
    public
    String visitDeclared(DeclaredType t, Types types) {
        return t.asElement().toString();
    }
}
