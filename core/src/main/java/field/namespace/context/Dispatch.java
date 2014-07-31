package field.namespace.context;

import field.math.graph.ITopology;
import field.math.graph.visitors.hint.TraversalHint;
import field.namespace.dispatch.DispatchOverTopology;
import field.util.ProxyBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *  @param K key type
 * @param I interface type
 */
public
class Dispatch<K, I> {

    final ContextTopology<K, I> topology;

    final ITopology<K> topBackwards = new ITopology<K>() {
        public
        List<K> getChildrenOf(K of) {
            Collection<K> pp = topology.parentsOf(of);
            if (pp != null)
                return new ArrayList<K>(pp);
            else
                return Collections.emptyList();
        }

        public
        List<K> getParentsOf(K of) {
            Collection<K> pp = topology.childrenOf(of);
            if (pp != null)
                return new ArrayList<K>(pp);
            else
                return Collections.emptyList();
        }

    };

    final ITopology<K> top = new ITopology<K>() {
        public
        List<K> getChildrenOf(K of) {
            Collection<K> pp = topology.childrenOf(of);
            if (pp != null)
                return new ArrayList<K>(pp);
            else
                return Collections.emptyList();
        }

        public
        List<K> getParentsOf(K of) {
            Collection<K> pp = topology.parentsOf(of);
            if (pp != null)
                return new ArrayList<K>(pp);
            else
                return Collections.emptyList();
        }
    };

    DispatchOverTopology<K> dispatch = new DispatchOverTopology<K>(top);

    DispatchOverTopology<K> dispatchBackwards = new DispatchOverTopology<K>(topBackwards);

    Method method;

    public
    Dispatch(ContextTopology<K, I> topology) {
        this.topology = topology;
    }

    public
    Collection dispatchBackward(final Method m, Object... args) {
        DispatchOverTopology<K>.Raw rawBackwards = getRawBackwards();
        TraversalHint o = rawBackwards.dispatch(m, topology.getAt(), args);
        return rawBackwards.returns();
    }

    public
    Collection dispatchBackward(K startingFrom, final Method m, Object... args) {
        DispatchOverTopology<K>.Raw rawBackwards = getRawBackwards();
        TraversalHint o = rawBackwards.dispatch(m, startingFrom, args);
        return rawBackwards.returns();
    }

    public
    Collection dispatchBackwardAbove(final K startingFrom, final Method m, Object... args) {
        DispatchOverTopology<K>.Raw rawBackwards = dispatchBackwards.new Raw(true) {
            @Override
            public
            Object getObject(K e) {
                if (e == startingFrom)
                    return null;
                return topology.storage.get(e, method);
            }
        };

        TraversalHint o = rawBackwards.dispatch(m, startingFrom, args);
        return rawBackwards.returns();
    }

    public
    Collection dispatchForward(final Method m, Object... args) {
        DispatchOverTopology<K>.Raw raw = getRaw();
        TraversalHint o = raw.dispatch(m, topology.getAt(), args);
        return raw.returns();
    }

    public
    Collection dispatchForward(K startingFrom, final Method m, Object... args) {
        DispatchOverTopology<K>.Raw raw = getRaw();
        TraversalHint o = raw.dispatch(m, startingFrom, args);
        return raw.returns();
    }

    public
    Collection dispatchForwardAbove(final K startingFrom, final Method m, Object... args) {
        DispatchOverTopology<K>.Raw raw = dispatch.new Raw(true) {
            @Override
            public
            Object getObject(K e) {
                if (e == startingFrom)
                    return null;
                return topology.storage.get(e, method);
            }
        };

        TraversalHint o = raw.dispatch(m, startingFrom, args);
        return raw.returns();
    }

    public
    <T> T getAboveOverrideProxyFor(Class<T> interf) {
        return ProxyBuilder.proxyFor(interf).withHandler(new InvocationHandler() {

            public
            Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
                method = arg1;
                arg1.setAccessible(true);

                final K startingFrom = topology.getAt();
                DispatchOverTopology<K>.Raw raw = dispatch.new Raw(true) {
                    @Override
                    public
                    Object getObject(K e) {
                        if (e == startingFrom)
                            return null;
                        return topology.storage.get(e, method);
                    }
                };
                return raw.dispatch(arg1, topology.getAt(), arg2);
            }
        });

    }

    public
    <T> T getBackwardsOverrideProxyFor(Class<T> interf) {
        return ProxyBuilder.proxyFor(interf).withHandler(new InvocationHandler() {
            public
            Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
                method = arg1;
                arg1.setAccessible(true);
                DispatchOverTopology<K>.Raw rawBackwards = getRawBackwards();
                TraversalHint o = rawBackwards.dispatch(arg1, topology.getAt(), arg2);
                return o;
            }
        });

    }

    // what I really
    // want to right
    // here is
    // I
    // extends T, or
    // T super
    // I,
    // but I can't
    public
    <T> T getBackwardsOverrideProxyFor(final K startAt, Class<T> interf) {
        return ProxyBuilder.proxyFor(interf).withHandler(new InvocationHandler() {

            public
            Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
                method = arg1;
                arg1.setAccessible(true);
                DispatchOverTopology<K>.Raw rawBackwards = getRawBackwards();
                TraversalHint o = rawBackwards.dispatch(arg1, startAt, arg2);
                return o;
            }
        });
    }

    public
    <T> T getOverrideProxyFor(Class<T> interf) {
        return ProxyBuilder.proxyFor(interf).withHandler(new InvocationHandler() {
            public
            Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
                method = arg1;
                arg1.setAccessible(true);
                DispatchOverTopology<K>.Raw raw = getRaw();
                return raw.dispatch(arg1, topology.getAt(), arg2);
            }
        });

    }

    public
    <T> T getOverrideProxyFor(final K startAt, Class<T> interf) {
        return ProxyBuilder.proxyFor(interf).withHandler(new InvocationHandler() {
            @Override
            public
            Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                method.setAccessible(true);
                DispatchOverTopology<K>.Raw raw = getRaw();
                return raw.dispatch(method, startAt, args);
            }
        });


    }

    DispatchOverTopology<K>.Raw getRaw() {
        return dispatch.new Raw(true) {
            @Override
            public
            Object getObject(K e) {
                return topology.storage.get(e, method);
            }
        };
    }

    DispatchOverTopology<K>.Raw getRawBackwards() {
        return dispatchBackwards.new Raw(true) {
            @Override
            public
            Object getObject(K e) {
                return topology.storage.get(e, method);
            }
        };
    }
}
