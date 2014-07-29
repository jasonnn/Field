package field.bytecode.mirror.impl;

import field.bytecode.mirror.IMethodFunction;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.namespace.context.ContextTopology;
import field.namespace.context.Dispatch;
import field.namespace.generic.ReflectionTools;
import field.namespace.generic.IFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
* Created by jason on 7/29/14.
*/
public
class MirrorMethod<t_class, t_returns, t_accepts> extends AbstractMirrorMethod implements IMethodFunction<t_class, t_accepts, t_returns> {
    public
    MirrorMethod(Class on, String name, Class...parameters) {
        super(on, name, parameters);
    }

    public
    MirrorMethod(Method m) {
        super(m);
    }

    public
    <A extends t_class> IAcceptor<t_accepts> acceptor(final A to) {
        return new IAcceptor<t_accepts>() {

            public
            IAcceptor<t_accepts> set(t_accepts parameter) {
                invoke(to, parameter);
                return this;
            }
        };
    }

    public
    MirrorMethod<t_class, t_returns, t_accepts> dispatchBackward(final ContextTopology<t_class, ?> topology) {
        return new MirrorMethod<t_class, t_returns, t_accepts>(method) {
            Dispatch<t_class, ?> d = new Dispatch(topology);

            @Override
            protected
            Object invoke(t_class to, Object... with) {
                Collection dd = d.dispatchBackward(to, method, with);
                return dd;
            }
        };
    }

    public
    MirrorMethod<t_class, Collection<t_returns>, t_accepts> dispatchForward(final ContextTopology<t_class, ?> topology) {
        return new MirrorMethod<t_class, Collection<t_returns>, t_accepts>(method) {
            Dispatch<t_class, ?> d = new Dispatch(topology);

            @Override
            protected
            Object invoke(t_class to, Object... with) {
                Collection dd = d.dispatchForward(to, method, with);
                return dd;
            }
        };
    }

    public
    <T> MirrorMethod<T, Collection<t_returns>, t_accepts> dispatchForwardOverProxy(final ContextTopology<T, t_class> topology) {
        return new MirrorMethod<T, Collection<t_returns>, t_accepts>(method) {
            Dispatch<T, t_class> d = new Dispatch(topology);

            @Override
            protected
            Object invoke(T to, Object... with) {
                Collection dd = d.dispatchForward(to, method, with);
                return dd;
            }
        };
    }

    public
    <A extends t_class> IFunction<t_accepts, t_returns> function(final A to) {
        return new IFunction<t_accepts, t_returns>() {
            @SuppressWarnings("unchecked")
            public
            t_returns apply(t_accepts in) {
                return (t_returns) invoke(to, in);
            }
        };
    }

    public
    <A extends t_class> IFunction<t_accepts, Collection<? extends t_returns>> function(final Collection<A> to) {
        return new IFunction<t_accepts, Collection<? extends t_returns>>() {
            @SuppressWarnings("unchecked")
            public
            Collection<? extends t_returns> apply(t_accepts in) {
                // fixme,
                // this
                // could
                // be
                // done
                // one
                // stage
                // earlier
                if (to.isEmpty()) return Collections.EMPTY_LIST;
                if (to.size() == 1)
                    return (Collection<? extends t_returns>) Collections.singletonList(invoke(to.iterator().next(),
                                                                                              in));
                ArrayList<t_returns> ret = new ArrayList<t_returns>();
                for (A a : to) {
                    ret.add((t_returns) invoke(a, in));
                }
                return ret;
            }
        };
    }

    public
    <A extends t_class, B extends t_accepts> IUpdateable updateable(final A to, final B with) {
        return new IUpdateable() {

            public
            void update() {
                invoke(to, with);
            }

        };
    }

    public
    <A extends t_class> IUpdateable updateable(final A to, final Object... with) {
        return new IUpdateable() {

            public
            void update() {
                invoke(to, with);
            }
        };
    }

    public
    <A extends t_class, B extends t_accepts> IUpdateable updateable(final Collection<A> to, final B with) {
        return new IUpdateable() {

            public
            void update() {
                for (A a : to)
                    invoke(a, with);
            }

        };
    }

    public
    <A extends t_class> IUpdateable updateable(final Collection<A> to, final Object... with) {
        return new IUpdateable() {

            public
            void update() {
                for (A a : to)
                    invoke(a, with);
            }
        };
    }

    protected
    Object invoke(t_class target, Object... with) {
        try {
            if ((with.length == 1) && (with[0] instanceof Object[]))
                return method.invoke(target, (Object[]) with[0]);
            else return method.invoke(target, with);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
