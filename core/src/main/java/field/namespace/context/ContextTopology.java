package field.namespace.context;

import java.util.Set;

/**
* Created by jason on 7/14/14.
*/
public abstract class ContextTopology<K, I> {
    public iContextStorage<K, I> storage;

    protected final Class<K> keyClass;

    protected  Class<I> interfaceClass;

    ThreadLocal<K> at = new ThreadLocal<K>() {
        @Override
        protected K initialValue() {
            return root();
        }
    };

    protected ContextTopology(Class<K> keyClass, Class<I> interfaceClass) {
        this.keyClass = keyClass;
        this.interfaceClass = interfaceClass;

    }

    protected ContextTopology(Class<K> keyClass) {
        this.keyClass = keyClass;
    }

    public void setInterfaceClass(Class<I> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public abstract
    Set<K> childrenOf(K p);

    public void delete(K child) {
        throw new IllegalArgumentException(" not implemented (optional delete key)");
    }

    public abstract
    void deleteChild(K parent, K child);

    public K getAt() {
        return at.get();
    }

    public Class<I> getInterfaceClass() {
        return interfaceClass;
    }

    public Class<K> getKeyClass() {
        return keyClass;
    }

    public abstract
    Set<K> parentsOf(K k);

    public abstract
    K root();

    // returns where
    // we were
    public K setAt(K k) {
        K was = at.get();
        at.set(k);
        return was;
    }

}
