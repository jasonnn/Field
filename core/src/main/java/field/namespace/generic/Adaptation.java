package field.namespace.generic;

import field.math.abstraction.IFloatProvider;
import field.math.graph.ITopology;
import field.math.graph.visitors.TopologySearching;
import field.math.graph.visitors.TopologySearching.AStarMetric;
import field.math.graph.visitors.TopologySearching.TopologyAStarSearch;
import field.util.collect.tuple.Pair;
import field.util.collect.tuple.Triple;

import java.util.*;


/**
 * @author marc Created on Dec 21, 2003
 */
public
class Adaptation {

    public
    interface iAdaptor<X, Y> {
        public
        Y adapt(Class<X> from, Class<Y> to, X object);
    }


    public static
    class Node {

        Class represents;

        LinkedHashMap<Class, Triple<Node, IFloatProvider, iAdaptor>> edges =
                new LinkedHashMap<Class, Triple<Node, IFloatProvider, iAdaptor>>();

        public
        Node(Class from) {
            this.represents = from;
        }

        @Override
        public
        String toString() {
            return String.valueOf(represents);
        }
    }

    static boolean debug = false;

    HashMap<Class, Node> nodes = new HashMap<Class, Node>();

    HashMap<Pair<Class, Class>, List<Triple<Node, IFloatProvider, iAdaptor>>> cached;

    ITopology<Node> nodeTopology = new ITopology<Node>() {
        public
        List<Node> getChildrenOf(Node of) {
            ArrayList<Node> al = new ArrayList<Node>(of.edges.size());
            for (Triple<Node, IFloatProvider, iAdaptor> t : of.edges.values()) {
                al.add(t.left);
            }
            return al;
        }

        public
        List<Node> getParentsOf(Node of) {
            return null;
        }
    };

    AStarMetric<Node> nodeMetric = new AStarMetric<Node>() {
        public
        double distance(Node from, Node to) {
            Triple<Node, IFloatProvider, iAdaptor> n = from.edges.get(to.represents);
            if (from.represents.equals(to.represents)) return 0;
            if (n == null) return 1e4;
            return n.middle.evaluate();
        }
    };

    public
    Adaptation() {
    }

    public
    <T> T adapt(Object from, Class<T> to) {
        if (from == null) {
            return null;
        }

        // find goal node
        Node goal = nodes.get(to);
        if (goal == null) {
            return null;
        }

        // find start node;
        Class c = from.getClass();

        List<Triple<Node, IFloatProvider, iAdaptor>> toCache = null;

        if (cached != null) {
            Pair<Class, Class> find = new Pair<Class, Class>(c, to);
            List<Triple<Node, IFloatProvider, iAdaptor>> found = cached.get(find);

            toCache = found;
        }

        if (toCache == null) {

            LinkedHashSet<Class> interfaces = new LinkedHashSet<Class>();
            LinkedHashSet<Node> startingPlaces = new LinkedHashSet<Node>();
            while (c != null) {
                Node n = nodes.get(c);
                if (n != null) {
                    startingPlaces.add(n);
                }
                interfaces.addAll(Arrays.asList(c.getInterfaces()));
                if (c == to) {
                    return (T) from;
                }
                c = c.getSuperclass();
            }

            for (Class i : interfaces) {
                Node n = nodes.get(i);
                if (n != null) {
                    startingPlaces.add(n);
                }
                if (i == to) {
                    return (T) from;
                }
            }

            if (startingPlaces.isEmpty()) {
                return null;
            }

            TopologyAStarSearch<Node> search =
                    new TopologySearching.TopologyAStarSearch<Node>(nodeTopology, nodeMetric);
            float bestDistance = Float.POSITIVE_INFINITY;
            List<Node> bestRoute = null;

            for (Node s : startingPlaces) {
                List<Node> result = search.search(s, goal);
                if (result != null) {
                    Collections.reverse(result);
                    float z = search.sumDistance(result);
                    if (z < bestDistance) {
                        bestRoute = result;
                        bestDistance = z;
                    }
                }
            }

            if (bestRoute == null) {
                return null;
            }
            toCache = new ArrayList<Triple<Node, IFloatProvider, iAdaptor>>();
            Node root = bestRoute.get(0);
            for (int i = 1; i < bestRoute.size(); i++) {
                Triple<Node, IFloatProvider, iAdaptor> edge = root.edges.get(bestRoute.get(i).represents);
                toCache.add(edge);
                root = edge.left;
            }

            // assuming that astar includes start and end nodes
            if (cached != null) {
                cached.put(new Pair<Class, Class>(from.getClass(), to), toCache);
            }
        }

        Object o = from;
        for (Triple<Node, IFloatProvider, iAdaptor> cc : toCache) {
            o = cc.right.adapt(o.getClass(), cc.left.represents, o);
        }

        return (T) o;

    }

    public
    <X, Y> void declare(Class<X> from, Class<Y> to, iAdaptor<X, Y> adaptor, IFloatProvider f) {
        Node nf = getNodeFor(from, true);
        Node nt = getNodeFor(to, true);

        nf.edges.put(to, new Triple<Node, IFloatProvider, iAdaptor>(nt, f, adaptor));
    }

    public
    Adaptation doCaching() {
        cached = new HashMap<Pair<Class, Class>, List<Triple<Node, IFloatProvider, iAdaptor>>>();
        return this;
    }

    protected
    Node getNodeFor(Class from, boolean autoConstruct) {
        Node node = nodes.get(from);
        if (node == null) nodes.put(from, node = new Node(from));
        return node;
    }

}