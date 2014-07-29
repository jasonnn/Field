package field.math.graph;

import field.namespace.generic.ReflectionTools;

import java.util.*;


public abstract
class GraphNodesForTopologyView<T> implements IMutableTopology<T> {

    HashMap<T, IMutable> forward = new HashMap<T, IMutable>();
    HashMap<IMutable, T> backward = new HashMap<IMutable, T>();

    LinkedHashSet<IMutableTopology<? super T>> notes = new LinkedHashSet<IMutableTopology<? super T>>();

    protected abstract
    IMutable newGraphNode(T from);

    abstract
    boolean removeGraphNode(IMutable fromP, T from);

    public
    Set<IMutable> getAllNodes() {
        return backward.keySet();
    }

    public
    void begin() {
        ReflectionTools.apply(notes, IMutableTopology.method_begin);
    }

    public
    void end() {
        ReflectionTools.apply(notes, IMutableTopology.method_begin);
    }

    public
    void addChild(T from, T to) {
        if (!forward.containsKey(from)) install(from);
        if (!forward.containsKey(to)) install(to);

        forward.get(from).addChild(forward.get(to));

        ReflectionTools.apply(notes, IMutableTopology.method_addChild, from, to);
    }

    protected
    IMutable install(T from) {
        IMutable node = newGraphNode(from);
        forward.put(from, node);
        backward.put(node, from);
        return node;
    }


    public
    void removeChild(T from, T to) {
        IMutable fromP = forward.get(from);
        IMutable toP = forward.get(to);
        fromP.removeChild(toP);
        if (fromP.getParents().size() == 0) if (removeGraphNode(fromP, from)) backward.remove(forward.remove(from));
        if (toP.getParents().size() == 0) if (removeGraphNode(toP, to)) backward.remove(forward.remove(toP));
        ReflectionTools.apply(notes, IMutableTopology.method_removeChild, from, to);
    }

    public
    void registerNotify(IMutableTopology<? super T> here) {
        notes.add(here);
    }

    public
    void deregisterNotify(IMutableTopology<? super T> here) {
        notes.remove(here);
    }

    public
    List<T> getParentsOf(T of) {
        IMutable fromP = forward.get(of);
        if (fromP == null) fromP = install(of);
        List<IMutable> parents = fromP.getParents();
        if (parents.size() == 0) return Collections.EMPTY_LIST;
        ArrayList<T> r = new ArrayList<T>(parents.size());
        for (IMutable m : parents)
            r.add(backward.get(m));
        return r;
    }

    public
    List<T> getChildrenOf(T of) {
        IMutable fromP = forward.get(of);
        if (fromP == null) fromP = install(of);
        List<IMutable> parents = fromP.getChildren();
        if (parents.size() == 0) return Collections.EMPTY_LIST;
        ArrayList<T> r = new ArrayList<T>(parents.size());
        for (IMutable m : parents)
            r.add(backward.get(m));
        return r;
    }
}
