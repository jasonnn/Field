package field.bytecode.protect.analysis.model;

import field.bytecode.protect.asm.CommonTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

/**
 * Created by jason on 7/16/14.
 */
public abstract class AbstractSimpleModel {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];


    public final int access;
    @NotNull
    public final String name;
    @Nullable
    public final String signature;
    @NotNull
    public final Set<String> annotations;

    public AbstractSimpleModel(int access, @NotNull String name, @Nullable String signature, Set<String> annotations) {
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.annotations = ensureNonNull(annotations);
    }


    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public final boolean equals(Object o) {
        return doEquals(o);
    }

    @SuppressWarnings("RedundantIfStatement")
    protected boolean doEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractSimpleModel that = (AbstractSimpleModel) o;

        if (access != that.access) return false;
        if (!annotations.equals(that.annotations)) return false;
        if (!name.equals(that.name)) return false;
        if (signature != null ? !signature.equals(that.signature) : that.signature != null) return false;

        return true;
    }

    protected int doHashCode() {
        int result = access;
        result = 31 * result + name.hashCode();
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        result = 31 * result + annotations.hashCode();
        return result;
    }

    private transient int hash = 0;

    @Override
    public final int hashCode() {
        int tmp = hash;
        if (tmp == 0) tmp = hash = doHashCode();
        return tmp;
    }

    protected static
    @NotNull
    <T> Set<T> ensureNonNull(@Nullable Set<T> set) {
        return (set == null) ?
                Collections.<T>emptySet() : set.isEmpty() ?
                Collections.<T>emptySet() : Collections.unmodifiableSet(set);
    }

    protected static
    @NotNull
    String[] ensureNonNull(@Nullable String[] str) {
        return (str == null) ?
                EMPTY_STRING_ARRAY : str.length == 0 ?
                EMPTY_STRING_ARRAY : str;
    }


    public boolean isWoven() {
        return annotations.contains(CommonTypes.WOVEN.getClassName());
    }



}
