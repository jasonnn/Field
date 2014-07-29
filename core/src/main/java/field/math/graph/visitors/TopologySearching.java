package field.math.graph.visitors;

import field.math.BaseMath;
import field.math.BaseMath.MutableFloat;
import field.math.graph.ITopology;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.util.collect.tuple.Pair;

import java.util.*;

public
class TopologySearching {

    public
    interface AStarMetric<T> {

        public
        double distance(T from, T to);
    }

    public abstract static
    class PathCache<T> {
        public
        class KnownPath {
            List<T> path;

            float distance;
        }

        HashMap<Pair<T, T>, KnownPath> cache = new HashMap<Pair<T, T>, KnownPath>();

        Pair<T, T> tmp = new Pair<T, T>(null, null);

        public
        void declarePath(T from, T to, List<T> isPath, float totalDistance) {
            if (isPath.isEmpty()) {
                isPath = new ArrayList<T>();
                isPath.add(from);
                isPath.add(to);
            }
            else if (isPath.get(0) != from) {
                isPath = new ArrayList<T>(isPath);
                isPath.add(0, from);
            }
            if (isPath.get(isPath.size() - 1) != to) {
                isPath = new ArrayList<T>(isPath);
                isPath.add(to);
            }

            float d = totalDistance;
            for (int i = 0; i < (isPath.size() - 1); i++) {
                from = isPath.get(i);
                Pair<T, T> q = new Pair<T, T>(from, to);
                KnownPath qd = cache.get(q);
                if ((qd == null) || (qd.distance > d)) {
                    KnownPath kp = new KnownPath();
                    kp.path = isPath.subList(i, isPath.size());
                    kp.distance = d;
                    cache.put(q, kp);
                }
                d -= distance(isPath.get(i), isPath.get(i + 1));
            }

        }

        public
        KnownPath getPath(T from, T to) {
            tmp.left = from;
            tmp.right = to;
            KnownPath ll = cache.get(tmp);
            return ll;
        }

        protected abstract
        float distance(T t, T t2);

    }

    public static
    class TopologyAStarSearch<T> {
        private final ITopology<T> topology;

        private final AStarMetric<T> metric;

        TreeElement<T> treeRoot;

        HashMap<T, TreeElement<T>> treeElements;

        T goalNode;

        HashSet<T> avoid = new HashSet<T>();

        public
        TopologyAStarSearch(ITopology<T> topology, AStarMetric<T> metric) {
            this.topology = topology;
            this.metric = metric;
        }

        public
        double calcF(T N, AStarMetric<T> metric) {
            double F = calcG(N) + calcH(N);
            return F;
        }

        public
        double calcG(T N) {
            double dist = 0;
            TreeElement<T> at = treeElements.get(N);
            while (at.parent != null) {
                dist += metric.distance(at.parent.pointer, at.pointer);
                at = at.parent;
            }
            return dist;
        }

        public
        double calcH(T N) {
            double H = metric.distance(N, goalNode);
            return H;
        }

        public
        double calcK(T N, T Nprime) {
            return metric.distance(N, Nprime);
        }

        public
        TopologyAStarSearch<T> preSee(Collection<T> t) {
            avoid.addAll(t);
            return this;
        }

