package field.namespace.context;

import field.bytecode.protect.BaseRef;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.util.BetterWeakHashMap.BaseWeakHashMapKey;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.*;

/**
 * class keeps around a mapping from an exernal object to internal contexts,
 * reassembles these as needed in the hierarchy loosely driven by the begin and
 * end sequence
 *
 * @author marc
 */
public
class ExternalContextTopology<K> extends ContextTopology<K, ExternalContextTopology<K>.Context> {

    public
    class Context extends BaseWeakHashMapKey implements iStorage {
        WeakReference<K> name;

        HashMap<K, Context> children = new HashMap<K, Context>();

        WeakReference<Context> parent;

        HashMap<String, Object> values = new HashMap<String, Object>();

        public
        Context(K name) {
            this.name = new WeakReference<K>(name);
        }

        protected
        Context() {
            this.name = null;
        }

        public
        Object get(String name) {
            BaseRef rr = new BaseRef(null);
            ExternalContextTopology.this.get(this, name, rr);
            return rr.get();
        }

        public
        TraversalHint get(String name, BaseRef result) {
            return ExternalContextTopology.this.get(this, name, result);
        }

        public
        TraversalHint set(String name, BaseRef value) {
            return ExternalContextTopology.this.set(this, name, value);
        }

        public
        void set(String name, Object value) {
            ExternalContextTopology.this.set(this, name, new BaseRef(value));
        }

        public
        TraversalHint unset(String name) {
            return ExternalContextTopology.this.unset(this, name);
        }

    }

    protected Context root = new Context();

    WeakHashMap<K, Context> knownContexts = new WeakHashMap<K, Context>();

    public
    ExternalContextTopology(Class<K> keyClass) {
        super(keyClass);
        setInterfaceClass((Class<Context>) root.getClass());
//		super(keyClass, Context.class);
        this.storage = new iContextStorage<K, Context>() {
            public
            Context get(K at, Method m) {
                return contextFor(null, at);
            }
        };

        knownContexts.put(null, new Context());
    }

    public
    void begin(K k) {
        setAt(contextFor(contextFor(null, getAt()), k).name.get());
    }

    @Override
    public
    Set<K> childrenOf(K p) {
        return new HashSet<K>(contextFor(null, p).children.keySet());
    }

    public
    Context contextFor(Context parent, K k) {
        if (k == null) return root;

        Context cc = knownContexts.get(k);
        if (cc == null) {
            cc = new Context(k);
            knownContexts.put(k, cc);
        }

        if (parent != null) {
            cc.parent = new WeakReference<Context>(parent);
            parent.children.put(k, cc);
        }
        return cc;
    }

    @Override
    public
    void delete(K child) {
        WeakReference<Context> p = contextFor(null, getAt()).parent;
        if ((p != null) && (p.get() != null)) {
            p.get().children.remove(child);
        }
        knownContexts.remove(child);
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

    public
    void end(K k) {
        assert getAt() == k : k + " " + getAt();
        WeakReference<K> nn = contextFor(null, getAt()).parent.get().name;
        if (nn == null) setAt(null);
        else setAt(nn.get());
    }

    @Override
    public
    Set<K> parentsOf(K k) {
        WeakReference<Context> pp = contextFor(null, k).parent;
        if (pp == null) return null;
        WeakReference<K> n = pp.get().name;
        if (n == null) return null;

        return Collections.singleton(n.get());
    }

    public
    String pwd() {
        HashSet<K> seen = new HashSet<K>();
        K aa = getAt();
        String p = "";
        while (!seen.contains(aa) && (aa != null)) {
            seen.add(aa);
            p = aa + "/" + p;
            aa = contextFor(null, aa).parent.get().name == null ? null : contextFor(null, aa).parent.get().name.get();
        }
        if (aa != null) p = "(loop)" + p;
        return p;

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

    protected
    TraversalHint get(Context c, String name, BaseRef result) {
        if (c.values.containsKey(name)) {
            result.set(c.values.get(name));
            return StandardTraversalHint.STOP;
        }
        return StandardTraversalHint.CONTINUE;
    }

    protected
    TraversalHint set(Context c, String name, BaseRef value) {
        c.values.put(name, value.get());
        return StandardTraversalHint.STOP;
    }

    protected
    TraversalHint unset(Context c, String name) {
        c.values.remove(name);
        return StandardTraversalHint.STOP;
    }

}
