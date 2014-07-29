package field.math.graph.visitors;

import field.math.graph.IGraphNode;
import field.math.graph.SimpleNode;
import field.math.graph.visitors.hint.SkipMultiple;
import field.math.graph.visitors.hint.SkipMultipleBut;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;

import java.util.*;

public
class GraphNodeSearching {

    public abstract static
    class GraphNodeVisitor_depthFirst<T extends IGraphNode> {
        private boolean avoidLoops;

        protected HashSet<T> seen = new HashSet<T>();

        protected Stack<T> stack = new Stack<T>();

        private boolean reverse = false;

        public
        GraphNodeVisitor_depthFirst(boolean avoidLoops) {
            this.avoidLoops = avoidLoops;
        }

        public
        GraphNodeVisitor_depthFirst(boolean avoidLoops, boolean reverse) {
            this.avoidLoops = avoidLoops;
            this.reverse = reverse;
        }

        boolean clearSeenOnExit = true;

        int maxDepth = 3000;

        public
        GraphNodeVisitor_depthFirst<T> setClearSeenOnExit(boolean clearSeenOnExit) {
            this.clearSeenOnExit = clearSeenOnExit;
            return this;
        }

        public
        void apply(T root) {
            depth = 0;
            enter(root);
            stack.push(root);
            seen.add(root);

            TraversalHint code = visit(root);
            if (code == StandardTraversalHint.STOP) return;
            if (code == StandardTraversalHint.SKIP) {
                stack.pop();
                exit(root);
                return;
            }
            if (code instanceof SkipMultiple) {
                seen.addAll(((SkipMultiple) code).c);
                avoidLoops = true;
            }
            if (code instanceof SkipMultipleBut) {
                seen.addAll(((SkipMultipleBut) code).c);
                seen.remove(((SkipMultipleBut) code).o);
                avoidLoops = true;
            }
            List<T> c = root.getChildren();
            _apply(c);
            stack.pop();
            if (clearSeenOnExit) seen.clear();
            exit(root);
        }

        public
        boolean hasSeen(T a) {
            return seen.contains(a);
        }

        protected
        void enter(T root) {
        }

        protected
        void exit(T root) {
        }

        int depth = 0;

        protected
        TraversalHint _apply(List<T> c) {
            depth++;
            if (depth > maxDepth * 2) {
                //System.out.println(" (( warning, max depth exceeded in search ))");
                return StandardTraversalHint.STOP;
            }

            ListIterator<T> li = c.listIterator(reverse ? c.size() : 0);
            // for (T n : c)
            while (reverse ? li.hasPrevious() : li.hasNext()) {
                T n = reverse ? li.previous() : li.next();
                if (!avoidLoops || !seen.contains(n)) {
                    if (avoidLoops) seen.add(n);
                    enter(n);
                    stack.push(n);
                    TraversalHint code = visit(n);
                    if (code == StandardTraversalHint.STOP) return StandardTraversalHint.STOP;
                    if (code instanceof SkipMultiple) {
                        seen.addAll(((SkipMultiple) code).c);
                        avoidLoops = true;
                    }
                    else if (code instanceof SkipMultipleBut) {
                        seen.addAll(((SkipMultipleBut) code).c);
                        seen.remove(((SkipMultipleBut) code).o);
                        avoidLoops = true;
                    }
                    if (code != StandardTraversalHint.SKIP) {
                        TraversalHint vc = _apply(n.getChildren());
                        if (vc == StandardTraversalHint.STOP) return StandardTraversalHint.STOP;
                    }
                    stack.pop();
                    exit(n);
                }
            }
            depth--;
            return StandardTraversalHint.CONTINUE;
        }

        protected abstract
        TraversalHint visit(T n);

        protected static
        String spaces(int n) {
            StringBuilder buf = new StringBuilder(n);
            for (int i = 0; i < n; i++)
                buf.append(' ');
            return buf.toString();
        }
    }

    public static
    SimpleNode<String> makeRandomTree(int numNodes, final float factor) {
        SimpleNode<String> root = new SimpleNode<String>().setPayload("root");
        ArrayList<SimpleNode<String>> nodes = new ArrayList<SimpleNode<String>>();
        nodes.add(root);
        final SimpleNode<String>[] ref = new SimpleNode[1];
        class RandomSearch extends GraphNodeVisitor_depthFirst<SimpleNode<String>> {
            public
            RandomSearch() {
                super(false);
            }

            protected
            TraversalHint visit(SimpleNode<String> n) {
                if (Math.random() < factor) {
                    ref[0] = n;
                    return StandardTraversalHint.STOP;
                }
                return StandardTraversalHint.CONTINUE;
            }
        }
        RandomSearch rs = new RandomSearch();
        int c = 0;
        while (nodes.size() < numNodes) {
            rs.apply(root);
            if (ref[0] != null) {
                SimpleNode<String> nn =
                        new SimpleNode<String>().setPayload("new node <" + (c++) + "> =" + c + "> = somethingelse");
                ref[0].addChild(nn);
                nodes.add(nn);
            }
        }
        return root;
    }

    public static
    <T extends IGraphNode<T>> List<T> findPath_avoidingLoops(T from, final T to) {
        final ArrayList<T> path = new ArrayList<T>();
        if (from == to) {
            path.add(from);
            return path;
        }
        new GraphNodeVisitor_depthFirst<T>(true) {
            @Override
            protected
            TraversalHint visit(T n) {
                TraversalHint vc = n == to ? StandardTraversalHint.STOP : StandardTraversalHint.CONTINUE;
                if (vc == StandardTraversalHint.STOP) path.addAll(stack);
                return vc;
            }
        }.apply(from);
        if (path.size() == 0) return null;
        return path.size() > 0 ? path : null;
    }

}