        public
        List<T> search(T from, T to) {

            avoid.remove(to);

            if (from.equals(to)) {
                if (!topology.getChildrenOf(from).contains(from)) return searchNotIncluding(from, to);
            }
            goalNode = to;

            HashSet<T> visited = new HashSet<T>();

            TreeMap<MutableFloat, T> open = new TreeMap<MutableFloat, T>(new Comparator<MutableFloat>() {
                public
                int compare(MutableFloat o1, MutableFloat o2) {
                    if (o1.equals(o2)) return 0;
                    int c = Float.compare(o1.floatValue(), o2.floatValue());
                    return (c == 0) ? ((System.identityHashCode(o1) < System.identityHashCode(o2)) ? -1 : 1) : c;
                }
            });
            HashMap<T, MutableFloat> openBackwards = new HashMap<T, MutableFloat>();

            treeRoot = new TreeElement<T>(from, null);
            treeElements = new HashMap<T, TreeElement<T>>();
            treeElements.put(from, treeRoot);

            double f = calcF(from, metric);
            BaseMath.MutableFloat F = new BaseMath.MutableFloat((float) f);
            open.put(F, from);
            openBackwards.put(from, F);

            visited.add(from);
            T N = null;
            while (!open.isEmpty()) {
                N = open.get(open.firstKey());
                openBackwards.remove(open.remove(open.firstKey())); // hmm...

                if (N.equals(to)) {
                    break;
                }

                for (T Nprime : topology.getChildrenOf(N)) {
                    if (!avoid.contains(Nprime)) {

                        if (visited.contains(Nprime)) {
                            double gOfNprime = calcG(Nprime);
                            double gOfN = calcG(N);
                            double kOfNNprime = calcK(N, Nprime);

                            if (gOfNprime > (gOfN + kOfNNprime)) {
                                TreeElement<T> te = treeElements.get(Nprime);
                                te.parent = treeElements.get(N);

                                if (openBackwards.containsKey(Nprime)) {
                                    Object value = openBackwards.remove(Nprime);
                                    open.remove(value);
                                }
                                double fNprime = calcF(Nprime, metric);
                                MutableFloat FNprime = new MutableFloat((float) fNprime);
                                open.put(FNprime, Nprime);
                                openBackwards.put(Nprime, FNprime);
                            }
                        }
                        else {
                            treeElements.put(Nprime, new TreeElement<T>(Nprime, treeElements.get(N)));

                            int oldLength = open.size();

                            f = calcF(Nprime, metric);
                            MutableFloat FNprime = new MutableFloat((float) f);
                            Object o = open.put(FNprime, Nprime);
                            openBackwards.put(Nprime, FNprime);

                            int newLength = open.size();

                            assert newLength > oldLength;

                            visited.add(Nprime);
                        }
                    }
                }
            }

            if (N.equals(to)) // change
            // from
            // document
            {
                // construct
                // path
                Vector<T> path = new Vector<T>();
                path.add(N);
                TreeElement<T> at = treeElements.get(to);
                while (at.pointer != from) {
                    at = at.parent;
                    path.add(at.pointer);
                }
                return path;
            }
            return null;
        }

        public
        float sumDistance(List<T> r) {
            float total = 0;
            T last = null;
            for (T t : r) {
                if (last != null) total += metric.distance(last, t);
                last = t;
            }
            return total;
        }

        /**
         * @param from
         * @param metric
         * @param to
         * @return
         */
        private
        List<T> searchNotIncluding(T from, T to) {
            List<T> best = null;
            float bestDistance = Float.POSITIVE_INFINITY;

            for (T newFrom : topology.getChildrenOf(from)) {
                List<T> r = search(newFrom, to);
                if (r != null) {
                    float d = sumDistance(r);
                    if (d < bestDistance) {
                        bestDistance = d;
                        best = r;
                    }
                }
            }
            if (best != null) {
                best.add(from);
            }
            return best;
        }

    }

    public abstract static
    class TopologyVisitor_directedBreadthFirst<T> implements Comparator<T> {
        private final boolean avoidLoops;

        HashSet<T> seen = new HashSet<T>();

        LinkedHashSet<T> fringe = new LinkedHashSet<T>();

        LinkedHashSet<T> fringe2 = new LinkedHashSet<T>();

        public
        TopologyVisitor_directedBreadthFirst(boolean avoidLoops) {
            this.avoidLoops = avoidLoops;
        }

        public
        void apply(ITopology<T> top, T root) {
            seen.clear();
            _apply(top, root, fringe, fringe2);
        }

        public abstract
        int compare(T o1, T o2);

        public
        void preSee(Collection<T> a) {
            seen.addAll(a);
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

            epoch();

            while (!fringe.isEmpty()) {
                for (T t : fringe) {
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
                fringe2 = t;
                fringe2.clear();

                ArrayList<T> aa = new ArrayList<T>(fringe);
                sort(aa);
                fringe.clear();
                fringe.addAll(aa);

                epoch();
            }
        }

        protected
        void epoch() {
        }

        protected
        void sort(ArrayList<T> aa) {
            Collections.sort(aa, this);
        }

        protected abstract
        TraversalHint visit(T root);
    }

