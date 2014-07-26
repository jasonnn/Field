package field.protect.asm.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Created by jason on 7/16/14.
 */
public class SimpleFieldModel extends AbstractSimpleModel {

    @Nullable
    String desc;
    @Nullable
    Object value;

    public SimpleFieldModel(int access,
                            @NotNull String name,
                            @NotNull String signature,
                            @Nullable String desc,
                            @Nullable Object value,
                            Set<String> annotations) {
        super(access, name, signature, annotations);
        this.desc = desc;
        this.value = value;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    protected boolean doEquals(Object o) {
        if (this == o) return true;
        if ((o == null) || (getClass() != o.getClass())) return false;
        if (!super.doEquals(o)) return false;

        SimpleFieldModel that = (SimpleFieldModel) o;

        if ((desc != null) ? !desc.equals(that.desc) : (that.desc != null)) return false;
        if ((value != null) ? !value.equals(that.value) : (that.value != null)) return false;

        return true;
    }

    @Override
    protected int doHashCode() {
        int result = super.doHashCode();
        result = 31 * result + ((desc != null) ? desc.hashCode() : 0);
        result = 31 * result + ((value != null) ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SimpleFieldModel{" +
                "desc='" + desc + '\'' +
                ", value=" + value +
                '}';
    }
}
