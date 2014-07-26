package field.namespace.generic.tuple;

import java.io.Serializable;

/**
 * Created by jason on 7/21/14.
 */
public class Triple<A, B, C> implements Serializable {
    public A left;
    public B middle;
    public C right;

    public Triple(A a, B b, C c) {
        left = a;
        middle = b;
        right = c;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || (getClass() != o.getClass())) return false;

        Triple triple = (Triple) o;

        if ((left != null) ? !left.equals(triple.left) : (triple.left != null)) return false;
        if ((middle != null) ? !middle.equals(triple.middle) : (triple.middle != null)) return false;
        if ((right != null) ? !right.equals(triple.right) : (triple.right != null)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (left != null) ? left.hashCode() : 0;
        result = 31 * result + ((middle != null) ? middle.hashCode() : 0);
        result = 31 * result + ((right != null) ? right.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "left:" + left + " middle:" + middle + " right:" + right;
    }
}
