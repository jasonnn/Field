package field.bytecode.mirror.impl;

import field.bytecode.mirror.IBoundMember;
import field.bytecode.mirror.IMethodMember;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IProvider;
import field.namespace.generic.ReflectionTools;

import java.lang.reflect.Field;

/**
* Created by jason on 7/29/14.
*/
public
class MirrorMember<t_class, t_is> implements IMethodMember<t_class, t_is> {
    protected final Field field;

    public
    MirrorMember(Class on, String name, Class type) {
        field = ReflectionTools.getFirstFIeldCalled(on, name, type);
    }

    public
    <A extends t_class> IAcceptor<t_is> acceptor(final A to) {
        return new IAcceptor<t_is>() {
            public
            IAcceptor<t_is> set(t_is val) {
                try {
                    field.set(to, val);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
                return this;
            }
        };
    }

    public
    <A extends t_class> IBoundMember<t_is> boundMember(final A to) {
        final IAcceptor<t_is> a = acceptor(to);
        final IProvider<t_is> p = provider(to);
        return new IBoundMember<t_is>() {

            public
            t_is get() {
                return p.get();
            }

            public
            IAcceptor<t_is> set(t_is to) {
                a.set(to);
                return this;
            }

        };
    }

    public
    <A extends t_class> IProvider<t_is> provider(final A to) {
        return new IProvider<t_is>() {
            @SuppressWarnings("unchecked")
            public
            t_is get() {
                try {
                    return (t_is) field.get(to);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        };
    }

}
