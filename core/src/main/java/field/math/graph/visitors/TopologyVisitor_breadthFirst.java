package field.math.graph.visitors;

import field.math.graph.iTopology;

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
    void apply(iTopology<T> top, T root) {
        seen.clear();
        fringe.clear();
        _apply(top, root, fringe, fringe2);
    }

    public
    void preSee(Collection<T> seen2) {
        seen.addAll(seen2);
    }

    private
    void _apply(iTopology<T> top, T root, LinkedHashSet<T> localFringe, LinkedHashSet<T> tempFringe) {
        GraphNodeSearching.VisitCode code = visit(root);
        if (code == GraphNodeSearching.VisitCode.stop) return;
        if (code == GraphNodeSearching.VisitCode.skip) {
            return;
        }

        List<T> c = top.getChildrenOf(root);
        fringe.addAll(c);

        visitFringe(fringe);
        while (!fringe.isEmpty()) {
            for (T t : maybeWrap(fringe)) {
                if (!avoidLoops || !seen.contains(t)) {
                    GraphNodeSearching.VisitCode vc = visit(t);
                    if (vc == GraphNodeSearching.VisitCode.stop) return;
                    if (vc == GraphNodeSearching.VisitCode.skip) {
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
    GraphNodeSearching.VisitCode visit(T root);

    protected
    void visitFringe(Collection<T> fringe) {
    }
}
