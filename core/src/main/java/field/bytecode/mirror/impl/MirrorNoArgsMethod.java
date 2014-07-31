package field.bytecode.mirror.impl;

import field.bytecode.mirror.IBoundNoArgsMethod;
import field.bytecode.mirror.IMethodFunction;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by jason on 7/29/14.
 */
public
class MirrorNoArgsMethod<t_class, t_returns> extends AbstractMirrorMethod implements IMethodFunction<t_class, Void, t_returns>,
                                                        IFunction<t_class, t_returns> {


    public
    MirrorNoArgsMethod(Method m) {
        super(m);
    }

    public
    MirrorNoArgsMethod(Class on, String name) {
        this(ReflectionTools.methodOf(name, on));

    }
    //TODO fix processor so this isnt necessary
    public
    <A extends t_class,T> IAcceptor<T> acceptor(final A to) {
        return new IAcceptor<T>() {

            public
            IAcceptor<T> set(T parameter) {
                invoke(to, parameter);
                return this;
            }
        };
    }

    public
    <A extends t_class> IBoundNoArgsMethod<t_returns> bind(final A to) {
        return new IBoundNoArgsMethod<t_returns>() {
            public
            t_returns get() {
                return apply(to);
            }

            public
            void update() {
                apply(to);
            }
        };
    }

    public
    <A extends t_class> IBoundNoArgsMethod<Collection<? extends t_returns>> bind(final Collection<A> to) {

        final IFunction<Void, Collection<? extends t_returns>> ff = function(to);
        return new IBoundNoArgsMethod<Collection<? extends t_returns>>() {
            public
            Collection<? extends t_returns> get() {
                return ff.apply(null);
            }

            public
            void update() {
                ff.apply(null);
            }
        };
    }

    public
    t_returns apply(t_class in) {
        try {
            return (t_returns) method.invoke(in);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public
    <A extends t_class> IFunction<Void, t_returns> function(final A to) {
        return new IFunction<Void, t_returns>() {
            @SuppressWarnings("unchecked")
            public
            t_returns apply(Void in) {
                return (t_returns) invoke(to, in);
            }
        };
    }

    public
    <A extends t_class> IFunction<Void, Collection<? extends t_returns>> function(final Collection<A> to) {
        return new IFunction<Void, Collection<? extends t_returns>>() {
            @SuppressWarnings("unchecked")
            public
            Collection<? extends t_returns> apply(Void in) {
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
    <A extends t_class> IUpdateable updateable(final A to) {
        return new IUpdateable() {

            public
            void update() {
                invoke(to);
            }

        };
    }

    public
    <A extends t_class> IUpdateable updateable(final Collection<A> to) {
        return new IUpdateable() {

            public
            void update() {
                for (A a : to)
                    invoke(a);
            }

        };
    }

    protected
    <A> Object invoke(final A to, final Object... with) {
        try {
            if ((with.length == 1) && (with[0] instanceof Object[]))
                return method.invoke(to, (Object[]) with[0]);
            else
                return method.invoke(to, with);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
