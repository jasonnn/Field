package field.namespace.context;

import field.bytecode.protect.BaseRef;

/**
 * Created by jason on 7/14/14.
 */
public
class SimpleKey<t_Key, T> {
    public final String name;

    private final ContextTopology<t_Key, IStorage> c;

    private final Dispatch<t_Key, ? extends IStorage> d;

    public
    SimpleKey(String name, ContextTopology<t_Key, ? extends IStorage> c) {
        this.name = name;
        this.c = (ContextTopology<t_Key, IStorage>) c;
        d = new Dispatch(this.c);
    }

    public
    T get() {
        BaseRef<T> ref = new BaseRef<T>(null);
        IStorage s = d.getOverrideProxyFor(IStorage.class);
        s.get(name, ref);
        return ref.get();
    }

    public
    T get(t_Key k) {
        t_Key was = c.getAt();
        c.setAt(k);
        try {
            BaseRef<T> ref = new BaseRef<T>(null);
            IStorage s = d.getOverrideProxyFor(IStorage.class);
            s.get(name, ref);
            return ref.get();
        } finally {
            c.setAt(was);
        }
    }

    public
    T set(T to) {
        BaseRef<T> ref = new BaseRef<T>(to);
        IStorage s = d.getOverrideProxyFor(IStorage.class);
        s.set(name, ref);
        return ref.get();
    }

    public
    T set(t_Key k, T to) {
        t_Key was = c.getAt();
        c.setAt(k);
        try {
            BaseRef<T> ref = new BaseRef<T>(to);
            IStorage s = d.getOverrideProxyFor(IStorage.class);
            s.set(name, ref);
            return ref.get();
        } finally {
            c.setAt(was);
        }
    }
}
