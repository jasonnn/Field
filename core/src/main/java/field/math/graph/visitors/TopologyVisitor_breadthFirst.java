package field.math.graph.visitors;

import field.math.graph.ITopology;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by jason on 7/15/14.
 */
public abstract
class TopologyVisitor_breadthFirst<T> {
    private final boolean avoidLoops;

    HashSet<T> seen = new HashSet<T>();

    LinkedHashSet<T> fringe = new LinkedHashSet<T>();

    LinkedHashSet<T> fringe2 = new LinkedHashSet<T>();

    public
    TopologyVisitor_breadthFirst(boolean avoidLoops) {
        this.avoidLoops = avoidLoops;
    }

    public
    void apply(ITopology<T> top, T root) {
        seen.clear();
        fringe.clear();
        _apply(top, root, fringe, fringe2);
    }

    public
    void preSee(Collection<T> seen2) {
        seen.addAll(seen2);
    }

    private
    void _apply(ITopology<T> top, T root, LinkedHashSet<T> localFringe, LinkedHashSet<T> tempFringe) {
        TraversalHint code = visit(root);
        if (code == StandardTraversalHint.STOP) return;
        if (code == StandardTraversalHint.SKIP) {
            return;
        }

        List<T> c = top.getChildrenOf(root);
        fringe.addAll(c);

        visitFringe(fringe);
        while (!fringe.isEmpty()) {
            for (T t : maybeWrap(fringe)) {
                if (!avoidLoops || !seen.contains(t)) {
                    TraversalHint vc = visit(t);
                    if (vc == StandardTraversalHint.STOP) return;
                    if (vc == StandardTraversalHint.SKIP) {
                    }
                    else {
                        List<T> childrenOf = top.getChildrenOf(t);
                        fringe2.addAll(childrenOf);
                    }
                    if (avoidLoops) seen.add(t);
                }
            }
            LinkedHashSet<T> t = fringe;
            fringe = fringe2;

            visitFringe(fringe);

            fringe2 = t;
            fringe2.clear();
        }
    }

    protected
    LinkedHashSet<T> maybeWrap(LinkedHashSet<T> f) {
        return f;
    }

    protected abstract
    TraversalHint visit(T root);

    protected
    void visitFringe(Collection<T> fringe) {
    }
}