    public abstract static
    class TopologyVisitor_directedDepthFirst<T> implements Comparator<T> {
        public HashSet<T> seen = new HashSet<T>();

        private final boolean avoidLoops;

        private final ITopology<T> topology;

        protected Stack<T> stack = new Stack<T>();

        public
        TopologyVisitor_directedDepthFirst(boolean avoidLoops, ITopology<T> topology) {
            this.avoidLoops = avoidLoops;
            this.topology = topology;
        }

        public
        void apply(T root) {
            stack.push(root);
            TraversalHint code = visit(root);
            if (code == StandardTraversalHint.STOP) return;
            if (code == StandardTraversalHint.SKIP) {
                stack.pop();
                return;
            }
            List<T> c = topology.getChildrenOf(root);
            _apply(c);
            stack.pop();
            seen.clear();
        }

        public abstract
        int compare(T o1, T o2);

        public
        void preSee(Set<T> s) {
            seen.addAll(s);
        }

        protected
        TraversalHint _apply(List<T> c) {
            ArrayList<T> cs = new ArrayList<T>(c);
            Collections.sort(cs, this);
            for (T n : cs) {
                if (!avoidLoops || !seen.contains(n)) {
                    if (avoidLoops) seen.add(n);
                    stack.push(n);
                    TraversalHint code = visit(n);
                    if (code == StandardTraversalHint.STOP) return StandardTraversalHint.STOP;
                    if (code != StandardTraversalHint.SKIP) {
                        TraversalHint vc = _apply(topology.getChildrenOf(n));
                        if (vc == StandardTraversalHint.STOP) return StandardTraversalHint.STOP;
                    }
                    exit(stack.pop());
                }
            }
            return StandardTraversalHint.CONTINUE;
        }

        protected
        void exit(T t) {
        }

        protected static
        String spaces(int n) {
            StringBuilder buf = new StringBuilder(n);
            for (int i = 0; i < n; i++)
                buf.append(' ');
            return buf.toString();
        }

        protected abstract
        TraversalHint visit(T n);
    }

    public abstract static
    class TopologyVisitor_longDirectedBreadthFirst<T> {

        public
        class Key implements Comparable<Key> {
            public List<T> path;

            public float accumulatedDistance;

            public
            int compareTo(Key o) {
                return -Float.compare(accumulatedDistance, o.accumulatedDistance);
            }

            @Override
            public
            String toString() {
                return "<<" + path + "> = " + accumulatedDistance + ">";
            }

        }

        protected PathCache<T>.KnownPath doShortPath = null;

        HashMap<T, Float> seen = new HashMap<T, Float>();

        List<Key> fringe = new ArrayList<Key>();

        int maxPop = Integer.MAX_VALUE;

        float maxDistance = Float.POSITIVE_INFINITY;

        public
        TopologyVisitor_longDirectedBreadthFirst<T> setMaxDistance(float maxDistance) {
            this.maxDistance = maxDistance;
            return this;
        }

        protected ITopology<T> top;

        public
        TopologyVisitor_longDirectedBreadthFirst() {
        }

        public
        void apply(ITopology<T> top, List<T> name) {
            seen.clear();
            this.top = top;
            _apply(top, name);
        }

        public
        void apply(ITopology<T> top, T name) {
            seen.clear();
            this.top = top;
            _apply(top, Collections.singletonList(name));
        }

        public
        void clear() {
            seen.clear();
            fringe.clear();
            doShortPath = null;
        }

        public
        TopologyVisitor_longDirectedBreadthFirst<T> setMaxPop(int maxPop) {
            this.maxPop = maxPop;
            return this;
        }

