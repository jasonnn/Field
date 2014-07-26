package field.namespace.context;

import field.bytecode.protect.BaseRef;
import field.math.graph.visitors.GraphNodeSearching.VisitCode;
import field.util.BetterWeakHashMap.BaseWeakHashMapKey;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.*;

/**
 * this is an example of a context tree that has arbitrary, and essentially internal, keys and interfaces to those keys. The keys <i>are</i> the storage.
 * <p/>
 * the set and get behavior of the keys are broken out into the main class for easy sub-classing
 *
 * @author marc
 */
public
class SimpleContextTopology extends ContextTopology<field.namespace.context.SimpleContextTopology.Context, field.namespace.context.SimpleContextTopology.Context> implements
                                                                                                                                                                  iSupportsBeginEnd<String> {

    public
    class Context extends BaseWeakHashMapKey implements iStorage {
        String name;

        HashMap<String, Context> children = new HashMap<String, Context>();

        WeakReference<Context> parent;

        HashMap<String, Object> values = new HashMap<String, Object>();

        public
        Context(String name) {
            this.name = name;
        }

        public
        Object get(String name) {
            BaseRef rr = new BaseRef(null);
            SimpleContextTopology.this.get(this, name, rr);
            return rr.get();
        }

        public
        VisitCode get(String name, BaseRef result) {
            return SimpleContextTopology.this.get(this, name, result);
        }

        public
        VisitCode set(String name, BaseRef value) {
            return SimpleContextTopology.this.set(this, name, value);
        }

        public
        void set(String name, Object value) {
            SimpleContextTopology.this.set(this, name, new BaseRef(value));
        }

        @Override
        public
        String toString() {
            return "c:" + name + '{' + values + '}';
        }

        public
        VisitCode unset(String name) {
            return SimpleContextTopology.this.unset(this, name);
        }
    }

    protected Context root = new Context("root");

    public static
    SimpleContextTopology newInstance() {
        return newInstance(null);
    }

    public static
    SimpleContextTopology newInstance(SimpleContextTopology sharedRoot) {
        return new SimpleContextTopology(Context.class, Context.class, sharedRoot);
    }

    SimpleContextTopology(Class<Context> k, Class<Context> i, SimpleContextTopology sharedRoot) {
        super(k, i);
        this.storage = new iContextStorage<Context, Context>() {
            public
            Context get(Context at, Method m) {
                return at;
            }
        };
        if (sharedRoot != null) {
            this.root = sharedRoot.root;
            this.setAt(root);
        }
    }


    public
    void begin(String k) {
        Context m = getAt().children.get(k);
        if (m != null) {
            setAt(m);
        }
        else {
            Context nc = new Context(k);
            nc.parent = new WeakReference<Context>(getAt());
            synchronized (getAt()) {
                getAt().children.put(k, nc);
            }
            setAt(nc);
        }
    }

    @Override
    public
    Set<Context> childrenOf(Context p) {
        return new HashSet<Context>(p.children.values());
    }

    @Override
    public
    void deleteChild(Context parent, Context name) {
        Set<Map.Entry<String, Context>> e = parent.children.entrySet();
        for (Map.Entry<String, Context> ee : e) {
            if (ee.getValue() == name) {
                synchronized (getAt()) {
                    parent.children.remove(ee.getKey());
                }
                name.parent.clear();
                return;
            }
        }
    }

    public
    void deleteChild(String child) {
        deleteChild(getAt(), getAt().children.get(child));
    }

    public
    void end(String k) {
        Context p = parentsOf(getAt()).iterator().next();
        assert p != null;
        assert p.children.get(k) == getAt();
        setAt(p);
    }

    public
    <T> T get(String name, T def) {
        Object o = getAt().values.get(name);
        if ((o == null) && !getAt().values.containsKey(name)) return def;
        return (T) o;
    }

    public
    Class<String> getBeginEndSupportedClass() {
        return String.class;
    }

    public
    Context getRoot() {
        return root;
    }

    public
    void go(Context c) {
        setAt(c);
    }

    @Override
    public
    Set<Context> parentsOf(Context k) {
        if (k.parent == null) return null;

        return Collections.singleton(k.parent.get());
    }

    public
    String pwd() {
        HashSet<Context> seen = new HashSet<Context>();
        Context aa = getAt();
        String p = "";
        while (!seen.contains(aa) && (aa != null)) {
            seen.add(aa);
            p = aa + "/" + p;
            aa = aa.parent == null ? null : aa.parent.get();
        }
        if (aa != null) p = "(loop)" + p;
        return p;

    }

    @Override
    public
    Context root() {
        return root;
    }

    public
    void set(String name, Object to) {
        Object o = getAt().values.put(name, to);
    }

    public
    Context where() {
        return getAt();
    }

    protected
    VisitCode get(Context c, String name, BaseRef result) {
        if (c.values.containsKey(name)) {
            result.set(c.values.get(name));
            return VisitCode.stop;
        }
        return VisitCode.cont;
    }

    protected
    VisitCode set(Context c, String name, BaseRef value) {
        c.values.put(name, value.get());
        return VisitCode.stop;
    }

    protected
    VisitCode unset(Context c, String name) {
        c.values.remove(name);
        return VisitCode.stop;
    }
}
