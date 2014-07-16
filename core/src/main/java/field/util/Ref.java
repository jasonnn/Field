package field.util;

/**
 * Created by jason on 7/16/14.
 */
public class Ref<T> {


    private transient volatile T ref;
    private boolean wasSet = false;

    public static <T> Ref<T> create() {
        return new Ref<T>();
    }

    public static <T> Ref<T> create(T ref) {
        Ref<T> r = new Ref<T>();
        r.set(ref);
        return r;
    }

    public void set(T t) {
        this.ref = t;
        wasSet = true;
    }

    public T get() {
        return ref;
    }

    public boolean valueWasSet() {
        return wasSet;
    }

    public boolean isNull() {
        return ref == null;
    }
}