        private
        void _apply(ITopology<T> top, List<T> lroot) {
            TraversalHint code;
            fringe.clear();
            for (T root : lroot) {
                Key rootK = new Key();
                rootK.path = new ArrayList<T>(1);
                rootK.path.add(root);
                rootK.accumulatedDistance = 0;
                seen.put(root, 0f);
                code = visit(rootK);

                if (code == StandardTraversalHint.STOP) return;
                if (code == StandardTraversalHint.SKIP) {
                }
                else fringe.add(rootK);
            }
            while (!fringe.isEmpty()) {
                Key k = fringe.remove(fringe.size() - 1);

                T r = k.path.get(k.path.size() - 1);

                code = visit(k);
                if (code == StandardTraversalHint.STOP) return;
                if (code == StandardTraversalHint.SKIP) {
                    if (doShortPath != null) {
                        float ad = doShortPath.distance;
                        T end = doShortPath.path.get(doShortPath.path.size() - 1);
                        Float q = seen.get(end);
                        if ((q == null) || ((ad + k.accumulatedDistance) < q)) {
                            Key k2 = new Key();
                            k2.path = new ArrayList<T>(k.path.size() + doShortPath.path.size());
                            k2.path.addAll(k.path);
                            k2.path.addAll(doShortPath.path.subList(1, doShortPath.path.size()));
                            k2.accumulatedDistance = ad + k.accumulatedDistance;
                            seen.put(end, ad + k.accumulatedDistance);
                            assert doShortPath.path.get(0) == r;
                            if (k2.accumulatedDistance < maxDistance) insert(fringe, k2);
                        }
                    }
                    doShortPath = null;
                    continue;
                }
                List<T> c = top.getChildrenOf(r);

                for (T t : c) {
                    float d = distance(r, t);
                    Float q = seen.get(t);
                    if ((q == null) || ((d + k.accumulatedDistance) < q)) {
                        Key k2 = new Key();
                        k2.path = new ArrayList<T>(k.path.size() + 1);
                        k2.path.addAll(k.path);
                        k2.path.add(t);
                        k2.accumulatedDistance = d + k.accumulatedDistance;
                        seen.put(t, d + k.accumulatedDistance);
                        if (k2.accumulatedDistance < maxDistance) insert(fringe, k2);
                    }
                }
                if (fringe.size() > maxPop) {
                    fringe = new ArrayList<Key>(fringe.subList(0, maxPop));
                }

            }
        }

        private
        void insert(List<Key> in, Key k) {
            int q = Collections.binarySearch(in, k);
            if (q < 0) {
                in.add(-q - 1, k);
            }
            else in.add(q, k);
            assert invarients();
        }

        private
        boolean invarients() {
            for (int i = 1; i < fringe.size(); i++) {
                if (fringe.get(i).accumulatedDistance > fringe.get(i - 1).accumulatedDistance) {
                    throw new Error(String.valueOf(fringe));
                }
            }
            return true;
        }

        protected abstract
        float distance(T root, T t);

        protected abstract
        TraversalHint visit(Key k);
    }

    public abstract static
    class TopologyVisitor_treeBreadthFirst<T> {
        protected final ITopology<T> t;

        protected HashMap<T, T> parented = new HashMap<T, T>();

        int maxDepth = -1;

        HashSet<T> currentFringe = new LinkedHashSet<T>();

        HashSet<T> nextFringe = new LinkedHashSet<T>();

        boolean all = false;

        public
        TopologyVisitor_treeBreadthFirst(ITopology<T> t) {
            this.t = t;
        }

