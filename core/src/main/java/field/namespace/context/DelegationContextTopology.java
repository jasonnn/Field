package field.namespace.context;

import field.namespace.generic.ReflectionTools;
import field.util.BetterWeakHashMap.BaseWeakHashMapKey;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * a set of "contexts" that can be hand assembled for making delegation chains
 *
 * @author marc
 */
public
class DelegationContextTopology<K> extends ContextTopology<K, K> {

    protected Context root = new Context();

    public
    class Context extends BaseWeakHashMapKey {
        WeakReference<K> name;

        LinkedHashMap<K, Context> children = new LinkedHashMap<K, Context>();

        LinkedHashMap<K, Context> parent = new LinkedHashMap<K, Context>();

        public
        Context(K name) {
            this.name = new WeakReference<K>(name);
        }

        protected
        Context() {
            this.name = null;
        }
    }

    public
    DelegationContextTopology(Class<K> c) {
        super(c, c);
        this.storage = new iContextStorage<K, K>() {
            public
            K get(K at, Method m) {
                return at;
            }
        };

        knownContexts.put(null, new Context());
    }

    public
    void begin(K k) {
        setAt(contextFor(contextFor(null, getAt()), k).name.get());
    }

    public
    void end(K k) {
        assert getAt().equals(k) : k + " " + getAt();

        Context c = contextFor(null, getAt());
        if (!c.parent.isEmpty()) {
            K top = getTop(c.parent);
            setAt(top);
        }
        else throw new IllegalArgumentException(" end with no next ? ");
    }

    private
    K getTop(LinkedHashMap<K, Context> p) {
        Map.Entry<K, Context> newest =
                (Entry<K, Context>) ReflectionTools.illegalGetObject(ReflectionTools.illegalGetObject(p, "header"),
                                                                     "before");
        K top = newest.getKey();
        return top;
    }

    // useful for constructing delegation chains
    public
    void begin(K... k) {
        for (K n : k)
            begin(n);
    }

    // useful for constructing delegation chains
    public
    void end(K... k) {
        for (int i = 0; i < k.length; i++)
            end(k[k.length - 1 - i]);
    }

    public
    void connect(K parent, K child) {
        contextFor(contextFor(null, parent), child);
    }

    WeakHashMap<K, Context> knownContexts = new WeakHashMap<K, Context>();

    public
    Context contextFor(Context parent, K k) {
        if (k == null) return root;

        Context cc = knownContexts.get(k);
        if (cc == null) {
            cc = new Context(k);
            knownContexts.put(k, cc);
        }

        if (parent != null) {
            K nn = (parent.name == null) ? null : parent.name.get();
            cc.parent.remove(nn);
            cc.parent.put(nn, parent);
            parent.children.put(k, cc);
        }
        return cc;
    }

    @Override
    public
    Set<K> childrenOf(K p) {
        return new HashSet<K>(contextFor(null, p).children.keySet());
    }

    @Override
    public
    void deleteChild(K parent, K name) {
        Context cp = contextFor(null, parent);
        Set<Map.Entry<K, Context>> e = cp.children.entrySet();
        for (Map.Entry<K, Context> ee : e) {
            if (ee.getValue() == name) {
                synchronized (getAt()) {
                    cp.children.remove(ee.getKey());
                }
                contextFor(null, name).parent.clear();
                return;
            }
        }
        return;
    }

    @Override
    public
    Set<K> parentsOf(K k) {
        HashMap<K, Context> pp = contextFor(null, k).parent;
        if (pp == null) return null;
        return new HashSet<K>(pp.keySet());
    }

    @Override
    public
    K root() {
        return null;
    }

    protected
    void deleteChild(K k) {
        contextFor(null, getAt()).children.remove(k);
    }

    @Override
    public
    void delete(K child) {
        assert (getAt() == null) || !getAt().equals(child) : "can't delete current context";
        LinkedHashMap<K, Context> p = contextFor(null, getAt()).parent;
        for (K pp : p.keySet()) {
            contextFor(null, pp).children.remove(child);
        }
        knownContexts.remove(child);
    }


    public
    String pwd() {
        HashSet<K> seen = new HashSet<K>();
        K aa = getAt();
        String p = "";
        while (!seen.contains(aa) && (aa != null)) {
            seen.add(aa);
            p = aa + "/" + p;
            LinkedHashMap<K, Context> pnext = contextFor(null, aa).parent;
            if (pnext.isEmpty()) {
                aa = null;
                break;
            }
            aa = getTop(pnext);
        }
        if (aa != null) p = "(loop)" + p;
        return p;

    }

}
