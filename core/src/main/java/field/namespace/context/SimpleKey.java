package field.namespace.context;

import field.bytecode.protect.BaseRef;

/**
* Created by jason on 7/14/14.
*/
public class SimpleKey<t_Key, T> {
    public final String name;

    private final ContextTopology<t_Key, iStorage> c;

    private final Dispatch<t_Key, ? extends iStorage> d;

    public SimpleKey(String name, ContextTopology<t_Key, ? extends iStorage> c) {
        this.name = name;
        this.c = (ContextTopology<t_Key, iStorage>) c;
        d = new Dispatch(this.c);
    }

    public T get() {
        BaseRef<T> ref = new BaseRef<T>(null);
        iStorage s = d.getOverrideProxyFor(iStorage.class);
        s.get(name, ref);
        return ref.get();
    }

    public T get(t_Key k) {
        t_Key was = c.getAt();
        c.setAt(k);
        try {
            BaseRef<T> ref = new BaseRef<T>(null);
            iStorage s = d.getOverrideProxyFor(iStorage.class);
            s.get(name, ref);
            return ref.get();
        } finally {
            c.setAt(was);
        }
    }

    public T set(T to) {
        BaseRef<T> ref = new BaseRef<T>(to);
        iStorage s = d.getOverrideProxyFor(iStorage.class);
        s.set(name, ref);
        return ref.get();
    }

    public T set(t_Key k, T to) {
        t_Key was = c.getAt();
        c.setAt(k);
        try {
            BaseRef<T> ref = new BaseRef<T>(to);
            iStorage s = d.getOverrideProxyFor(iStorage.class);
            s.set(name, ref);
            return ref.get();
        } finally {
            c.setAt(was);
        }
    }
}
