package field.bytecode.mirror.impl;

import field.bytecode.mirror.IMethodFunction;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.namespace.context.ContextTopology;
import field.namespace.context.Dispatch;
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
class MirrorMethod<OWNER, I, O> extends AbstractMirrorMethod implements IMethodFunction<OWNER, I, O> {
    public
    MirrorMethod(Class on, String name, Class... parameters) {
        super(on, name, parameters);
    }

    public
    MirrorMethod(Method m) {
        super(m);
    }

    public
    <A extends OWNER> IAcceptor<I> acceptor(final A to) {
        return new IAcceptor<I>() {

            public
            IAcceptor<I> set(I parameter) {
                invoke(to, parameter);
                return this;
            }
        };
    }

    public
    <T> MirrorMethod<OWNER, I, O> dispatchBackward(final ContextTopology<OWNER, T> topology) {
        return new MirrorMethod<OWNER, I, O>(method) {
            Dispatch<OWNER, T> d = new Dispatch<OWNER, T>(topology);

            @Override
            protected
            Object invoke(OWNER to, Object... with) {
                return d.dispatchBackward(to, method, with);
            }
        };
    }

    public
    <T> MirrorMethod<OWNER, I, Collection<O>> dispatchForward(final ContextTopology<OWNER, T> topology) {
        return new MirrorMethod<OWNER, I, Collection<O>>(method) {
            Dispatch<OWNER, T> d = new Dispatch<OWNER, T>(topology);

            @Override
            protected
            Object invoke(OWNER to, Object... with) {
                return d.dispatchForward(to, method, with);
            }
        };
    }

    public
    <E> MirrorMethod<E, I, Collection<O>> dispatchForwardOverProxy(final ContextTopology<E, OWNER> topology) {
        return new MirrorMethod<E, I, Collection<O>>(method) {
            Dispatch<E, OWNER> d = new Dispatch<E,OWNER>(topology);

            @Override
            protected
            Object invoke(E to, Object... with) {
                return d.dispatchForward(to, method, with);
            }
        };
    }

    public
    <A extends OWNER> IFunction<I, O> function(final A to) {
        return new IFunction<I, O>() {
            @SuppressWarnings("unchecked")
            public
            O apply(I in) {
                return (O) invoke(to, in);
            }
        };
    }

    public
    <A extends OWNER> IFunction<I, Collection<? extends O>> function(final Collection<A> to) {
        return new IFunction<I, Collection<? extends O>>() {
            @SuppressWarnings("unchecked")
            public
            Collection<? extends O> apply(I in) {
                // fixme,
                // this
                // could
                // be
                // done
                // one
                // stage
                // earlier
                if (to.isEmpty())
                    return Collections.EMPTY_LIST;
                if (to.size() == 1)
                    return (Collection<? extends O>) Collections.singletonList(invoke(to.iterator().next(), in));
                ArrayList<O> ret = new ArrayList<O>();
                for (A a : to) {
                    ret.add((O) invoke(a, in));
                }
                return ret;
            }
        };
    }

    public
    <A extends OWNER, B extends I> IUpdateable updateable(final A to, final B with) {
        return new IUpdateable() {

            public
            void update() {
                invoke(to, with);
            }

        };
    }

    public
    <A extends OWNER> IUpdateable updateable(final A to, final Object... with) {
        return new IUpdateable() {

            public
            void update() {
                invoke(to, with);
            }
        };
    }

    public
    <A extends OWNER, B extends I> IUpdateable updateable(final Collection<A> to, final B with) {
        return new IUpdateable() {

            public
            void update() {
                for (A a : to)
                    invoke(a, with);
            }

        };
    }

    public
    <A extends OWNER> IUpdateable updateable(final Collection<A> to, final Object... with) {
        return new IUpdateable() {

            public
            void update() {
                for (A a : to)
                    invoke(a, with);
            }
        };
    }

    protected
    Object invoke(OWNER target, Object... with) {
        try {
            if ((with.length == 1) && (with[0] instanceof Object[]))
                return method.invoke(target, (Object[]) with[0]);
            else
                return method.invoke(target, with);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
