package field.core.util;

import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.NextUpdate;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;

import java.util.ArrayList;
import java.util.List;

@Woven
public
class LocalFuture<T> {
    boolean set = false;

    T t;
    List<IAcceptor<T>> continuation = new ArrayList<IAcceptor<T>>();

    public
    void addContinuation(final IUpdateable u) {
        continuation.add(new IAcceptor<T>() {

            public
            IAcceptor<T> set(T to) {
                u.update();
                return this;
            }
        });
    }

    public
    void addContinuation(final IAcceptor<T> a) {
        continuation.add(a);
    }

    public
    T get() {
        return t;
    }

    public
    boolean has() {
        return set;
    }

    @NextUpdate
    public
    void set(T t) {
        this.t = t;
        for (IAcceptor<T> u : continuation)
            u.set(t);
    }
}