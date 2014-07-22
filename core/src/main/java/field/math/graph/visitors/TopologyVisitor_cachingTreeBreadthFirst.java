package field.math.graph.visitors;

import field.math.graph.iTopology;
import field.namespace.generic.tuple.Pair;
import field.util.HashMapOfLists;

import java.util.*;

/**
* Created by jason on 7/15/14.
*/
public abstract class TopologyVisitor_cachingTreeBreadthFirst<T> {

    public HashMapOfLists<T, Pair<T, List<T>>> knownPaths = new HashMapOfLists<T, Pair<T, List<T>>>();

    private final iTopology<T> t;

    HashMap<T, T> parented = new HashMap<T, T>();

    int maxDepth = -1;

    HashSet<T> currentFringe = new LinkedHashSet<T>();

    HashSet<T> nextFringe = new LinkedHashSet<T>();

    public TopologyVisitor_cachingTreeBreadthFirst(iTopology<T> t) {
        this.t = t;
    }

    public void apply(Collection<T> root) {
        currentFringe.clear();
        nextFringe.clear();
        parented.clear();

        for (T t : root)
            parented.put(t, null);

        for (T x : root) {
            List<T> children = t.getChildrenOf(x);
            currentFringe.clear();
            currentFringe.addAll(children);
            for (T c : currentFringe) {
                parented.put(c, x);
            }
        }

        _apply(null);
    }

    public void apply(T root) {
        currentFringe.clear();
        nextFringe.clear();
        parented.clear();

        parented.put(root, null);

        List<T> children = t.getChildrenOf(root);
        currentFringe.clear();
        currentFringe.addAll(children);
        for (T c : currentFringe) {
            parented.put(c, root);
        }
        _apply(root);

    }

    public void copyCache(TopologyVisitor_cachingTreeBreadthFirst<T> ls2) {
        ls2.knownPaths.putAll(knownPaths);
    }

    public List<T> getPath(T to) {
        List<T> r = new ArrayList<T>();
        r.add(to);
        T p = parented.get(to);
        while (p != null && r.size() < 20) {
            r.add(p);
            T op = parented.get(p);
            if (p == op)
                break;
            p = op;
        }
        if (r.size() == 20) {
            System.err.println(" warning, self parenting path ? ");
        }
        return r;
    }

    public TopologyVisitor_cachingTreeBreadthFirst<T> setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    private void _apply(T root) {

        int m = 0;
        do {
            m++;
            nextFringe.clear();

            for (T c : currentFringe) {
                synchronized (this.getClass()) {
                    GraphNodeSearching.VisitCode code = visit(c);

                    if (code == GraphNodeSearching.VisitCode.stop) {
                        return;
                    }
                    if (code == GraphNodeSearching.VisitCode.skip) {
                    } else {
                        List<T> l = t.getChildrenOf(c);
                        for (T cc : l) {
                            if (!parented.containsKey(cc)) {
                                parented.put(cc, c);
                                nextFringe.add(cc);
                            }
                        }
                    }
                }
            }

            HashSet<T> tmp = currentFringe;
            currentFringe = nextFringe;
            nextFringe = tmp;

            visitFringe(nextFringe);

        } while (currentFringe.size() > 0 && (maxDepth == -1 || m < maxDepth));
    }

    protected List<T> getCachedPath(T from, T to) {
        Collection<Pair<T, List<T>>> q = knownPaths.get(from);
        if (q == null)
            return null;
        for (Pair<T, List<T>> p : q) {
            if (p.left.equals(to)) {
                return p.right;
            }
        }
        return null;
    }

    protected void markPathAsCached(List<T> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            knownPaths.addToList(path.get(i), new Pair<T, List<T>>(path.get(path.size() - 1), path.subList(i + 1, path.size())));
        }
    }

    abstract protected GraphNodeSearching.VisitCode visit(T c);

    protected void visitFringe(HashSet<T> nextFringe) {
    }

}
