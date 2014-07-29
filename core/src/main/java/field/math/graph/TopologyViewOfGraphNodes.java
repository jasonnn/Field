package field.math.graph;

import field.namespace.generic.ReflectionTools;

import java.util.*;


public
class TopologyViewOfGraphNodes<T extends IMutable<T>> implements ISynchronizedTopology<T> {

    private final boolean backwards;
    private boolean everything;

    public
    TopologyViewOfGraphNodes(boolean backwards) {
        this.backwards = backwards;
        this.everything = false;
    }

    public
    TopologyViewOfGraphNodes<T> setEverything(boolean everything) {
        this.everything = everything;
        return this;
    }

    public
    TopologyViewOfGraphNodes() {
        this(false);
    }

    public
    void begin() {
        ReflectionTools.apply(notes, IMutableTopology.method_begin);
    }

    public
    void end() {
        ReflectionTools.apply(notes, IMutableTopology.method_end);
    }

    public
    void addChild(T from, T to) {
        from.addChild(to);
        ReflectionTools.apply(notes, IMutableTopology.method_addChild, from, to);
    }

    public
    void removeChild(T from, T to) {
        from.removeChild(to);
        ReflectionTools.apply(notes, IMutableTopology.method_removeChild, from, to);
    }

    Set<IMutableTopology<? super T>> notes = new LinkedHashSet<IMutableTopology<? super T>>();

    public
    void registerNotify(IMutableTopology<? super T> here) {
        notes.add(here);
    }

    public
    void deregisterNotify(IMutableTopology<? super T> here) {
        notes.remove(here);
    }

    public
    void added(T t) {
    }

    public
    void removed(T t) {
    }

    public
    void update(T t) {
    }

    public
    List<T> getParentsOf(T of) {
        if (everything) {
            ArrayList<T> r = new ArrayList<T>();
            r.addAll((Collection<? extends T>) of.getParents());
            r.addAll(of.getChildren());
            return r;
        }
        if (!backwards) return (List<T>) of.getParents();
        return of.getChildren();
    }

    public
    List<T> getChildrenOf(T of) {
        if (everything) {
            ArrayList<T> r = new ArrayList<T>();
            r.addAll((Collection<? extends T>) of.getParents());
            r.addAll(of.getChildren());
            return r;
        }
        if (!backwards) return of.getChildren();
        return (List<T>) of.getParents();
    }

    public
    List<T> getAll() {
        return null;
    }
}
