package field.protect.asm.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by jason on 7/16/14.
 */
public class SimpleClassModel extends AbstractSimpleModel {
    @NotNull
    public final Set<SimpleFieldModel> fields;
    @NotNull
    public final Set<SimpleMethodModel> methods;
    @NotNull
    public final String superName;
    @NotNull
    public final String[] interfaces;


    SimpleClassModel(int access,
                     @NotNull String name,
                     @Nullable String signature,
                     @NotNull String superName,
                     @Nullable String[] interfaces,
                     Set<String> annotations,
                     Set<SimpleFieldModel> fields,
                     Set<SimpleMethodModel> methods) {
        super(access, name, signature, annotations);
        this.superName = superName;
        this.interfaces = ensureNonNull(interfaces);
        this.fields = ensureNonNull(fields);
        this.methods = ensureNonNull(methods);
        Arrays.sort(this.interfaces);
    }

    public Type asType() {
        return Type.getObjectType(name);
    }

    //TODO this could be more robust (determined during visiting)
    public boolean isInner() {
        return name.indexOf('$') != -1;
    }

    public boolean isInterface() {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    protected boolean doEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.doEquals(o)) return false;

        SimpleClassModel that = (SimpleClassModel) o;

        if (!fields.equals(that.fields)) return false;
        if (!Arrays.equals(interfaces, that.interfaces)) return false;
        if (!methods.equals(that.methods)) return false;
        if (!superName.equals(that.superName)) return false;
        return true;
    }

    @Override
    protected int doHashCode() {
        int result = super.doHashCode();
        result = 31 * result + fields.hashCode();
        result = 31 * result + methods.hashCode();
        result = 31 * result + superName.hashCode();
        result = 31 * result + (interfaces != null ? Arrays.hashCode(interfaces) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SimpleClassModel{" +
                "fields=" + fields +
                ", methods=" + methods +
                ", superName='" + superName + '\'' +
                ", interfaces=" + Arrays.toString(interfaces) +
                '}';
    }
}


