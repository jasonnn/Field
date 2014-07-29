package field.bytecode.mirror.impl;

import field.bytecode.mirror.IBoundFloatMember;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IDoubleProvider;
import field.math.abstraction.IFloatProvider;
import field.math.abstraction.IProvider;

/**
* Created by jason on 7/29/14.
*/
public
class MirrorFloatMember<t_class> extends MirrorMember<t_class, Float> {

    public
    MirrorFloatMember(Class on, String name) {
        super(on, name, Float.TYPE);
    }

    @Override
    public
    <A extends t_class> IBoundFloatMember boundMember(final A to) {
        final IAcceptor<Float> a = acceptor(to);
        final IProvider<Float> p = provider(to);
        final IFloatProvider f = floatProvider(to);

        return new IBoundFloatMember() {

            public
            float evaluate() {
                return f.evaluate();
            }

            public
            Float get() {
                return p.get();
            }

            public
            IAcceptor<Float> set(Float to) {
                a.set(to);
                return this;
            }

        };
    }

    public
    <A extends t_class> IDoubleProvider doubleProvider(final A to) {
        return new IDoubleProvider() {
            public
            double evaluate() {
                try {
                    return ((Number) field.get(to)).doubleValue();
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        };
    }

    public
    <A extends t_class> IFloatProvider floatProvider(final A to) {
        return new IFloatProvider() {
            public
            float evaluate() {
                try {
                    return ((Number) field.get(to)).floatValue();
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        };
    }

}
