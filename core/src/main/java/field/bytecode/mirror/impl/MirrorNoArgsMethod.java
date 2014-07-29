package field.bytecode.mirror.impl;

import field.bytecode.mirror.IBoundNoArgsMethod;
import field.bytecode.mirror.IMethodFunction;
import field.launch.IUpdateable;
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
class MirrorNoArgsMethod<t_class, t_returns> implements IMethodFunction<t_class, Object, t_returns>,
                                                        IFunction<t_class, t_returns> {
    protected Method method;

    public
    MirrorNoArgsMethod(Class on, String name) {
        method = ReflectionTools.methodOf(name, on);
        method.setAccessible(true);
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

        final IFunction<Object, Collection<? extends t_returns>> ff = function(to);
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
    <A extends t_class> IFunction<Object, t_returns> function(final A to) {
        return new IFunction<Object, t_returns>() {
            @SuppressWarnings("unchecked")
            public
            t_returns apply(Object in) {
                return (t_returns) invoke(to, in);
            }
        };
    }

    public
    <A extends t_class> IFunction<Object, Collection<? extends t_returns>> function(final Collection<A> to) {
        return new IFunction<Object, Collection<? extends t_returns>>() {
            @SuppressWarnings("unchecked")
            public
            Collection<? extends t_returns> apply(Object in) {
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
            if ((with.length == 1) && (with[0] instanceof Object[])) return method.invoke(to, (Object[]) with[0]);
            else return method.invoke(to, with);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
