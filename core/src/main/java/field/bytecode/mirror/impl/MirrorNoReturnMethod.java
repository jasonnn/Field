package field.bytecode.mirror.impl;

import field.bytecode.mirror.IMethodFunction;
import field.launch.IUpdateable;
import field.math.abstraction.IAcceptor;
import field.namespace.context.ContextTopology;
import field.namespace.context.Dispatch;
import field.namespace.generic.IFunction;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by jason on 7/29/14.
 */
public
class MirrorNoReturnMethod<t_class, t_accepts> extends AbstractMirrorMethod implements IMethodFunction<t_class, t_accepts, Void> {


    public
    MirrorNoReturnMethod(Class on, String name, Class...parameters) {
        super(on, name, parameters);
    }

    public
    MirrorNoReturnMethod(Method m) {
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
    <A extends t_class> IAcceptor<t_accepts> acceptor(final Collection<A> to) {
        return new IAcceptor<t_accepts>() {

            public
            IAcceptor<t_accepts> set(t_accepts parameter) {
                for (A a : to)
                    invoke(a, parameter);
                return this;

            }
        };
    }

    public
    MirrorNoReturnMethod<t_class, t_accepts> dispatchBackward(final ContextTopology<t_class, ?> topology) {
        return new MirrorNoReturnMethod<t_class, t_accepts>(method) {
            Dispatch d = new Dispatch(topology);

            @Override
            protected
            <A> Object invoke(A to, Object... with) {
                d.dispatchBackward(to, method, with);
                return null;
            }
        };
    }

    public
    MirrorNoReturnMethod<t_class, t_accepts> dispatchForward(final ContextTopology<t_class, ?> topology) {
        return new MirrorNoReturnMethod<t_class, t_accepts>(method) {
            Dispatch d = new Dispatch(topology);

            @Override
            protected
            <A> Object invoke(A to, Object... with) {
                d.dispatchForward(to, method, with);
                return null;
            }
        };
    }

    @Override
    public
    <A extends t_class> IFunction<t_accepts, Void> function(final A to) {
        return new IFunction<t_accepts, Void>() {
            @SuppressWarnings("unchecked")
            public
            Void apply(t_accepts in) {
                invoke(to, in);
                return null;
            }
        };
    }

    @Override
    public
    <A extends t_class> IFunction<t_accepts, Collection<? extends Void>> function(final Collection<A> to) {
        return new IFunction<t_accepts, Collection<? extends Void>>() {
            @Override
            public
            Collection<? extends Void> apply(t_accepts in) {
                for (A a : to) {
                    invoke(a, in);
                }
                return Collections.emptyList();
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
                    invoke(to, with);
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
            System.err.println(" while invoking <" + method + "> on <" + to + "> with <" + with.length + "> arguments");
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            System.err.println(" while invoking <" + method + "> on <" + to + "> with <" + with.length + "> arguments");
            throw new IllegalArgumentException(e);
        } catch (IllegalArgumentException e) {
            System.err.println(" while invoking <" + method + "> on <" + to + "> with <" + with.length + "> arguments");
            throw new IllegalArgumentException(e);
        }
    }

}
