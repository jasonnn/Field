package field.util.collect.tuple;

import java.io.Serializable;

/**
 * Created by jason on 7/21/14.
 */
public
class Pair<A, B> implements Serializable {
    public A left;
    public B right;

    public
    Pair(A a, B b) {
        left = a;
        right = b;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public
    boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || (getClass() != o.getClass())) return false;

        Pair pair = (Pair) o;

        if ((left != null) ? !left.equals(pair.left) : (pair.left != null)) return false;
        if ((right != null) ? !right.equals(pair.right) : (pair.right != null)) return false;

        return true;
    }

    @Override
    public
    int hashCode() {
        int result = (left != null) ? left.hashCode() : 0;
        result = 31 * result + ((right != null) ? right.hashCode() : 0);
        return result;
    }

    public
    String toString() {
        return "left:" + left + " right:" + right;
    }
}
