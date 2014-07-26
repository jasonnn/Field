package field.protect.asm.model;


import field.protect.asm.ASMType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;

/**
 * Created by jason on 7/16/14.
 */
public class SimpleMethodModel extends AbstractSimpleModel {
    //private static final
    @Nullable
    public final String desc; //TODO when is this null? <init>?
    @NotNull
    public final String[] exceptions;


    public SimpleMethodModel(int access,
                             @NotNull String name,
                             @Nullable String signature,
                             Set<String> annotations,
                             @Nullable String desc,
                             @Nullable String[] exceptions) {
        super(access, name, signature, annotations);
        this.desc = desc;

        this.exceptions = ensureNonNull(exceptions);
        Arrays.sort(this.exceptions);

    }

    public ASMType[] getArgumentTypes() {
        return ASMType.getArgumentTypes(signature);
    }


    @SuppressWarnings("RedundantIfStatement")
    @Override
    protected boolean doEquals(Object o) {
        if ((o == null) || (getClass() != o.getClass())) return false;
        if (!super.doEquals(o)) return false;

        SimpleMethodModel that = (SimpleMethodModel) o;

        if ((desc != null) ? !desc.equals(that.desc) : (that.desc != null)) return false;
        if (!Arrays.equals(exceptions, that.exceptions)) return false;

        return true;
    }

    @Override
    protected int doHashCode() {
        int result = super.doHashCode();
        result = 31 * result + ((desc != null) ? desc.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(exceptions);
        return result;
    }

    @Override
    public String toString() {
        return "SimpleMethodModel{" +
                "name='" + name + '\'' +
                "desc='" + desc + '\'' +
                ", exceptions=" + Arrays.toString(exceptions) +
                '}';
    }
}
