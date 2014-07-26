package field.bytecode.protect.cache;

import java.util.Arrays;


public
class ModCountArrayWrapper implements iGetModCount<ModCountArrayWrapper> {

    private final Object[] a;

    public
    ModCountArrayWrapper(Object[] a) {
        this.a = a;
    }

    private static
    int computeModCount(Object o) {
        int m = 0;
        if (o instanceof iGetModCount) {
            return ((iGetModCount) o).countFor(o);
        }
        else if (o instanceof Iterable) {
            for (Object oo : ((Iterable) o)) {
                m += computeModCount(oo);
            }
            return m;
        }
        else if (o instanceof Object[]) {
            for (Object oo : ((Object[]) o)) {
                m += computeModCount(oo);
            }
            return m;
        }
        else if (o == null) return 0;

        assert false : o + " " + o.getClass();
        return 0;
    }

    public
    int countFor(ModCountArrayWrapper t) {
        return computeModCount(t.a);
    }

    @Override
    public
    int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(a);
        return result;
    }

    @Override
    public
    boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ModCountArrayWrapper other = (ModCountArrayWrapper) obj;
        return Arrays.equals(a, other.a);
    }

}
