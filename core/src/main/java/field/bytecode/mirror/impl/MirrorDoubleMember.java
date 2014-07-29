package field.bytecode.mirror.impl;

import field.bytecode.mirror.IIBoundDoubleMember;
import field.math.abstraction.IAcceptor;
import field.math.abstraction.IDoubleProvider;
import field.math.abstraction.IFloatProvider;
import field.math.abstraction.IProvider;

/**
* Created by jason on 7/29/14.
*/
public
class MirrorDoubleMember<t_class> extends MirrorMember<t_class, Double> {

    public
    MirrorDoubleMember(Class on, String name) {
        super(on, name, Double.TYPE);
    }

    @Override
    public
    <A extends t_class> IIBoundDoubleMember boundMember(final A to) {
        final IAcceptor<Double> a = acceptor(to);
        final IProvider<Double> p = provider(to);
        final IDoubleProvider f = doubleProvider(to);

        return new IIBoundDoubleMember() {

            public
            double evaluate() {
                return f.evaluate();
            }

            public
            Double get() {
                return p.get();
            }

            public
            IAcceptor<Double> set(Double to) {
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
