package field.bytecode.protect.cache;

import java.util.ArrayList;

/**
 * immutablility refers to the objects inside , recomputes hashcode and equality on demand, this is good for caches, where the cost of calculating the hashcode for the key is large
 * <p/>
 * bah! a hashmap lookup _always_ needs both an hashCode() and an Equals so this is only a bit faster
 *
 * @param <E>
 */
public
class ImmutableArrayList<E> extends ArrayList<E> implements iImmutableContainer {

    int lastHashCode = 0;

    int lastModCount = -1;

    String bigBaseHashCode;

    @Override
    public
    int hashCode() {
        if (modCount == lastModCount) return lastHashCode;
        lastModCount = modCount;
        bigBaseHashCode = null;
        return lastHashCode = super.hashCode();
    }

    @Override
    public
    boolean equals(Object o) {
        if (!(o instanceof ArrayList)) return false;
        if (this == o) return true;
        if (o instanceof ImmutableArrayList) {
            if (hashCode() != o.hashCode()) return false;
            if (size() != ((ImmutableArrayList) o).size()) return false;
        }
        return getBigBaseHashCode().equals(((ImmutableArrayList) o).getBigBaseHashCode()) || super.equals(o);
    }

    public
    String getBigBaseHashCode() {
        if (bigBaseHashCode != null) return bigBaseHashCode;

        return bigBaseHashCode = getBigBaseHashCodeForIterable(this);
    }

    public static
    String getBigBaseHashCodeForIterable(Iterable i) {
        StringBuilder buffer = new StringBuilder();
        for (Object o : i) {
            int q = System.identityHashCode(o);
            buffer.append(':');
            buffer.append(toBase256(q));
        }
        return buffer.toString();
    }

    public static
    String getBigBaseHashCodeForIterable(Object[] i) {
        StringBuilder buffer = new StringBuilder();
        for (Object o : i) {
            int q = System.identityHashCode(o);
            buffer.append(':');
            buffer.append(toBase256(q));
        }
        return buffer.toString();
    }

    public static char[] base256Table = null;

    public static
    StringBuffer toBase256(int num) {
        if (base256Table == null) {
            base256Table = new char[256];
            for (int i = 0; i < 256; i++)
                base256Table[i] = (char) i;
        }
        StringBuffer sb = new StringBuffer(4);
        if (num < 0) {
            sb.append('-');
            num = -num;
        }
        sb.append(base256Table[num & 255]);
        num >>= 8;
        sb.append(base256Table[num & 255]);
        num >>= 8;
        sb.append(base256Table[num & 255]);
        num >>= 8;
        sb.append(base256Table[num & 255]);
        return sb;
    }
}
