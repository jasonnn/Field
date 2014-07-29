package field.bytecode.mirror.impl;

import field.bytecode.mirror.IMethodFunction;
import field.launch.IUpdateable;
import field.namespace.context.ContextTopology;
import field.namespace.context.Dispatch;
import field.namespace.generic.ReflectionTools;
import field.namespace.generic.IFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
* Created by jason on 7/29/14.
*/
public
class MirrorNoReturnNoArgsMethod<t_class> extends AbstractMirrorMethod implements IMethodFunction<t_class, Void, Void> {


    public
    MirrorNoReturnNoArgsMethod(Class on, String name) {
       this(ReflectionTools.methodOf(name, on));
    }

    public
    MirrorNoReturnNoArgsMethod(Method method) {
        super(method);
    }

    public
    MirrorNoReturnNoArgsMethod<t_class> dispatchBackward(final ContextTopology<t_class, ?> topology) {
        return new MirrorNoReturnNoArgsMethod<t_class>(method) {
            Dispatch d = new Dispatch(topology);

            @Override
            protected
            <A> void invoke(A to, Object... with) {
                d.dispatchBackward(to, method, with);
            }
        };
    }

    public
    MirrorNoReturnNoArgsMethod<t_class> dispatchForward(final ContextTopology<t_class, ?> topology) {
        return new MirrorNoReturnNoArgsMethod<t_class>(method) {
            Dispatch d = new Dispatch(topology);

            @Override
            protected
            <A> void invoke(A to, Object... with) {
                d.dispatchForward(to, method, with);
            }
        };
    }

    public
    <A extends t_class> IFunction<Void, Void> function(final A to) {
        return new IFunction<Void, Void>() {
            @SuppressWarnings("unchecked")
            public
            Void apply(Void in) {
                invoke(to);
                return null;
            }
        };
    }

    public
    <A extends t_class> IFunction<Void, Collection<? extends Void>> function(final Collection<A> to) {
        return new IFunction<Void, Collection<? extends Void>>() {
            //@SuppressWarnings("unchecked")
            public
            Collection<Void> apply(Void in) {
                for (A a : to)
                    invoke(a);
                return null;
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
    <A extends t_class> IUpdateable updateable(final A to, final Object... with) {
        return new IUpdateable() {

            public
            void update() {
                invoke(to, with);
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
    <A> void invoke(final A to, final Object... with) {
        try {
            if ((with.length == 1) && (with[0] instanceof Object[])) method.invoke(to, (Object[]) with[0]);
            else method.invoke(to, with);

        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