        public
        void apply(Collection<T> root) {
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

        public
        void apply(T root) {
            TraversalHint r = visit(root);
            if (r == StandardTraversalHint.CONTINUE) {
                parented.put(root, null);

                List<T> children = t.getChildrenOf(root);
                currentFringe.clear();
                currentFringe.addAll(children);
                for (T c : currentFringe) {
                    parented.put(c, root);
                }
                _apply(root);
            }
        }

        public
        List<T> getPath(T to) {
            List<T> r = new ArrayList<T>();
            r.add(to);
            T p = parented.get(to);
            while (p != null) {
                r.add(p);
                T np = parented.get(p);
                if (np == p) {
                    System.err.println(" warning: self parenting path ? ");
                    break;
                }
                p = np;
            }
            // if (r.size() == 20) {
            // System.err.println(" warning,
            // self parenting path ? ");
            // }
            return r;
        }

        public
        TopologyVisitor_treeBreadthFirst<T> setMaxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        private
        void _apply(T root) {

            int m = 0;
            do {
                m++;
                nextFringe.clear();

                for (T c : currentFringe) {

                    TraversalHint code = visit(c);

                    if (code == StandardTraversalHint.STOP) return;
                    if (code == StandardTraversalHint.SKIP) {
                    }
                    else {
                        List<T> l = t.getChildrenOf(c);
                        for (T cc : l) {
                            if (!parented.containsKey(cc) || all) {
                                parented.put(cc, c);
                                nextFringe.add(cc);
                            }
                        }
                    }
                }

                HashSet<T> tmp = currentFringe;
                currentFringe = nextFringe;
                nextFringe = tmp;

                visitFringe(currentFringe);

            } while (!currentFringe.isEmpty() && ((maxDepth == -1) || (m < maxDepth)));
            //System.out.println(" exhaused fringe ");
        }

        protected abstract
        TraversalHint visit(T c);

        protected
        void visitFringe(HashSet<T> nextFringe) {
        }
    }

    public abstract static
    class TopologyVisitory_depthFirst<T> {
        private final boolean avoidLoops;

        private final ITopology<T> topology;

        protected HashSet<T> seen = new HashSet<T>();

        protected Stack<T> stack = new Stack<T>();

        boolean setCleanOnExit = true;

        public
        TopologyVisitory_depthFirst(boolean avoidLoops, ITopology<T> topology) {
            this.avoidLoops = avoidLoops;
            this.topology = topology;
        }

        public
        void apply(T root) {
            stack.push(root);
            TraversalHint code = visit(root);
            if (code == StandardTraversalHint.STOP) return;
            if (code == StandardTraversalHint.SKIP) {
                stack.pop();
                return;
            }
            List<T> c = topology.getChildrenOf(root);
            _apply(c);
            stack.pop();
            if (setCleanOnExit) seen.clear();
        }

        public
        boolean hasSeen(T t) {
            return seen.contains(t);
        }

        public
        TopologyVisitory_depthFirst<T> setSetCleanOnExit(boolean setCleanOnExit) {
            this.setCleanOnExit = setCleanOnExit;
            return this;
        }

        protected
        TraversalHint _apply(List<T> c) {
            for (T n : c) {
                if (!avoidLoops || !seen.contains(n)) {
                    if (avoidLoops) seen.add(n);
                    stack.push(n);
                    TraversalHint code = visit(n);
                    if (code == StandardTraversalHint.STOP) return StandardTraversalHint.STOP;
                    if (code != StandardTraversalHint.SKIP) {
                        TraversalHint vc = _apply(topology.getChildrenOf(n));
                        if (vc == StandardTraversalHint.STOP) return StandardTraversalHint.STOP;
                    }
                    exit(stack.pop());
                }
            }
            return StandardTraversalHint.CONTINUE;
        }

        protected
        void exit(T t) {
        }

        protected
        String spaces(int n) {
            StringBuilder buf = new StringBuilder(n);
            for (int i = 0; i < n; i++)
                buf.append(' ');
            return buf.toString();
        }

        protected abstract
        TraversalHint visit(T n);
    }

    public static
    class TreeElement<T> {
        public T pointer;

        public TreeElement<T> parent;

        public
        TreeElement(T pointer, TreeElement<T> parent) {
            this.pointer = pointer;
            this.parent = parent;
        }

        @Override
        public
        String toString() {
            return pointer + " ";
        }
    }

    public static
    <T> List<T> allBelow(T root, ITopology<T> topology) {
        final ArrayList<T> r = new ArrayList<T>();
        new TopologyVisitory_depthFirst<T>(true, topology) {
            @Override
            protected
            TraversalHint visit(T n) {
                r.add(n);
                return StandardTraversalHint.CONTINUE;
            }
        }.apply(root);
        return r;
    }
}
